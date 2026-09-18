# Logging framework

## Requirements
- Multiple levels (trace through error), multiple sinks (console, file, network) active at once, per-logger level and sink configuration.
- Filtering before a record reaches a sink, and formatting that can differ per sink.
- Appending must not block the caller's request thread under normal load; buffered records must not be lost on a clean shutdown.

## Core abstractions
- `LogLevel`: ordered severities.
- `LogRecord`: an immutable snapshot of one log event (logger name, level, message, throwable, timestamp).
- `LogFilter`: decides whether a record proceeds.
- `LogFormatter`: turns a record into a string for a specific sink.
- `LogAppender`: writes a record somewhere; `CompositeAppender` fans a record out to several; `AsyncAppender` decouples the caller from a slow appender with a bounded queue.
- `ConfiguredLogger`: the per-name entry point that owns a level, a filter chain, and an appender.

## Java 8 skeleton
```java
public enum LogLevel { TRACE, DEBUG, INFO, WARN, ERROR }

public final class LogRecord {
    public final String loggerName;
    public final LogLevel level;
    public final String message;
    public final Throwable throwable;
    public final long timestampMillis;
    public LogRecord(String loggerName, LogLevel level, String message, Throwable throwable) {
        this.loggerName = loggerName; this.level = level; this.message = message;
        this.throwable = throwable; this.timestampMillis = System.currentTimeMillis();
    }
}

public interface LogFilter { boolean accept(LogRecord record); }
public interface LogFormatter { String format(LogRecord record); }
public interface LogAppender { void append(LogRecord record); void flush(); void close(); }

public class CompositeAppender implements LogAppender {
    private final List<LogAppender> appenders;
    public CompositeAppender(List<LogAppender> appenders) { this.appenders = appenders; }
    public void append(LogRecord r) { for (LogAppender a : appenders) a.append(r); }
    public void flush() { for (LogAppender a : appenders) a.flush(); }
    public void close() { for (LogAppender a : appenders) a.close(); }
}

public class AsyncAppender implements LogAppender {
    private final LogAppender delegate;
    private final BlockingQueue<LogRecord> queue;
    private final boolean dropWhenFull;
    private volatile boolean running = true;
    public AsyncAppender(LogAppender delegate, int capacity, boolean dropWhenFull) {
        this.delegate = delegate; this.queue = new ArrayBlockingQueue<LogRecord>(capacity); this.dropWhenFull = dropWhenFull;
        Thread worker = new Thread(new Runnable() { public void run() { drainLoop(); } }, "async-appender");
        worker.setDaemon(true); worker.start();
    }

    public void append(LogRecord record) {
        if (dropWhenFull) { try { queue.add(record); } catch (IllegalStateException e) { /* full: record is dropped */ } }
        else { try { queue.put(record); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } }
    }

    private void drainLoop() {
        while (running || !queue.isEmpty()) {
            try {
                LogRecord record = queue.poll(200, TimeUnit.MILLISECONDS);
                if (record != null) delegate.append(record);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    public void shutdown() { running = false; flush(); close(); }
    public void flush() { delegate.flush(); }
    public void close() { delegate.close(); }
}

public class ConfiguredLogger {
    private final String name;
    private final LogLevel minLevel;
    private final List<LogFilter> filters;
    private final LogAppender appender;
    public ConfiguredLogger(String name, LogLevel minLevel, List<LogFilter> filters, LogAppender appender) {
        this.name = name; this.minLevel = minLevel; this.filters = filters; this.appender = appender;
    }

    public void log(LogLevel level, String message, Throwable throwable) {
        if (level.ordinal() < minLevel.ordinal()) return;
        LogRecord record = new LogRecord(name, level, message, throwable);
        for (LogFilter filter : filters) if (!filter.accept(record)) return;
        appender.append(record);
    }
}
```

## Design patterns used and why
`CompositeAppender` is the **Composite** pattern: it fans one record out to every configured sink, and the caller cannot tell a single appender from a group of them. Filtering is closer to **Chain of Responsibility**: each `LogFilter` gets a chance to veto a record before it reaches formatting or appending, and the chain stops at the first rejection. `AsyncAppender` wraps a `LogAppender` the way a **Decorator** wraps a component, adding buffering without changing the interface the rest of the system depends on.

## Extension points
A new sink (a network appender, a metrics-emitting appender) is a new `LogAppender` implementation added to a `CompositeAppender`'s list; no existing class changes. A new filter (rate limiting a noisy log line, redacting a field) is a new `LogFilter` appended to the chain. A new output shape is a new `LogFormatter` handed to the appender that needs it. None of this touches `ConfiguredLogger`.

## Concurrency
`AsyncAppender` is the only place concurrency is hard: a bounded `BlockingQueue` separates the caller's thread from the slower sink thread. Under a burst, `dropWhenFull` chooses between blocking the caller (safe, but a log storm can stall the request path) and dropping the record (keeps the application responsive, loses data); most setups drop `DEBUG` and `INFO` and block, or reserve capacity, for `WARN` and `ERROR`. On shutdown, a JVM shutdown hook should stop new writes, drain what remains with a bounded wait, then flush and close every delegate, so a clean exit does not lose buffered records.

## Testing approach
Unit test `ConfiguredLogger.log` with a fake filter that rejects and one that accepts, confirming the appender is only called when every filter passes. Test `CompositeAppender` fans out to all delegates and that one delegate throwing does not stop the others (wrap each delegate call and record failures separately). Test `AsyncAppender` under both policies: fill the queue past capacity and assert either blocking (a producer thread parks) or dropping (a counted metric increments, the caller never blocks). Test shutdown drains and flushes: enqueue records, call `shutdown`, and assert every one of them reached the delegate.

## Follow-up questions
1. Why composite for sinks instead of chain of responsibility? Sinks are independent fan-out targets (console and file both want every matching record), not a pick-one decision; chain of responsibility fits the filter step, where any one filter can stop the record.
2. What breaks if the async queue is unbounded? Memory grows without limit under sustained overload, turning a slow sink into an out-of-memory error instead of a bounded, visible drop rate.
3. How do you keep error logs from ever dropping while letting debug logs drop? Give error-level records a reserved slice of queue capacity, or route them through a second queue with a blocking policy.
4. How does per-logger configuration inherit? Look up the exact logger name first, then walk up the package hierarchy to the nearest ancestor with configuration, the way `com.foo.bar` falls back to `com.foo` then `com`.
5. How do you test that shutdown does not lose data under real load? Push records continuously from a background thread, call shutdown mid-stream, and assert the total appended plus dropped equals the total submitted.
6. Why make `LogRecord` immutable? It crosses a thread boundary (caller thread to the async worker thread); immutability means no synchronization is needed to read it safely on the other side.
