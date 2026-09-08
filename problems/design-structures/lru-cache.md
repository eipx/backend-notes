# LRU Cache
`ref: LC 146` · Difficulty: Medium · Pattern: hash map + doubly linked list (recency ordering)

## Problem

Design a fixed-capacity key/value cache that discards the **least recently used** entry whenever it is full and a new key needs to be added. "Used" means either read or written.

The class exposes:
- `LRUCache(int capacity)` — construct a cache that can hold at most `capacity` key/value pairs.
- `int get(int key)` — if `key` exists, return its value and mark it as just-used; otherwise return `-1`.
- `void put(int key, int value)` — if `key` already exists, overwrite its value and mark it as just-used. If `key` is new and the cache is already at `capacity`, evict the least recently used entry first, then insert the new pair as the most recently used.

Both `get` and `put` are required to run in **O(1)** average time.

## Constraints

- `1 <= capacity <= 3000` — capacity is always positive; you never need to special-case capacity 0 for this problem (unlike LFU below).
- Up to `2 * 10^5` calls to `get`/`put` combined — this rules out any per-call linear scan (an ArrayList "find and move to front" scan is out).
- Keys and values fit in a 32-bit signed int — no overflow tricks needed here.
- The huge call count is the real signal: it means the intended solution is O(1) per call, not O(log n) or O(capacity).

## Worked examples

**Example 1 — basic eviction**
```
LRUCache(2)
put(1,1) put(2,2) get(1)->1 put(3,3) get(2)->-1 put(4,4) get(1)->-1 get(3)->3 get(4)->4
```
After `put(1,1)`, `put(2,2)` the recency order (most-recent first) is `[2,1]`. `get(1)` returns `1` and moves `1` to the front, giving `[1,2]`. `put(3,3)` needs room, so it evicts the back of the list, which is `2` — that is why `get(2)` afterward is `-1`. The cache now holds `{1,3}`.

**Example 2 — capacity 1**
```
LRUCache(1)
put(1,10) get(1)->10 put(2,20) get(1)->-1 get(2)->20
```
With capacity 1 every `put` on a different key evicts the only occupant. `get(1)` after `put(2,20)` is `-1` because `1` was evicted the instant `2` was inserted — there is no room for both.

**Example 3 — put on an existing key refreshes recency**
```
LRUCache(2)
put(1,1) put(2,2) put(1,100) put(3,3) get(2)->-1 get(1)->100
```
`put(1,100)` does not add a new entry (key `1` already exists) — it updates the value **and** moves `1` to most-recently-used. So right before `put(3,3)`, the recency order is `[1,2]` (1 most recent). `put(3,3)` evicts the back, which is now `2`, not `1`. This is the step people get wrong: they update the value but forget to also treat it as a "use."

## Edge cases checklist

- Capacity 1 (every put/get on a different key evicts immediately).
- `get` on a key that was never inserted.
- `get` on a key that existed but was already evicted.
- `put` on an existing key — must refresh recency, not just overwrite the value.
- `put` on a new key when the cache is exactly full (off-by-one at the capacity boundary).
- Repeated `put` on the *same* key many times in a row — must never grow past capacity.
- `get` itself counts as a use and must reorder, even though it looks read-only.
- Calling `get`/`put` with the same key back-to-back.

## Approach

### Naive

Keep a `List<int[]>` of `(key, value)` pairs in recency order. `get` scans the list for the key (O(n)), then removes and re-inserts it at the front (O(n) shift). `put` does the same lookup, and eviction is a removal from the back (O(1) if it is a true linked structure, O(n) if it is an ArrayList needing a shift). Overall O(n) per call — with up to `2 * 10^5` calls this can degrade toward O(n²) total, which is too slow for the intended bound of O(1) average per call.

### Optimal

**Key invariant:** a hash map gives O(1) key lookup, and a doubly linked list gives O(1) removal/insertion at arbitrary positions once you have a direct node reference — combining them turns "find, then move to front" into O(1) total. The two sentinel nodes (a permanent `head` and `tail`) remove every null-check for the boundary conditions of an empty list or a single-element list.

- `get(key)`: hash map lookup gives the node directly (O(1)); unlink it from wherever it sits and relink it right after `head` (O(1) pointer surgery).
- `put(key, value)` on an existing key: same lookup + move-to-front, plus overwrite the value.
- `put(key, value)` on a new key at capacity: read `tail.prev` (O(1) — that is exactly the least-recently-used node because the list is kept in recency order at all times), unlink it, remove it from the map, then insert the new node at the front and add it to the map.

Every operation touches a constant number of pointers and does one map access, so each call is O(1).

