# Top K Frequent Elements
`ref: LC 347` · Difficulty: Medium · Pattern: Frequency map + heap

## Problem
You are given an array of integers and an integer `k`. Return the `k` distinct values that occur most often in the array. The order of the returned values does not matter, but the set of values itself must be exactly the `k` most frequent ones — the test cases here are all built so that this set is unambiguous (no tie sits exactly on the boundary between the `k`-th and `(k+1)`-th most frequent value). The output is a collection of exactly `k` distinct integers.

## Constraints
- Array length can range up to on the order of tens of thousands of elements, with possibly far fewer distinct values than total elements.
- `k` is always between `1` and the number of distinct values in the array, inclusive.
- Implication: counting frequencies is unavoidable O(n) work; the interesting complexity question is how to extract the top `k` counts afterward without fully sorting all distinct values when `k` is much smaller than the number of distinct values.

## Worked examples
1. `nums = [1,1,1,2,2,3]`, `k = 2` -> `{1, 2}`. Counts are `1 -> 3`, `2 -> 2`, `3 -> 1`; the two highest counts belong to `1` and `2`.
2. `nums = [4,4,4,4,5,5,6]`, `k = 1` -> `{4}`. Count `4 -> 4` dominates over `5 -> 2` and `6 -> 1`.
3. `nums = [7,7,8,8,8,9,9,9,9]`, `k = 2` -> `{8, 9}`. Counts are `7 -> 2`, `8 -> 3`, `9 -> 4`; the two most frequent values are `8` and `9`, leaving `7` out.
4. `nums = [1,2,2,3,3,3,4,4,4,4]`, `k = 3` -> `{2, 3, 4}`. Counts are `1 -> 1`, `2 -> 2`, `3 -> 3`, `4 -> 4`; the top three counts belong to `2`, `3`, and `4`, leaving out the least frequent value `1`.

## Edge cases checklist
- `k` equal to the number of distinct values (the answer is simply every distinct value).
- A single distinct value repeated many times, with `k = 1`.
- Negative numbers used as array values (frequency counting doesn't care about sign, but it's worth confirming nothing assumes non-negative values).
- Large gaps between frequencies (one dominant value vs. many rare ones).
- A case where only two distinct values exist at all.
- Values whose frequency ranks are close together but still resolvable without a tie at the `k`-th boundary.

## Approach

### Brute force
Count frequencies with a hash map in O(n), then fully sort the distinct values by frequency (descending) and take the first `k`. This is O(n) for counting plus O(d log d) for the sort, where `d` is the number of distinct values — since `d <= n`, this is bounded by O(n log n) overall. This is not unreasonably slow at the given bounds, but it does more work than needed: it fully orders every distinct value by frequency just to read off the top `k`, even when `k` is tiny compared to `d`.

### Optimal
Count frequencies with a hash map in O(n), exactly as in the brute force. Then, instead of sorting all distinct values, maintain a min-heap of size `k` ordered by frequency (each heap entry pairs a value with its count). Walk the distinct value-count pairs; push each one onto the heap, and whenever the heap's size exceeds `k`, pop the pair with the smallest count. At the end, the heap holds exactly the `k` most frequent values.

**Key invariant:** at every point after at least `k` distinct values have been processed, the heap holds exactly the `k` highest-frequency values seen so far among the distinct values processed, with the lowest-frequency one of that group at the root.

Proof sketch: this is the exact same argument as the Kth Largest Element pattern, applied to `(value, count)` pairs ordered by `count` instead of to raw numbers. Once the heap reaches size `k`, any new pair either has a count less than or equal to the current root's count — in which case it cannot belong in the true top-`k` by count, since `k` pairs with at least that count are already retained — or it has a count greater than the root, in which case the root is now provably outside the true top-`k` and must be evicted. Either branch preserves the invariant, and by induction it holds once every distinct value has been processed. As an alternative with the same end result, bucket sort by frequency works in O(n) instead of O(d log k): build an array of buckets indexed by count (from `0` up to the maximum possible count, which is at most `n`), place each distinct value into the bucket matching its count, then walk the buckets from the highest count downward, collecting values until `k` of them have been gathered. This avoids the heap entirely because frequency counts are bounded integers, not arbitrary comparable values, so they can be used directly as array indices.

