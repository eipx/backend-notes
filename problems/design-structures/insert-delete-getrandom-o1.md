# Insert Delete GetRandom O(1)
`ref: LC 380` · Difficulty: Medium · Pattern: array + index-map with swap-to-last deletion

## Problem

Design a set of integers supporting three operations, each in average **O(1)** time, where `getRandom` must return an existing element with equal probability across all currently-present elements:

- `boolean insert(int val)` — if `val` is not already present, add it and return `true`; if it is already present, do nothing and return `false`.
- `boolean remove(int val)` — if `val` is present, remove it and return `true`; if it is not present, do nothing and return `false`.
- `int getRandom()` — return a uniformly random element from the current set (only called when the set is non-empty).

## Constraints

- Values fit in a 32-bit signed int, positive or negative.
- Up to `2 * 10^5` calls total across `insert`/`remove`/`getRandom` — rules out any O(n) scan per call (a plain `ArrayList.remove(Object)`, which scans linearly, is too slow at this volume).
- `getRandom` is only ever called when the set is non-empty — no need to special-case an empty-set random call.
- The requirement for *uniform* randomness (not just "any" element) rules out data structures like a `HashSet` alone, since there is no O(1) way to pick a uniformly random element out of a hash table's internal buckets.

## Worked examples

**Example 1 — classic sequence**
```
insert(1)->true remove(2)->false insert(2)->true getRandom()-> 1 or 2
remove(1)->true insert(2)->false getRandom()-> 2
```
`remove(2)` before `2` was ever inserted correctly returns `false` and changes nothing. After `insert(1)` and `insert(2)`, `getRandom` must be able to return either value — this is checked by membership across many calls rather than one exact value. `insert(2)` a second time returns `false` because `2` is already present, and after `remove(1)`, `getRandom` always returns `2` since it is now the only element.

**Example 2 — remove the only element, then insert again**
```
insert(10)->true remove(10)->true insert(20)->true
```
Removing the sole element leaves the set truly empty (not just logically empty) — the backing list must actually shrink so that a subsequent `insert` starts clean and `getRandom` afterward only ever returns `20`.

**Example 3 — removing an element that is already the last one physically stored**
```
insert(1) insert(2) insert(3)   // backing list is now [1,2,3]
remove(3)->true                 // 3 is already at the tail
getRandom() -> 1 or 2
```
This is the trap case for the swap-with-last technique: when the element being removed is already the last element of the backing array, the "swap with last" step swaps an element with itself. The implementation must not corrupt the index map in this case — it still ends up correct, but only if the write order is right (see Java 8 pitfalls).

## Edge cases checklist

- `remove` on a value that was never inserted.
- `insert` on a value that already exists (must return `false` and not duplicate it).
- Removing the only remaining element (list must become truly empty, not just "logically" empty).
- Removing the element that happens to already sit at the last index of the backing list (self-swap case).
- Re-inserting a value immediately after removing it.
- `getRandom` called immediately after the very first `insert` (single-element set).
- `getRandom` called many times in a row without any mutation — every present value must be reachable, not just one.
- Negative values and zero, mixed with positive values, in `insert`/`remove`.

## Approach

### Naive

Keep a plain `ArrayList<Integer>` with no index map. `insert` checks `contains` (O(n) scan) before adding. `remove` uses `list.remove(Object.valueOf(val))`, which itself scans for the value (O(n)) and then shifts every subsequent element left by one (another O(n)). `getRandom` is the only O(1) operation in this version. With up to `2 * 10^5` calls, the O(n) `insert`/`remove` can degrade toward O(n^2) total — too slow for the required average O(1).

### Optimal

**Key invariant:** a `HashMap<value, indexInList>` always reflects exactly where `value` currently sits in the backing `ArrayList`, so any single element can be located, removed, or overwritten in O(1) without ever scanning — the only trick needed is to delete from the *middle* of an ArrayList in O(1) by first swapping the target with the *last* element (which is always cheap to remove) before shrinking.