### Step-by-step trace

Trace of Example 1, `LRUCache(2)`, shown as recency order **most-recent-first** (equivalent to reading `head -> tail`):

| Step | Operation | Return | List (MRU -> LRU) | Map keys |
|---|---|---|---|---|
| 1 | `put(1,1)` | — | `[1]` | `{1}` |
| 2 | `put(2,2)` | — | `[2,1]` | `{1,2}` |
| 3 | `get(1)` | `1` | `[1,2]` | `{1,2}` |
| 4 | `put(3,3)` | — | `[3,1]` (evicted `2`) | `{1,3}` |
| 5 | `get(2)` | `-1` | `[3,1]` (unchanged) | `{1,3}` |
| 6 | `put(4,4)` | — | `[4,3]` (evicted `1`) | `{3,4}` |
| 7 | `get(1)` | `-1` | `[4,3]` (unchanged) | `{3,4}` |
| 8 | `get(3)` | `3` | `[3,4]` | `{3,4}` |
| 9 | `get(4)` | `4` | `[4,3]` | `{3,4}` |

## Java 8 solution

```java
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LC 146 - two implementations of a fixed-capacity cache that evicts the
 * least recently used entry once it is full:
 *   1) LRUCacheLinkedHashMap - relies on LinkedHashMap's built-in access-order mode
 *   2) LRUCacheManual        - HashMap + doubly linked list with two sentinel nodes
 */
public class LruCache {

    interface ICache {
        int get(int key);
        void put(int key, int value);
    }

    // ---------------------------------------------------------------
    // Version A: LinkedHashMap with accessOrder = true
    // ---------------------------------------------------------------
    static class LRUCacheLinkedHashMap implements ICache {
        private final int capacity;
        private final LinkedHashMap<Integer, Integer> map;

        LRUCacheLinkedHashMap(int capacity) {
            this.capacity = capacity;
            // The 3-arg constructor (initialCapacity, loadFactor, accessOrder) is required.
            // accessOrder = true reorders the internal linked list on every get() AND put()
            // that touches an existing key, so the head is always least-recently-used.
            // The anonymous-subclass diamond below is NOT allowed pre Java 9, so the
            // generic types are spelled out on both sides.
            this.map = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                    // This method is protected and is invoked BY the map itself, right
                    // after a put(), never by our own code.
                    return size() > capacity;
                }
            };
        }

        @Override
        public int get(int key) {
            Integer value = map.get(key); // get() also counts as an access, reorders the list
            return value == null ? -1 : value;
        }

        @Override
        public void put(int key, int value) {
            // Putting an existing key updates its value AND moves it to most-recently-used,
            // because accessOrder = true makes put() on an existing key an access too.
            map.put(key, value);
        }
    }

    // ---------------------------------------------------------------
    // Version B: hand-rolled HashMap + doubly linked list, two sentinels
    // ---------------------------------------------------------------
    static class LRUCacheManual implements ICache {
        private static class Node {
            int key, value;
            Node prev, next;
            Node(int key, int value) { this.key = key; this.value = value; }
        }

        private final int capacity;
        private final HashMap<Integer, Node> map;
        private final Node head; // sentinel; head.next is the most-recently-used real node
        private final Node tail; // sentinel; tail.prev is the least-recently-used real node

        LRUCacheManual(int capacity) {
            this.capacity = capacity;
            this.map = new HashMap<Integer, Node>();
            head = new Node(-1, -1);
            tail = new Node(-1, -1);
            head.next = tail;
            tail.prev = head;
        }

        private void unlink(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        private void insertAtFront(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }

        @Override
        public int get(int key) {
            Node node = map.get(key);
            if (node == null) return -1;
            unlink(node);
            insertAtFront(node);
            return node.value;
        }

        @Override
        public void put(int key, int value) {
            Node existing = map.get(key);
            if (existing != null) {
                existing.value = value; // put on an existing key must refresh recency, not just value
                unlink(existing);
                insertAtFront(existing);
                return;
            }
            if (map.size() == capacity) {
                Node lru = tail.prev;
                unlink(lru);
                map.remove(lru.key);
            }
            Node fresh = new Node(key, value);
            map.put(key, fresh);
            insertAtFront(fresh);
        }
    }
}
```

## Complexity

- `get`: O(1) — one hash map lookup plus a constant number of pointer updates (or, for the LinkedHashMap version, one internal map operation).
- `put`: O(1) — same reasoning; eviction reads `tail.prev` directly instead of scanning.
- Space: O(capacity) — the map and the linked list each hold at most `capacity` entries; the two sentinel nodes are O(1) extra.

