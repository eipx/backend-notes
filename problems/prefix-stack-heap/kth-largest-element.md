# Kth Largest Element
`ref: LC 215` · Difficulty: Medium · Pattern: Min-heap of size k

## Problem
You are given an unsorted array of integers and an integer `k`. Return the `k`-th largest value in the array, where "largest" is ranked by value with `k = 1` meaning the single maximum value. Duplicates count by position, not by distinct value — if the same number appears multiple times, each occurrence takes up its own rank in the ordering. Concretely: if you sorted the array in descending order, the answer is the value sitting at index `k - 1` of that sorted list. The output is a single integer.

## Constraints
- Array length is at least `k`, and can range up to on the order of tens of thousands of elements.
- Elements can be any bounded integer, including negative values and duplicates.
- `k` is always between `1` and the array's length, inclusive.
- Implication: a full sort is O(n log n), which comfortably fits within the size range here, but is more work than necessary; a heap of size `k` gives O(n log k), which is faster whenever `k` is much smaller than `n`.

## Worked examples
1. `nums = [3,2,1,5,6,4]`, `k = 2` -> `5`. Sorted descending this is `[6,5,4,3,2,1]`, and index `1` (the second position) holds `5`.
2. `nums = [3,2,3,1,2,4,5,5,6]`, `k = 4` -> `4`. Sorted descending this is `[6,5,5,4,3,3,2,2,1]`, and the fourth position holds `4`; note the duplicate `5`s and `3`s each occupy their own rank.
3. `nums = [-1,-2,-3,-4]`, `k = 2` -> `-2`. Sorted descending this is `[-1,-2,-3,-4]`, and the second position holds `-2` — "largest" still applies to negative numbers in the usual numeric sense.
4. `nums = [2,2,2,2]`, `k = 2` -> `2`. All four entries are identical, so every rank from 1st to 4th largest is the same value.

