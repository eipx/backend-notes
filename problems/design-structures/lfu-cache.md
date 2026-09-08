# LFU Cache
`ref: LC 460` · Difficulty: Hard · Pattern: hash map of frequency buckets (each an ordered set) + a running minimum frequency

## Problem

Design a fixed-capacity cache that, when full, evicts the **least frequently used** entry. "Frequency" is a per-key use counter that increments on every successful `get` and every `put` that touches an existing key (a brand new key starts at frequency 1). If several keys tie for the lowest frequency, evict the one that was used least recently among that tied group.

The class exposes:
- `LFUCache(int capacity)` — capacity may be `0`, in which case the cache never stores anything.
- `int get(int key)` — if present, bump its frequency by 1 and return its value; otherwise return `-1`.
- `void put(int key, int value)` — if `key` exists, overwrite the value and bump its frequency by 1 (this counts as a use). If `key` is new and the cache is at capacity, evict the least-frequently-used (ties broken by least-recently-used) entry first, then insert the new key at frequency 1.

`get` and `put` are both required to run in **O(1)** average time.

## Constraints

- `0 <= capacity <= 10^4` — unlike LRU, capacity 0 is explicitly allowed and must behave as a no-op cache.
- Up to `2 * 10^5` calls to `get`/`put` combined — again rules out any per-call linear scan over all keys or all frequencies.
- Keys and values fit in a 32-bit signed int.
- The combination of "capacity can be 0" and "O(1) required" is the signal: you cannot special-case capacity 0 with a slow path, and you cannot scan frequencies to find the minimum on every call.

## Worked examples

**Example 1 — frequency ties broken by recency**
```
LFUCache(2)
put(1,1) put(2,2) get(1)->1 put(3,3) get(2)->-1 get(3)->3
put(4,4) get(1)->-1 get(3)->3 get(4)->4
```
After `get(1)`, key `1` has frequency 2 and key `2` still has frequency 1. `put(3,3)` needs to evict, and the lowest frequency present is 1, held only by key `2` — so `2` is evicted, matching `get(2) -> -1`. Later, `1` and `3` both sit at frequency 2 when `put(4,4)` needs to evict; `1` was used less recently than `3` (last touched at `get(1)`, before `3` was even inserted), so `1` is evicted even though their frequencies are tied.

**Example 2 — capacity 0**
```
LFUCache(0)
put(1,1) get(1)->-1
```
With capacity 0, `put` is a no-op — nothing is ever stored, so every subsequent `get` returns `-1` regardless of what was "put."

**Example 3 — minFreq must climb when a bucket empties**
```
LFUCache(2)
put(1,1) put(2,2) get(1)->1 get(2)->2 put(3,3) get(1)->-1
```
Both keys start at frequency 1 (`minFreq = 1`). `get(1)` moves key `1` to frequency 2; the frequency-1 bucket still has `{2}`, so `minFreq` stays 1. `get(2)` moves key `2` to frequency 2 as well — now the frequency-1 bucket is empty, so `minFreq` must be advanced to 2. When `put(3,3)` needs to evict, it correctly looks at frequency 2 (not the stale value 1) and evicts whichever of `1`/`2` was least recently touched there, which is `1` (touched first). Hence `get(1)` afterward is `-1`.

## Edge cases checklist

- Capacity 0 — every `put` is a no-op, every `get` is `-1`.
- Capacity 1 — every `put` of a new key evicts the current sole occupant.
- `get` on a key that was never inserted.
- `put` on an existing key — must update the value **and** bump frequency (it counts as a use).
- Two or more keys tied at the lowest frequency — evict the least recently used among them, not an arbitrary one.
- The bucket at `minFreq` becoming empty after a bump — `minFreq` must advance, never stay stale.
- A brand new key always enters at frequency 1, and `minFreq` must reset to 1 whenever a new key is inserted (the new key is now the global least-frequently-used).
- Repeatedly `get`-ing the same key many times in a row (frequency climbing through many buckets, old buckets should be cleaned up so `minFreq` never gets "stuck").

## Approach

### Naive

Store `(key, value, frequency, lastUsedTime)` in a list. On eviction, scan every entry to find the minimum frequency (and among ties, the oldest `lastUsedTime`) — O(n) per eviction and O(n) per `get`/`put` if you also scan to bump frequency by key. With up to `10^4` capacity and `2 * 10^5` calls this is far too slow for the required O(1) average.

### Optimal

**Key invariant:** every key lives in exactly one `frequency -> LinkedHashSet<key>` bucket at all times, the bucket it lives in always equals its true current frequency, and `minFreq` always equals the smallest frequency that has a non-empty bucket.

Three maps do the work:
- `keyToVal: HashMap<Integer,Integer>` — O(1) value lookup.
- `keyToFreq: HashMap<Integer,Integer>` — O(1) frequency lookup.
- `freqToKeys: HashMap<Integer, LinkedHashSet<Integer>>` — for each frequency, the set of keys currently at that frequency, in the order they arrived at that frequency (insertion order of a `LinkedHashSet` gives O(1) "oldest" access via `iterator().next()`).