### Step-by-step trace
Trace on `nums = [1,1,1,2,2,3]`, `k = 2`, using the heap approach. First the count map is built: `{1:3, 2:2, 3:1}`. Then each `(value, count)` pair is added to the size-2 min-heap, in map iteration order (assume `1`, then `2`, then `3` for this trace; real `HashMap` order is unspecified, but the final result is the same regardless of order):

| Step | pair added | heap action | heap contents after this step (value:count pairs) |
|---|---|---|---|
| 1 | (1,3) | push | `{(1,3)}` |
| 2 | (2,2) | push | `{(1,3),(2,2)}` |
| 3 | (3,1) | push, then pop smallest count (size 3 > k=2) | pushed (3,1) -> `{(1,3),(2,2),(3,1)}`, pop (3,1) (smallest count) -> `{(1,3),(2,2)}` |

Final heap holds `(1,3)` and `(2,2)`, so the answer set is `{1, 2}`, matching worked example 1.

## Java 8 solution
```java
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.TreeSet;

public class TopKFrequentElements {

    // Returns the k most frequent distinct values. Order in the returned array is not significant.
    public static int[] solve(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<Integer, Integer>();
        for (int num : nums) {
            // merge semantics: if num is absent, seed it with 1; otherwise add 1 to the existing count
            freq.merge(num, 1, Integer::sum);
        }

        // Min-heap of [value, count] pairs ordered by count, so the least-frequent kept
        // element is always at the root and can be evicted in O(log k).
        PriorityQueue<int[]> minHeap = new PriorityQueue<int[]>(new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Integer.compare(a[1], b[1]); // compare counts, never subtract for ordering-critical code
            }
        });

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            minHeap.add(new int[]{entry.getKey(), entry.getValue()});
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        int[] result = new int[minHeap.size()];
        int i = 0;
        for (int[] pair : minHeap) {
            result[i++] = pair[0];
        }
        return result;
    }
}
```
Note: because the returned order is not significant, the matching test runner compares results as sets (via a `TreeSet<Integer>`) rather than as ordered arrays.

## Complexity
Time O(n + d log k): O(n) to build the frequency map, then O(d log k) to push all `d` distinct values through a heap capped at size `k`. Since `d <= n`, this is never worse than O(n log n), and is much better than that when `k` is small relative to `d`. Space O(d) for the frequency map plus O(k) for the heap.

