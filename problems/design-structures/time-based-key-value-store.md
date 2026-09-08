# Time Based Key-Value Store
`ref: LC 981` · Difficulty: Medium · Pattern: per-key sorted versions + floor lookup (TreeMap, or ArrayList + binary search)

## Problem

Design a key/value store where each key can hold many versions of a value, each version tagged with a timestamp. Reading a key at a given timestamp should return the most recent version whose timestamp does not exceed the requested one — effectively "what was this key's value at (or just before) this moment in time."

The class exposes:
- `void set(String key, String value, int timestamp)` — record a new version of `key` at `timestamp`. Timestamps for a given key are guaranteed to be strictly increasing across successive `set` calls on that key (this problem's stated guarantee — see Constraints).
- `String get(String key, int timestamp)` — return the value stored under `key` at the largest recorded timestamp `<= timestamp`. If `key` was never set, or every recorded timestamp for `key` is greater than the requested one, return `""`.

Both operations should be efficient per call relative to the number of versions stored for a key — `set` should be O(1) (or O(log n) at worst), and `get` should be O(log n) in the number of versions recorded for that key.

## Constraints

- `1 <= key.length, value.length <= 100`, both consist of lowercase English letters — irrelevant to the algorithm, but rules out needing to handle empty strings for a valid key/value.
- `1 <= timestamp <= 10^7`.
- All the timestamps of `set` calls **for the same key** are strictly increasing across calls — this guarantee is exactly what makes the ArrayList + binary search version valid without any insertion-sort step; if it were violated, that version would need to insert at the correct sorted position instead of always appending.
- At most `2 * 10^5` calls to `set` and `get` combined — an O(n) scan per `get` (checking every version of a key one by one) would be too slow in the worst case where one key accumulates most of the versions.

## Worked examples

**Example 1 — single version, queried before and after it**
```
set("foo","bar",1)
get("foo",1) -> "bar"
get("foo",2) -> "bar"
```
There is only one recorded version of `"foo"`, at timestamp 1. Any query at timestamp `>= 1` finds that same version, since there is nothing more recent to prefer.

**Example 2 — two versions, exact match vs floor**
```
set("foo","bar",1) set("foo","bar2",4)
get("foo",4) -> "bar2"   get("foo",5) -> "bar2"   get("foo",3) -> "bar"
```
`get("foo",4)` lands exactly on the second version's timestamp — an exact match counts as `<=` and is returned. `get("foo",5)` has no exact match but the largest timestamp `<= 5` is still 4, so it also returns `"bar2"`. `get("foo",3)` falls between the two recorded timestamps, so it returns the *earlier* version (`"bar"`), not `""` and not the later one.

**Example 3 — query before the very first recorded timestamp**
```
set("k","v",10)
get("k",5) -> ""
```
Timestamp 5 is before the only recorded version at 10, so there is no timestamp `<= 5` to return — the answer is `""`, not the first version and not an error.

## Edge cases checklist

- `get` on a key that was never `set` at all.
- `get` with a timestamp strictly before the earliest recorded timestamp for a key that does exist (must return `""`, not the earliest version).
- `get` with a timestamp exactly equal to a recorded timestamp (exact match must be returned, not the previous version).
- `get` with a timestamp larger than every recorded timestamp for a key (must return the *latest* version).
- Only one version recorded for a key.
- Many versions recorded for one key, forcing a real binary search rather than a linear scan to stay within budget.
- Two different keys must never leak values into each other's queries.
- (If the strictly-increasing guarantee were ever relaxed) equal or out-of-order timestamps for the same key — see the Approach section for which of the two implementations tolerates this and which does not.

## Approach

### Naive

Store every `(timestamp, value)` pair for a key in an unsorted list (or store globally and filter by key). `get` scans every version of the key, tracking the best `(timestamp <= target)` seen so far — O(versions for that key) per call. With up to `2 * 10^5` total calls and a single hot key accumulating most of the `set` calls, this can approach O(n^2) total, too slow for the intended O(log n)-per-`get` target.

### Optimal

**Key invariant:** for each key, its versions are always kept in ascending timestamp order, so "the largest timestamp `<= target`" is a **floor** query on a sorted sequence, answerable in O(log n) either by a self-balancing tree structure or by binary search over a sorted array.

**Version A — `HashMap<String, TreeMap<Integer,String>>`.** A `TreeMap` keeps its keys (the timestamps for one key string) sorted automatically no matter what order `set` inserts them in, and exposes `floorEntry(timestamp)` directly — the exact operation this problem needs, in one call, O(log n).

**Version B — `HashMap<String, List<Integer>/List<String>>` (parallel arrays) + manual binary search.** Because the problem guarantees timestamps for one key arrive in strictly increasing order, simply appending to the end of a list keeps it sorted "for free," and a manual binary search for the rightmost index with `timestamp <= target` gives the same O(log n) floor lookup without needing a tree structure at all. If that ordering guarantee did **not** hold, this version would break (appending an out-of-order timestamp violates the sortedness the binary search depends on), and the timestamp would need to be inserted at its correct sorted position instead — at which point the `TreeMap` version is simply the easier, more robust choice, since a `TreeMap` sorts regardless of insertion order.

### Step-by-step trace

Trace of Example 2's key `"foo"`, `TreeMap` version, showing `store.get("foo")`'s contents:

| Step | Operation | Return | TreeMap for "foo" |
|---|---|---|---|
| 1 | `set("foo","bar",1)` | — | `{1: "bar"}` |
| 2 | `set("foo","bar2",4)` | — | `{1: "bar", 4: "bar2"}` |
| 3 | `get("foo",4)` | `"bar2"` | `floorEntry(4)` = key 4 exactly |
| 4 | `get("foo",5)` | `"bar2"` | `floorEntry(5)` = key 4 (largest `<= 5`) |
| 5 | `get("foo",3)` | `"bar"` | `floorEntry(3)` = key 1 (largest `<= 3`) |

Binary-search version for the same key, backed by `times=[1,4]`, `values=["bar","bar2"]`: `get("foo",3)` searches `[1,4]`, finds `times[0]=1 <= 3` is a possible match, `times[1]=4 <= 3` is false, so the answer index stays `0` -> `"bar"`.

## Java 8 solution

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * LC 981 - a key/value store where each key can hold many (timestamp, value) versions.
 * get(key, timestamp) must return the value stored at the largest recorded timestamp
 * that is less than or equal to the requested timestamp, or "" if none qualifies.
 * Two implementations: a TreeMap-per-key version, and an ArrayList + binary search version.
 */
public class TimeBasedKeyValueStore {

    interface ITimeMap {
        void set(String key, String value, int timestamp);
        String get(String key, int timestamp);
    }

    // ---------------------------------------------------------------
    // Version A: HashMap<String, TreeMap<Integer,String>> + floorEntry
    // ---------------------------------------------------------------
    static class TimeMapTreeMap implements ITimeMap {
        // The variable type must be TreeMap/NavigableMap, not plain Map,
        // because floorEntry/floorKey are not on the Map interface.
        private final HashMap<String, TreeMap<Integer, String>> store;

        TimeMapTreeMap() {
            store = new HashMap<String, TreeMap<Integer, String>>();
        }

        @Override
        public void set(String key, String value, int timestamp) {
            TreeMap<Integer, String> versions = store.get(key);
            if (versions == null) {
                versions = new TreeMap<Integer, String>();
                store.put(key, versions);
            }
            versions.put(timestamp, value); // TreeMap keeps sorted order regardless of insertion order
        }

        @Override
        public String get(String key, int timestamp) {
            NavigableMap<Integer, String> versions = store.get(key);
            if (versions == null) return "";
            Map.Entry<Integer, String> floor = versions.floorEntry(timestamp); // largest key <= timestamp
            return floor == null ? "" : floor.getValue();
        }
    }

    // ---------------------------------------------------------------
    // Version B: HashMap<String, parallel ArrayLists> + manual binary search
    // Assumes timestamps for a given key arrive in non-decreasing order (as LC 981
    // guarantees); see .md for what breaks if that assumption does not hold.
    // ---------------------------------------------------------------
    static class TimeMapBinarySearch implements ITimeMap {
        private final HashMap<String, List<Integer>> keyToTimes;
        private final HashMap<String, List<String>> keyToValues;

        TimeMapBinarySearch() {
            keyToTimes = new HashMap<String, List<Integer>>();
            keyToValues = new HashMap<String, List<String>>();
        }

        @Override
        public void set(String key, String value, int timestamp) {
            List<Integer> times = keyToTimes.get(key);
            List<String> values = keyToValues.get(key);
            if (times == null) {
                times = new ArrayList<Integer>();
                values = new ArrayList<String>();
                keyToTimes.put(key, times);
                keyToValues.put(key, values);
            }
            times.add(timestamp);
            values.add(value);
        }

        @Override
        public String get(String key, int timestamp) {
            List<Integer> times = keyToTimes.get(key);
            if (times == null || times.isEmpty()) return "";
            List<String> values = keyToValues.get(key);
            int lo = 0, hi = times.size() - 1, resultIdx = -1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                if (times.get(mid) <= timestamp) {
                    resultIdx = mid; // possible match found, keep searching to the right for a closer one
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
            return resultIdx == -1 ? "" : values.get(resultIdx);
        }
    }
}
```

## Complexity

- `set` (TreeMap version): O(log n) where n is the number of versions already stored for that key, since `TreeMap.put` is a red-black tree insertion.
- `set` (binary search version): O(1) amortized — plain `ArrayList.add` at the end.
- `get` (both versions): O(log n) — one `floorEntry` call on a red-black tree, or one manual binary search over a sorted array.
- Space: O(total versions stored across all keys) — every `set` call adds exactly one version to exactly one key's structure.

## Java 8 pitfalls for this problem

- `TreeMap.floorEntry` / `floorKey` are declared on `NavigableMap` (which `TreeMap` implements), not on the plain `Map` interface — a variable declared as `Map<Integer,String>` will not compile against `.floorEntry(...)`; it must be declared as `TreeMap<Integer,String>` or `NavigableMap<Integer,String>`.
- `floorEntry` returns `null` (not an exception) when no key qualifies — always null-check before calling `.getValue()`.
- String comparison must use `.equals(...)`, never `==`, when checking returned values against expected results in tests (string literals happen to often be interned and compare `==` equal in simple cases, but relying on that is a bug waiting to happen, especially once strings are built via concatenation).
- The binary search version's correctness depends entirely on the list staying sorted; it silently produces wrong answers (not a crash) if a `set` call for the same key ever arrived with a smaller timestamp than a previous one, since the code only appends and never checks or re-sorts.
- No `var`, no `List.of` — `ArrayList<Integer>` and `ArrayList<String>` must be constructed and typed explicitly; the two parallel lists per key must be created together (both `null` or both populated) to avoid one going out of sync with the other.
- Auto-unboxing `Integer` timestamps from the list during binary search (`times.get(mid) <= timestamp`) is safe here since neither side is `null` by construction, but it is worth noticing that this comparison unboxes on every call.

## Wrong approaches and why they fail

- **Linear scan per `get` over all versions of a key.** Sequence: `set("k","v0",1)`, `set("k","v1",2)`, ..., up to `10^5` versions of the same key, interleaved with `10^5` `get` calls each scanning from the start — worst case O(n) per `get`, O(n^2) total, far past the O(log n)-per-call target.
- **Storing all versions of all keys in one giant sorted structure keyed only by timestamp (ignoring key), then filtering by key after the fact.** This makes lookups slower (you now search across every key's timestamps, not just the relevant key's) and is strictly worse than partitioning by key first; it also mixes unrelated keys' timestamp orderings together for no benefit.
- **Using `ceilingEntry` instead of `floorEntry` (or a binary search that returns the leftmost `>= timestamp` index instead of the rightmost `<= timestamp` index).** Sequence: `set("foo","bar",1)`, `set("foo","bar2",4)`, `get("foo",3)` — the correct answer is `"bar"` (largest timestamp `<= 3`, which is 1), but a ceiling-based lookup would instead find timestamp 4 (the smallest timestamp `>= 3`) and wrongly return `"bar2"`.

## Variants

- **Deletion of a specific version.** Add `void delete(String key, int timestamp)`; for the `TreeMap` version this is a direct `versions.remove(timestamp)` (O(log n)); for the binary-search version this requires removing from the middle of an `ArrayList` (O(n) shift) or switching to a structure that supports O(log n) deletion, which pushes that version toward needing a tree or skip list anyway.
- **Range queries ("all versions between timestamp A and B").** `TreeMap.subMap(A, true, B, true)` answers this directly in O(log n + k) where k is the number of matching versions; the binary-search version would need two binary searches (one for each bound) over the sorted array.
- **Timestamps that can arrive out of order for the same key.** The `TreeMap` version needs no changes at all (`put` re-sorts implicitly by tree structure regardless of insertion order); the binary-search version would need to switch from "always append" to "binary-search for the insertion point, then insert there," which turns its O(1) `set` into an O(n) `set` because `ArrayList` insertion in the middle requires shifting elements.

## Test cases

| # | Operation sequence | Expected outputs | What it tests |
|---|---|---|---|
| 1 | `set("foo","bar",1) get("foo",1) get("foo",2)` | `-,"bar","bar"` | Single version, exact match and query after it |
| 2 | `set("foo","bar",1) set("foo","bar2",4) get("foo",4) get("foo",5) get("foo",3)` | `-,-,"bar2","bar2","bar"` | Exact match vs floor between two versions |
| 3 | `set("k","v",10) get("k",5)` | `-,""` | Query strictly before the earliest recorded timestamp |
| 4 | `set("k","v",1) get("other",1)` | `-,""` | Query on a key that was never set |
| 5 | `set("k","v1",1) set("k","v2",3) set("k","v3",5) set("k","v4",7) get("k",0) get("k",4) get("k",6) get("k",100)` | `-,-,-,-,"","v2","v3","v4"` | Multi-version floor search including before-earliest and after-latest |
| 6 | `set("a","va",1) set("b","vb",1) get("a",5) get("b",5)` | `-,-,"va","vb"` | Two keys never leak into each other's lookups |

Randomization note: this problem has no randomized methods, so every expected value above is exact. Both the TreeMap and ArrayList+binary-search versions are run against every row and must agree.
