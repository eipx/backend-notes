# In-process publish-subscribe bus

## Requirements
- Publishers send an event of some type; every subscriber currently registered for that type receives it.
- Subscribers can subscribe and unsubscribe by type at any time, including from inside a callback.
- One subscriber throwing an exception must not stop delivery to the others, and must not crash the publisher.
- Support both synchronous delivery (call the subscriber on the publisher's thread) and asynchronous delivery (hand off to an executor).

## Core abstractions
- `EventBus`: `publish(event)`, `subscribe(type, subscriber)` returning a `Subscription` used to unsubscribe.
- `Subscriber<T>`: single method `onEvent(T event)`, the callback interface.
- `Subscription`: `unsubscribe()`, a handle so callers are not required to keep the original subscriber reference around.
- `SubscriberErrorHandler`: isolates a failing subscriber from the rest of the delivery loop.

## Java 8 skeleton
```java
public interface EventBus {
    <T> void publish(T event);
    <T> Subscription subscribe(Class<T> type, Subscriber<T> subscriber);
}

public interface Subscriber<T> { void onEvent(T event); }
public interface Subscription { void unsubscribe(); }
public interface SubscriberErrorHandler { void handle(Subscriber<?> subscriber, Object event, Throwable error); }

public class SimpleEventBus implements EventBus {
    private final ConcurrentMap<Class<?>, CopyOnWriteArrayList<Subscriber<?>>> subscribersByType =
            new ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Subscriber<?>>>();
    private final Executor executor;
    private final SubscriberErrorHandler errorHandler;
    public SimpleEventBus(Executor executor, SubscriberErrorHandler errorHandler) {
        this.executor = executor; this.errorHandler = errorHandler;
    }

    @Override
    public <T> Subscription subscribe(final Class<T> type, final Subscriber<T> subscriber) {
        CopyOnWriteArrayList<Subscriber<?>> list = subscribersByType.get(type);
        if (list == null) {
            list = new CopyOnWriteArrayList<Subscriber<?>>();
            CopyOnWriteArrayList<Subscriber<?>> existing = subscribersByType.putIfAbsent(type, list);
            if (existing != null) {
                list = existing;
            }
        }
        list.add(subscriber);
        final CopyOnWriteArrayList<Subscriber<?>> target = list;
        return new Subscription() {
            @Override
            public void unsubscribe() {
                target.remove(subscriber);
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void publish(final T event) {
        CopyOnWriteArrayList<Subscriber<?>> list = subscribersByType.get(event.getClass());
        if (list == null) {
            return;
        }
        for (final Subscriber<?> subscriber : list) {
            Runnable delivery = new Runnable() {
                @Override
                public void run() {
                    try {
                        ((Subscriber<T>) subscriber).onEvent(event);
                    } catch (Throwable t) {
                        errorHandler.handle(subscriber, event, t);
                    }
                }
            };
            if (executor == null) {
                delivery.run();
            } else {
                executor.execute(delivery);
            }
        }
    }
}
```

## Design patterns used and why
The bus itself is the **Observer** pattern: subscribers register interest in a type and the bus notifies them without either side knowing the other's concrete class. Swapping `executor` between `null` (call inline) and a real `ExecutorService` is a **Strategy**: the delivery mechanism changes without touching `publish`'s caller-facing contract. Wrapping each callback in a `Runnable` before dispatch is a small **Command** so the same object can either run immediately or be queued.

## Extension points
A new event type needs no change to `SimpleEventBus`: it is keyed by `Class<?>` at publish time, so any new class works the moment something subscribes to it. A new delivery policy (a per-subscriber executor for stronger ordering, a bounded queue with backpressure) is a new `Executor` implementation passed into the constructor, not a change to the dispatch loop. A new error policy is a new `SubscriberErrorHandler`.

## Concurrency
`CopyOnWriteArrayList` is the right structure here because publish (iteration) is frequent and subscribe or unsubscribe (mutation) is rare; iteration never needs a lock and never throws `ConcurrentModificationException` even if a subscriber unsubscribes itself mid-callback. The cost is that subscribe and unsubscribe copy the whole backing array, fine at the scale of tens or hundreds of subscribers per type but wrong if subscriptions churn constantly. A slow subscriber under synchronous delivery blocks the publisher and every subscriber queued behind it; async delivery through an executor decouples them but drops any ordering guarantee across subscribers unless each one is pinned to its own single-threaded executor.

## Testing approach
Unit test subscribe, publish, and unsubscribe with a counting `Subscriber` that records received events. Test that an exception thrown by one subscriber does not prevent delivery to a second subscriber registered after it, using a fake `SubscriberErrorHandler` that captures the error. Test the slow-subscriber case under async delivery with a `CountDownLatch`: publish, assert the publishing thread returns immediately, then assert the slow subscriber eventually completes. Run a concurrency test that subscribes, publishes, and unsubscribes from multiple threads simultaneously to catch structural races, even though `CopyOnWriteArrayList` should make them safe by construction.

## Follow-up questions
1. Why `CopyOnWriteArrayList` instead of a synchronized `ArrayList`? Reads (publish) never block on a lock; the trade-off is an O(n) copy on every subscribe or unsubscribe, acceptable when reads dominate writes.
2. How do you get ordered delivery to one subscriber under async dispatch? Give that subscriber its own single-threaded executor instead of sharing a pool, so its callbacks run in submission order.
3. How do you avoid leaking subscribers that forget to unsubscribe? Return a `Subscription` handle and make callers responsible for closing it, or tie subscriptions to a lifecycle object (a request scope, a connection) that unsubscribes automatically when it ends.
4. How would wildcard subscriptions across supertypes work? Look up not just `event.getClass()` but also its supertypes and interfaces at publish time, at the cost of a slower dispatch path.
5. What bounds a slow subscriber under async delivery? Give it a bounded queue in front of its executor and a drop or reject policy, plus a metric on queue depth.
6. Sync or async by default? Sync for a small in-process tool where callers want events processed before `publish` returns; async in a service so one slow subscriber cannot stall the publisher.