## Java 8 pitfalls for this problem
- `freq.merge(num, 1, Integer::sum)` is a Java 8 method-reference shorthand for "insert 1 if absent, otherwise add 1 to the existing value" — equivalent to, but more concise than, `freq.put(num, freq.getOrDefault(num, 0) + 1)`. Both are valid Java 8; pick one consistently.
- `PriorityQueue<int[]>` needs an explicit `Comparator`, since raw `int[]` has no natural ordering — forgetting the comparator (or writing one incorrectly) will not fail to compile, but will silently produce nonsense ordering (Java would compare array *references*, not their contents, if you mistakenly used a `Comparator` that didn't actually inspect the array elements).
- Using `a[1] - b[1]` instead of `Integer.compare(a[1], b[1])` inside the comparator is a subtle overflow trap: for extreme count values near `Integer.MIN_VALUE`/`MAX_VALUE` the subtraction can wrap around and flip the comparison's sign. Frequency counts here are always small and non-negative, so it wouldn't actually misbehave in this specific problem, but `Integer.compare` is the habit worth keeping since the same comparator pattern shows up in contexts where it does matter.
- Iterating a `HashMap`'s `entrySet()` gives no guaranteed order — code must never depend on which distinct value gets processed first, and the final heap-based algorithm here correctly does not.
- Declaring the frequency map as `Map<Integer,Integer>` (the interface) rather than the concrete `HashMap` is good practice, but if some other part of a larger program declared it as plain `Map` while actually needing `TreeMap`'s sorted-order methods (`firstKey`, `ceilingKey`, etc.), those methods would simply be unavailable through the declared type — a common "declared type hides useful methods" trap, though it doesn't come up in this particular solution since no sorted-map behavior is needed.

## Wrong approaches and why they fail
1. **Assume the array's own order reflects frequency** (e.g., "the first k distinct values encountered are the most frequent"). Counterexample: `nums = [1,2,2,3,3,3]`, `k = 1` — the value encountered first is `1`, but the most frequent value is `3`; encounter order has no relationship to frequency.
2. **Use a max-heap of all distinct values instead of a min-heap capped at size k.** This is not incorrect, but it defeats the point of the size cap: a full max-heap holds all `d` distinct values (O(d) space and O(d) to build) and then needs `k` pops at O(log d) each, which does more work and uses more memory than a min-heap that is never allowed to exceed size `k`.
3. **Compare frequencies by first converting each count to a `double` or by string-formatting counts for comparison**, rather than comparing the integer counts directly. This adds unnecessary conversion overhead and, in the string case, produces outright wrong ordering, since comparing counts as strings sorts lexicographically rather than numerically. Counterexample: string-comparing `"9"` and `"10"` says `"9"` is "greater" than `"10"`, which is backwards for numeric frequency comparison.

## Variants
1. **Bucket sort by frequency, O(n) instead of O(d log k).** Build an array of `n + 1` buckets (index = frequency, since no value can occur more than `n` times), place each distinct value into the bucket matching its count, then scan buckets from highest index down, collecting values until `k` are gathered. Described in the Optimal section above; worth implementing directly when frequency counts are known to be small integers, since it avoids the heap's `log k` factor entirely.
2. **Top k frequent words, ordered alphabetically as a tiebreak** (a close relative of this problem for strings). The heap comparator must now break ties by comparing the strings themselves whenever counts are equal, which needs a max-heap of size `k` (or a min-heap with the tie-break direction inverted) rather than the simple count-only comparator used here.
3. **Return the values sorted by frequency, most frequent first**, instead of an unordered set. After building the size-`k` min-heap exactly as here, drain it and reverse the resulting list, since draining a min-heap yields ascending order by count.

## Test cases
Because return order does not matter, "expected" below means the exact set of values, and both the test runner and the `.java` file compare results as sets rather than ordered arrays.

| # | input | expected (as a set) | what it tests |
|---|---|---|---|
| 1 | `nums=[1,1,1,2,2,3]`, `k=2` | `{1,2}` | basic case |
| 2 | `nums=[1]`, `k=1` | `{1}` | single element |
| 3 | `nums=[4,4,4,4,5,5,6]`, `k=1` | `{4}` | one dominant value |
| 4 | `nums=[7,7,8,8,8,9,9,9,9]`, `k=2` | `{9,8}` | three distinct values, top 2 requested |
| 5 | `nums=[-1,-1,-2,-3,-3,-3]`, `k=1` | `{-3}` | negative numbers |
| 6 | `nums=[5,5,5,5,5]`, `k=1` | `{5}` | single distinct value repeated |
| 7 | `nums=[1,2,2,3,3,3,4,4,4,4]`, `k=3` | `{4,3,2}` | strictly increasing frequency ladder |
| 8 | `nums=[10,20,20,30,30,30,40,40,40,40,50]`, `k=3` | `{40,30,20}` | five distinct values, top 3 clearly resolvable |
| 9 | `nums=[0,0,0,1]`, `k=2` | `{0,1}` | only two distinct values, k equals distinct count |
| 10 | `nums=[3,3,1,1,1,2,2,2,2]`, `k=2` | `{2,1}` | three distinct values, top 2 requested |