- `insert`: O(1) map lookup to check membership, O(1) `ArrayList.add` (amortized) to append, O(1) map insert recording the new index.
- `remove`: O(1) map lookup to find the index of `val`, O(1) `ArrayList.get`/`set` to copy the last element into that index, O(1) map update to repoint the moved element's index, O(1) `ArrayList.remove(lastIndex)` to shrink from the end (removing from the *end* of an `ArrayList` is O(1) — no shifting), O(1) map removal of `val`.
- `getRandom`: O(1) — pick a uniformly random index in `[0, size)` and read it; because deletions never leave gaps (the swap-to-last trick keeps the list dense), every live element has an equal `1/size` chance of being chosen.

### Step-by-step trace

Trace of Example 3, showing the backing list and the value-to-index map:

| Step | Operation | Return | list | valueToIndex |
|---|---|---|---|---|
| 1 | `insert(1)` | `true` | `[1]` | `{1:0}` |
| 2 | `insert(2)` | `true` | `[1,2]` | `{1:0, 2:1}` |
| 3 | `insert(3)` | `true` | `[1,2,3]` | `{1:0, 2:1, 3:2}` |
| 4 | `remove(3)` | `true` | idx of 3 is 2 (already last); copy `list[2]` onto itself, then shrink -> `[1,2]` | `{1:0, 2:1}` |
| 5 | `getRandom()` | `1` or `2` | unchanged | unchanged |

## Java 8 solution

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * LC 380 - a set that supports insert, remove, and getRandom, each in average O(1) time,
 * with getRandom returning each currently-present value with equal probability.
 */
public class InsertDeleteGetRandomO1 {

    static class RandomizedSet {
        private final ArrayList<Integer> values;
        private final HashMap<Integer, Integer> valueToIndex; // value -> its index in "values"
        private final Random random;

        RandomizedSet() {
            values = new ArrayList<Integer>();
            valueToIndex = new HashMap<Integer, Integer>();
            random = new Random();
        }

        boolean insert(int val) {
            if (valueToIndex.containsKey(val)) return false;
            valueToIndex.put(val, values.size());
            values.add(val);
            return true;
        }

        boolean remove(int val) {
            Integer idx = valueToIndex.get(val);
            if (idx == null) return false;
            int lastIdx = values.size() - 1;
            int lastVal = values.get(lastIdx);
            // Move the last element into the hole left by val, then shrink from the end.
            // This keeps removal O(1) because ArrayList never has to shift elements.
            values.set(idx, lastVal);
            valueToIndex.put(lastVal, idx); // safe even when lastVal == val, see .md pitfalls
            values.remove(lastIdx); // ArrayList.remove(int index), NOT remove(Object)
            valueToIndex.remove(val);
            return true;
        }