Every "use" (`get` hit, or `put` on an existing key) removes the key from its current frequency bucket, bumps `keyToFreq`, and re-inserts it at the tail of the next bucket — an O(1) set removal plus an O(1) set insertion. If removing it empties the bucket and that bucket was `minFreq`, `minFreq` increments by exactly 1 (frequencies only ever increase by 1 per use, so the new minimum cannot skip past `minFreq + 1`). Eviction reads the front of the `minFreq` bucket directly — O(1), no scanning.

### Step-by-step trace

Trace of Example 1, `LFUCache(2)`:

| Step | Operation | Return | keyToVal | keyToFreq | freqToKeys | minFreq |
|---|---|---|---|---|---|---|
| 1 | `put(1,1)` | — | `{1:1}` | `{1:1}` | `{1:[1]}` | 1 |
| 2 | `put(2,2)` | — | `{1:1,2:2}` | `{1:1,2:1}` | `{1:[1,2]}` | 1 |
| 3 | `get(1)` | `1` | same | `{1:2,2:1}` | `{1:[2],2:[1]}` | 1 |
| 4 | `put(3,3)` | — evict `2` | `{1:1,3:3}` | `{1:2,3:1}` | `{1:[3],2:[1]}` | 1 |
| 5 | `get(2)` | `-1` | unchanged | unchanged | unchanged | 1 |
| 6 | `get(3)` | `3` | same | `{1:2,3:2}` | `{2:[1,3]}` (freq-1 bucket now empty) | 2 |
| 7 | `put(4,4)` | — evict `1` | `{3:3,4:4}` | `{3:2,4:1}` | `{1:[4],2:[3]}` | 1 |
| 8 | `get(1)` | `-1` | unchanged | unchanged | unchanged | 1 |
| 9 | `get(3)` | `3` | same | `{3:3,4:1}` | `{1:[4],3:[3]}` | 1 |
| 10 | `get(4)` | `4` | same | `{3:3,4:2}` | `{2:[4],3:[3]}` | 2 |

## Java 8 solution

```java
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * LC 460 - fixed-capacity cache that evicts the Least Frequently Used key.
 * On a tie in frequency, evicts the Least Recently Used among the tied keys.
 * get() and put() both run in O(1) average time.
 */
public class LfuCache {

    static class LFUCache {
        private final int capacity;
        private int minFreq;
        private final HashMap<Integer, Integer> keyToVal;
        private final HashMap<Integer, Integer> keyToFreq;
        // LinkedHashSet preserves insertion order, so within one frequency bucket the
        // first element is the least recently used and is the eviction target.
        private final HashMap<Integer, LinkedHashSet<Integer>> freqToKeys;

        LFUCache(int capacity) {
            this.capacity = capacity;
            this.minFreq = 0;
            this.keyToVal = new HashMap<Integer, Integer>();
            this.keyToFreq = new HashMap<Integer, Integer>();
            this.freqToKeys = new HashMap<Integer, LinkedHashSet<Integer>>();
        }

        private void bumpFreq(int key) {
            int freq = keyToFreq.get(key);
            LinkedHashSet<Integer> bucket = freqToKeys.get(freq);
            bucket.remove(key);
            if (bucket.isEmpty()) {
                freqToKeys.remove(freq);
                if (freq == minFreq) minFreq++; // this freq bucket is gone; minFreq must move up
            }
            keyToFreq.put(key, freq + 1);
            LinkedHashSet<Integer> nextBucket = freqToKeys.get(freq + 1);
            if (nextBucket == null) {
                nextBucket = new LinkedHashSet<Integer>();
                freqToKeys.put(freq + 1, nextBucket);
            }
            nextBucket.add(key); // re-inserted at the tail -> most recently used within freq+1
        }

        int get(int key) {
            if (!keyToVal.containsKey(key)) return -1;
            bumpFreq(key);
            return keyToVal.get(key);
        }

        void put(int key, int value) {
            if (capacity <= 0) return;
            if (keyToVal.containsKey(key)) {
                keyToVal.put(key, value);
                bumpFreq(key);
                return;
            }
            if (keyToVal.size() == capacity) {
                LinkedHashSet<Integer> bucket = freqToKeys.get(minFreq);
                int evictKey = bucket.iterator().next(); // oldest entry in the lowest-freq bucket
                bucket.remove(evictKey);
                if (bucket.isEmpty()) freqToKeys.remove(minFreq);
                keyToVal.remove(evictKey);
                keyToFreq.remove(evictKey);
            }
            keyToVal.put(key, value);
            keyToFreq.put(key, 1);
            LinkedHashSet<Integer> bucket = freqToKeys.get(1);
            if (bucket == null) {
                bucket = new LinkedHashSet<Integer>();
                freqToKeys.put(1, bucket);
            }
            bucket.add(key);
            minFreq = 1; // a brand new key always starts at frequency 1
        }
    }
}
```

## Complexity