## Java 8 pitfalls for this problem

- `new LinkedHashMap<>(cap, 0.75f, true)` needs all three arguments — the 1-arg and 2-arg constructors default to insertion order (`accessOrder = false`), which silently turns this into an insertion-order cache instead of an LRU cache.
- `removeEldestEntry` is `protected` and is called automatically by `LinkedHashMap` right after `put()` — it is not something your code ever calls directly; calling `map.removeEldestEntry(...)` yourself does not compile because the method is not visible/does not exist as a public API you invoke.
- Subclassing `LinkedHashMap` anonymously with the diamond operator (`new LinkedHashMap<>(...) { ... }`) only compiles on Java 9+; on Java 8 you must repeat the generic types explicitly: `new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) { ... }`.
- `map.get(key)` returns a boxed `Integer`; compare it to `null` to detect a miss, never assume `0` means missing, and never compare boxed values with `==` (only safe for the cached `-128..127` range).
- `HashMap`/`LinkedHashMap` generics need boxed `Integer`, not primitive `int` — auto-boxing happens on every `put`/`get`, which is part of why raw arrays can outperform this approach for extremely hot paths, but is irrelevant to the required complexity.
- The manual version's `Node` class must be `static` (a non-static inner class silently captures an implicit outer-class reference to `LRUCacheManual`, wasting memory and being an easy mistake to introduce).
- No `var`, no records — the `Node` fields and constructor must be written out in full Java 8 syntax.

## Wrong approaches and why they fail

- **Insertion-order `LinkedHashMap` (2-arg constructor, or default `accessOrder=false`).** Sequence: `put(1,1)`, `put(2,2)`, `get(1)`, `put(3,3)` with capacity 2. With `accessOrder=false`, `get(1)` does not move `1` to the end, so `removeEldestEntry` still evicts `1` (the insertion-order-oldest) instead of `2` (the actually-least-recently-used) — wrong eviction target.
- **ArrayList of pairs with linear scan.** Works correctly but each `get`/`put` is O(capacity) to find the key and O(capacity) to shift it to the front. With `2 * 10^5` calls and capacity up to `3000`, this can reach `6 * 10^8` element touches — far past the intended O(1)-per-call budget.
- **Only updating the value on `put` for an existing key, forgetting to move it to the front.** Sequence: `put(1,1)`, `put(2,2)`, `put(1,100)`, `put(3,3)`, `get(2)`. If `put(1,100)` does not refresh recency, the implementation thinks `1` is still the least-recently-used and evicts it instead of `2` when `put(3,3)` runs, making `get(2)` wrongly return `2` instead of `-1`.

## Variants

- **TTL expiry.** Store an expiration timestamp alongside each value; on `get`, if the entry is expired, treat it as a miss and lazily evict it (and unlink it) before returning `-1`, in addition to the normal LRU eviction path.
- **Thread safety.** Wrap all public methods in a single lock (`synchronized` or a `ReentrantLock`) since both the map and the linked-list pointers must move together atomically; a lock-free version generally needs sharding (multiple independent LRU segments keyed by `key.hashCode() % segments`) to reduce contention, trading perfect global LRU ordering for throughput.
- **Capacity change at runtime.** Add a `setCapacity(int newCapacity)` method; if the new capacity is smaller than the current size, evict from `tail.prev` in a loop until `map.size() <= newCapacity`, then update the stored `capacity` field used by future `put` calls.

## Test cases

| # | Operation sequence | Expected outputs | What it tests |
|---|---|---|---|
| 1 | `put(1,1) put(2,2) get(1) put(3,3) get(2) put(4,4) get(1) get(3) get(4)` cap=2 | `-,-,1,-,-1,-,-1,3,4` | Classic eviction chain |
| 2 | `put(1,10) get(1) put(2,20) get(1) get(2)` cap=1 | `-,10,-,-1,20` | Capacity 1, every put evicts |
| 3 | `get(5) put(5,50) get(5)` cap=2 | `-1,-,50` | Get on a never-inserted key |
| 4 | `put(1,1) put(2,2) put(1,100) put(3,3) get(2) get(1)` cap=2 | `-,-,-,-,-1,100` | Put on existing key refreshes recency |
| 5 | `put(1,1) put(2,2) get(1) put(3,3) get(2) get(1)` cap=2 | `-,-,1,-,-1,1` | Get itself refreshes recency |
| 6 | `put(7,1) put(7,2) put(7,3) get(7)` cap=1 | `-,-,-,3` | Repeated overwrite of the same key never overflows capacity |

Randomization note: this problem has no randomized methods, so every expected value above is exact.