## Edge cases checklist
- `k = 1` (the maximum value in the array).
- `k` equal to the array's length (the minimum value in the array).
- A single-element array with `k = 1`.
- All elements identical (every rank returns the same value).
- Negative numbers mixed with the ranking (make sure "largest" isn't confused with "largest magnitude").
- Duplicate values that straddle the `k`-th boundary (the value at rank `k` might also appear at rank `k-1` or `k+1`).
- Already-sorted input, both ascending and descending, as a sanity check that no assumption about input order sneaks in.

## Approach

### Brute force
Sort the entire array in descending order and read off the element at index `k - 1`. This is O(n log n) time (dominated by the sort) and O(log n) to O(n) space depending on the sort implementation. This is not actually "too slow" at the given bounds — a full sort is perfectly acceptable here — but it does more work than necessary when `k` is small, since it fully orders the entire array just to read one position out of it.

### Optimal
Maintain a min-heap that never holds more than `k` elements. Walk the array once; for each number, push it onto the heap, and if the heap's size exceeds `k`, pop the smallest element in it. By the end, the heap holds exactly the `k` largest numbers seen across the whole array, and the smallest of that group — sitting at the root of the min-heap — is exactly the `k`-th largest overall.

**Key invariant:** at every point after processing at least `k` elements, the heap contains exactly the `k` largest values seen so far, with the smallest of those `k` values at the root.

Proof sketch: by induction on the number of elements processed. Once the heap has reached size `k`, consider any new incoming value `x`. If `x` is smaller than the current root (the smallest of the retained top-`k`), then `x` cannot belong in the true top-`k` of everything seen so far either, because there are already `k` values at least as large as the current root; pushing `x` and immediately popping it back off (since it becomes the new smallest) leaves the heap unchanged, correctly excluding it. If `x` is larger than the current root, the root can no longer be among the true top-`k` once `x` is included, so evicting it and keeping `x` is exactly right. Either way, the invariant holds after processing `x`, and by induction it holds after the full array, at which point the root is the `k`-th largest value overall.

### Step-by-step trace
Trace on `nums = [3,2,1,5,6,4]`, `k = 2`:

| Step | value read | heap action | min-heap contents after this step (unordered set view) | root (current k-th largest so far) |
|---|---|---|---|---|
| 1 | 3 | push | `{3}` | 3 |
| 2 | 2 | push | `{3,2}` | 2 |
| 3 | 1 | push, then pop smallest (size 3 > k=2) | pushed 1 -> `{3,2,1}`, pop 1 -> `{3,2}` | 2 |
| 4 | 5 | push, then pop smallest | pushed 5 -> `{3,2,5}`, pop 2 -> `{3,5}` | 3 |
| 5 | 6 | push, then pop smallest | pushed 6 -> `{3,5,6}`, pop 3 -> `{5,6}` | 5 |
| 6 | 4 | push, then pop smallest | pushed 4 -> `{5,6,4}`, pop 4 -> `{5,6}` | 5 |

Final heap is `{5,6}`, root `5`, matching worked example 1.

## Java 8 solution
```java
import java.util.PriorityQueue;

public class KthLargestElement {

    // Returns the kth largest element (k = 1 means the maximum), counting duplicates by position.
    public static int solve(int[] nums, int k) {
        // PriorityQueue is a MIN-heap by default in Java, which is exactly what we want here:
        // keep the k largest values seen so far, with the smallest of that group at the root.
        PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>(k);
        for (int num : nums) {
            minHeap.add(num);
            if (minHeap.size() > k) {
                minHeap.poll(); // evict the current smallest of the retained top-k group
            }
        }
        return minHeap.peek();
    }
}
```

## Complexity
Time O(n log k): each of the `n` elements triggers at most one `add` and possibly one `poll`, and both operations cost O(log k) on a heap capped at size `k`. Space O(k) for the heap itself, which never grows past `k` elements by construction.

## Java 8 pitfalls for this problem
- `new PriorityQueue<Integer>()` with no comparator is a **min-heap** by natural ordering — a very common mistake is assuming it behaves like a max-heap. If you want a max-heap instead (for a different variant), you need `new PriorityQueue<Integer>(Collections.reverseOrder())`.
- `minHeap.peek()` returns `null` on an empty heap and auto-unboxes when returned as `int` — this is safe here only because the array is guaranteed to have at least `k` elements per the constraints; defensive code for an untrusted input would need a null check first.
- Comparing two boxed `Integer` heap elements with `==` instead of `.equals()`/`Integer.compare()` is a classic trap; it happens to "work" for small cached values (`-128..127`) and silently breaks outside that range. This implementation avoids the issue entirely by never comparing heap elements manually — `PriorityQueue` handles ordering internally via natural ordering.
- `new PriorityQueue<Integer>(k)` passes `k` as the *initial capacity*, not a hard size cap — the heap will happily grow past `k` if you keep calling `add` without also calling `poll`; the size limit here is enforced manually by the `if (minHeap.size() > k) { minHeap.poll(); }` check, not by the constructor argument.
- `Integer.compare(a, b)` is safer than `a - b` for comparator logic in general, since subtraction of two `int`s can overflow near `Integer.MIN_VALUE`/`MAX_VALUE` and silently flip the sign; it is not needed in this particular solution because no custom comparator is used, but it becomes directly relevant in the Top K Frequent Elements variant of this pattern.

## Wrong approaches and why they fail
1. **Build a max-heap of the entire array and pop `k` times.** This is not incorrect, but it defeats the purpose of using a bounded heap: a full-size max-heap costs O(n) to build and each of the `k` pops costs O(log n), giving no real advantage over sorting when `k` is close to `n`, and using more memory than the O(k) bounded min-heap approach for no benefit when `k` is small.
2. **Track only a single running maximum and a single running "second largest", generalized naively to "the k largest seen so far" using k separate variables instead of a heap.** This works for very small fixed `k` but becomes an unmaintainable, bug-prone O(k) linear scan per element for the general case, and is easy to get wrong with duplicate values. Counterexample: `nums = [5,5,5]`, `k = 2` — ad hoc "track the top few in named variables" logic often mishandles the case where the new value ties an existing tracked value, either dropping a valid duplicate or double-counting it.
3. **Use quickselect but forget to handle duplicate pivot values correctly**, assuming all values are distinct. Counterexample: `nums = [3,3,3,3]`, `k = 2` — a naive three-way (or two-way) partition that doesn't correctly group equal-to-pivot elements can recurse into an empty or wrongly sized partition and either loop forever or return the wrong index.

## Variants
1. **Quickselect (average O(n), worst-case O(n^2)).** Instead of a heap, repeatedly partition the array around a pivot (Lomuto or Hoare partitioning), and recurse only into the side of the partition that must contain the target rank, discarding the other side entirely — this avoids sorting or maintaining a heap altogether and is the standard follow-up for this exact problem when average-case linear time is wanted instead of `log k` factors.
2. **Kth smallest element instead of largest.** Either flip the heap to a max-heap of size `k` (evicting the current largest whenever the heap exceeds size `k`), or simply call this same solution with `k` replaced by `n - k + 1` against the same array.
3. **Stream of numbers where k is fixed but new numbers keep arriving over time** (a "running kth largest" data structure, close to LC 703). Keep the exact same bounded min-heap alive across calls instead of rebuilding it from scratch, and add one new number per call, which turns each query into O(log k) instead of O(n log k).

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `nums=[3,2,1,5,6,4]`, `k=2` | `5` | basic case |
| 2 | `nums=[3,2,3,1,2,4,5,5,6]`, `k=4` | `4` | duplicates present in the array |
| 3 | `nums=[1]`, `k=1` | `1` | single-element array |
| 4 | `nums=[7,6,5,4,3,2,1]`, `k=1` | `7` | k=1 returns the maximum |
| 5 | `nums=[7,6,5,4,3,2,1]`, `k=7` | `1` | k=n returns the minimum |
| 6 | `nums=[2,2,2,2]`, `k=2` | `2` | all elements identical |
| 7 | `nums=[-1,-2,-3,-4]`, `k=2` | `-2` | negative numbers |
| 8 | `nums=[5,5,5,1,1]`, `k=3` | `5` | duplicate value straddles the k-th boundary |
| 9 | `nums=[1,2]`, `k=2` | `1` | minimal two-element array, k=n |
| 10 | `nums=[9,3,2,4,8]`, `k=3` | `4` | general unsorted case |