- `get`: O(1) — two hash map lookups plus one `LinkedHashSet` removal and one insertion (both O(1) average).
- `put`: O(1) — same reasoning; eviction reads `freqToKeys.get(minFreq).iterator().next()` directly instead of scanning.
- Space: O(capacity) — each of the three maps holds at most `capacity` live keys; `freqToKeys` holds at most `capacity` buckets total across all frequencies since every key appears in exactly one bucket.

## Java 8 pitfalls for this problem

- `LinkedHashSet.iterator().next()` gives the oldest-inserted element in O(1); a plain `HashSet` has no defined order and must never be used here — it would make eviction pick an arbitrary tied key instead of the least recently used one.
- `keyToFreq.get(key)` unboxes to `int` automatically, but if `key` is missing this throws `NullPointerException` — always guard with `containsKey` (or check for `null` on the boxed `Integer`) before unboxing.
- `Map.computeIfAbsent` is available in Java 8, but this solution spells out the `get`-then-`put` pattern explicitly for clarity; either is fine, just do not forget `computeIfAbsent`'s lambda is still evaluated once (no double-bucket creation bugs) since it is a stream/lambda feature already available in Java 8's `java.util.Map`.
- `size() == capacity` should be `==`, but writing `>=` is safer defensively; do not confuse this with `size() > capacity`, which would let the cache grow one entry past capacity before evicting.
- No `var`, no records — `LinkedHashSet<Integer>` and `HashMap<Integer, Integer>` generics must be spelled out on both sides of every declaration.
- Nested nested-map values (`HashMap<Integer, LinkedHashSet<Integer>>`) need the inner collection instantiated lazily (`null` check then `new LinkedHashSet<Integer>()`) — Java 8 has no `getOrDefault`-with-side-effect shortcut for "create if absent, mutate, and store back" beyond `computeIfAbsent`.

## Wrong approaches and why they fail

- **Tracking only a single global frequency counter per key with no buckets, and scanning all keys to find the min on eviction.** Sequence: capacity `10^4`, thousands of `put`/`get` calls — this makes every eviction O(capacity), and with `2*10^5` operations the total work can reach `2*10^9`, blowing the O(1)-average requirement.
- **Using a `TreeMap<Integer, List<Integer>>` keyed by frequency instead of buckets with a tracked `minFreq`.** This technically works but makes every frequency bump an O(log capacity) `TreeMap` operation instead of O(1) — it passes correctness tests but violates the strict O(1) requirement, and also needs a `List` (not O(1) removal by value) or a `LinkedHashSet` per bucket, so the direct `minFreq`-tracking approach is strictly better at the same code complexity.
- **Forgetting to advance `minFreq` when its bucket becomes empty.** Sequence: `put(1,1)`, `put(2,2)`, `get(1)`, `get(2)` — after both bumps to frequency 2, the frequency-1 bucket is empty but a broken implementation leaves `minFreq = 1`. The next `put` at capacity then does `freqToKeys.get(1)` and gets `null` (or an empty bucket), crashing or evicting nothing, instead of correctly consulting frequency 2.

## Variants

- **TTL expiry.** Store an insertion/refresh timestamp alongside `keyToVal`; on `get`, check whether the entry has expired and, if so, remove it from `keyToVal`/`keyToFreq`/its bucket before treating the call as a miss, in addition to running normal LFU eviction for capacity.
- **Thread safety.** A single lock around all three maps and `minFreq` is the simplest correct option, since a `get` mutates state (frequency bump) just like `put` does — this is not a read-mostly structure, so sharding is harder to get right than for LRU; most production LFU caches accept a single striped lock rather than true lock-freedom.
- **Frequency decay over time.** Periodically halve every key's stored frequency (and rebuild the buckets) so that keys popular long ago do not permanently outrank keys that are newly popular; this trades strict correctness of "true lifetime frequency" for adaptability to changing access patterns.

## Test cases

| # | Operation sequence | Expected outputs | What it tests |
|---|---|---|---|
| 1 | `put(1,1) put(2,2) get(1) put(3,3) get(2) get(3) put(4,4) get(1) get(3) get(4)` cap=2 | `-,-,1,-,-1,3,-,-1,3,4` | Frequency ties broken by recency |
| 2 | `put(1,1) get(1)` cap=0 | `-,-1` | Capacity 0 is a permanent no-op |
| 3 | `put(1,1) put(2,2) get(1) put(3,3) get(2)` cap=1 | `-,-,-1,-,-1` | Capacity 1, every put replaces the slot |
| 4 | `get(9) put(9,90) get(9)` cap=2 | `-1,-,90` | Get on a never-inserted key |
| 5 | `put(1,1) put(2,2) put(1,100) put(3,3) get(2) get(1)` cap=2 | `-,-,-,-,-1,100` | Put on existing key both updates value and bumps frequency |
| 6 | `put(1,1) put(2,2) get(1) get(2) put(3,3) get(1)` cap=2 | `-,-,1,2,-,-1` | minFreq climbs after its bucket empties |

Randomization note: this problem has no randomized methods, so every expected value above is exact.