        int getRandom() {
            int idx = random.nextInt(values.size());
            return values.get(idx);
        }
    }
}
```

## Complexity

- `insert`: O(1) average — one map lookup, one map insert, one amortized `ArrayList.add`.
- `remove`: O(1) average — one map lookup, one `ArrayList.set` + `ArrayList.remove(int)` from the tail (no shifting), one map update, one map removal.
- `getRandom`: O(1) — one random-number draw, one `ArrayList.get`.
- Space: O(n) — the list and the map each hold exactly the current set's elements, so total space is linear in the number of live elements, not the number of operations performed.

## Java 8 pitfalls for this problem

- `ArrayList.remove(int index)` vs `ArrayList.remove(Object o)` — `list.remove(lastIdx)` calls the index overload (O(1), removes by position), while `list.remove(Integer.valueOf(lastIdx))` would call the object overload (O(n) scan for a matching *value*). Since `lastIdx` is a primitive `int` here, the compiler picks the index overload automatically — but the moment you store index as a boxed `Integer` variable and pass that in, it silently switches overloads. Always double check which one you are calling.
- The self-swap case (removing an element that is already last): the code does `values.set(idx, lastVal)` (a no-op write when `idx == lastIdx`), then `valueToIndex.put(lastVal, idx)` (also a no-op remap when `lastVal == val`), and only *then* `valueToIndex.remove(val)`. Doing `valueToIndex.remove(val)` *before* the `put(lastVal, idx)` step would, in the self-swap case, delete the very mapping the `put` was about to (re)write, corrupting state — order matters.
- `java.util.Random` is not cryptographically secure and is fine here since this is not a security context; do not reach for `SecureRandom`, which is slower with no benefit for this problem.
- `random.nextInt(values.size())` throws `IllegalArgumentException` if `values` is empty — the problem guarantees `getRandom` is never called on an empty set, so no guard is added, but it is worth stating that assumption explicitly in code review.
- No `var`, no `List.of` — the backing collection must be a mutable `ArrayList<Integer>`, not an immutable list literal.
- Boxed `Integer` from `valueToIndex.get(val)` must be checked against `null`, not compared with `==` against a primitive, to detect "not present" (auto-unboxing a `null` throws `NullPointerException`).

## Wrong approaches and why they fail

- **`HashSet<Integer>` alone.** `insert`/`remove`/membership are all O(1), but there is no O(1) way to fetch a uniformly random element from a hash set — you would have to iterate to some index, which is O(n) per `getRandom`, or maintain a separate random-access structure anyway (which is exactly this problem's actual solution).
- **`ArrayList` with `remove(Object)` and no index map.** Sequence: `insert(1)`, `insert(2)`, ..., `insert(200000)`, then `remove` calls scattered throughout — each `remove` scans linearly to find the value's position before it can delete it, so total work approaches O(n^2) across `2*10^5` operations, failing the O(1)-average bound.
- **Swapping with the last element but updating the map in the wrong order (removing `val`'s map entry before remapping the swapped-in last element).** Sequence: `insert(1)`, `insert(2)`, `insert(3)`, then `remove(3)` where `3` is already last — removing `valueToIndex` for `3` first and *then* writing `valueToIndex.put(lastVal=3, idx=2)` would re-insert a mapping for the value that was supposed to be gone, leaving `3` still "present" in the map even though it was removed from the list, so a later `insert(3)` would wrongly return `false`.

## Variants

- **Insert/remove/getRandom with duplicates allowed (LC 381, `RandomizedCollection`).** The value-to-index map becomes value-to-*set-of-indices* (e.g. `HashMap<Integer, LinkedHashSet<Integer>>`), since a value may now live at multiple positions; the swap-to-last removal picks any one of the duplicate's indices to evict and updates only the moved element's own index-set entries.
- **Weighted random selection.** If elements should be returned with probabilities proportional to a weight rather than uniformly, maintain a Fenwick tree (binary indexed tree) or segment tree over cumulative weights so a random draw plus a binary search over prefix sums picks an element in O(log n) — true O(1) weighted sampling generally is not achievable with simple arrays.
- **Thread safety.** Since both `values` and `valueToIndex` must stay in lockstep, wrap all three methods in a single lock; a lock-free version would need an atomic compare-and-swap across two independent structures, which is substantially harder to get right than a coarse lock for this problem's scale.

## Test cases

| # | Operation sequence | Expected outputs | What it tests |
|---|---|---|---|
| 1 | `insert(1)` | `true` | Insert into an empty set |
| 2 | `remove(2)` | `false` | Remove a value never inserted |
| 3 | `insert(2)` | `true` | Insert a second distinct value |
| 4 | `getRandom()` (after inserting 1, 2) | member of `{1,2}` | Uniform membership over 2 elements (checked, not an exact value) |
| 5 | `remove(1)` | `true` | Remove an existing value |
| 6 | `insert(2)` (duplicate) | `false` | Insert of an already-present value is rejected |
| 7 | `getRandom()` (only 2 left) | `2` | Single remaining element is always returned |
| 8 | `remove(10)` on a set containing only `10` | `true` | Removing the only element empties the set |
| 9 | `insert(20)` right after emptying | `true` | Re-inserting after the set was fully emptied |
| 10 | `remove(3)` where backing list is `[1,2,3]` (3 already last) | `true` | Self-swap case: removing the physically-last element |
| 11 | `getRandom()` after case 10 | member of `{1,2}` | Membership holds after a self-swap removal |
| 12 | 3000 calls to `getRandom()` over `{100,200,300}` | all three values observed | Distribution sanity check, not exact-value comparison |

Randomization note: `getRandom` is nondeterministic by design. Tests 4, 11, and 12 assert set membership (and, for 12, that every inserted value is eventually returned across many calls) instead of comparing against one fixed expected value.
