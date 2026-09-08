# Sliding Window Maximum
`ref: LC 239` · Difficulty: Hard · Pattern: fixed-size sliding window with a monotonic deque

## Problem

Given an integer array `nums` and a window size `k`, slide a window of exactly `k` consecutive elements from the left end of the array to the right end, one step at a time, and report the maximum value inside the window at every position it stops at. The window starts covering indices `[0, k-1]`, then `[1, k]`, then `[2, k+1]`, and so on until it covers `[n-k, n-1]`.

Input: an integer array `nums` of length `n`, and an integer `k` with `1 <= k <= n`.
Output: an integer array of length `n - k + 1`, where entry `i` is the maximum of `nums[i .. i+k-1]`.

## Constraints

- `1 <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`
- `1 <= k <= nums.length`
- With n up to 1e5 and one output value per window (up to n windows), any approach that recomputes the max of each window from scratch is O(n*k), which is O(n^2) when k is close to n — up to ~1e10 operations, far too slow. The expected solution is O(n) total, meaning each element can only be looked at a small, amortized-constant number of times.

## Worked examples

1. Input: `nums = [1,3,-1,-3,5,3,6,7]`, `k = 3` → Output: `[3,3,5,5,6,7]`. Window `[1,3,-1]` → max 3; `[3,-1,-3]` → max 3; `[-1,-3,5]` → max 5; `[-3,5,3]` → max 5; `[5,3,6]` → max 6; `[3,6,7]` → max 7.
2. Input: `nums = [1]`, `k = 1` → Output: `[1]`. Window size equals array length equals 1, so there is exactly one window containing the single element.
3. Input: `nums = [3,1,2]`, `k = 3` → Output: `[3]`. Window size equals the whole array, so there is exactly one window and its max is the max of the whole array.
4. Input: `nums = [-7,-8,-3,-9,-1]`, `k = 3` → Output: `[-3,-3,-1]`. All values are negative; window `[-7,-8,-3]` → max -3 (the "biggest" of three negatives, i.e. closest to zero); `[-8,-3,-9]` → max -3; `[-3,-9,-1]` → max -1.

## Edge cases checklist

- `k == 1`: every element is its own window, output equals the input array unchanged.
- `k == n`: exactly one window, output has length 1 and equals the overall maximum.
- All values equal: every window's max is that same repeated value.
- Strictly increasing array: each window's max is always its rightmost element.
- Strictly decreasing array: each window's max is always its leftmost element.
- All negative values: "maximum" still means closest to zero / least negative, not "largest magnitude."
- Two-element array with `k = 2`: smallest non-trivial case beyond `k = n = 1`.
- Duplicate maxima across overlapping windows (the same maximum value can legitimately appear in several consecutive output slots).
- Large input size (1e5) for a performance sanity check on the O(n) requirement.

## Approach

### Brute force

For each of the `n - k + 1` window positions, scan all `k` elements inside it and take the max. This is O(n*k) time and O(1) extra space (besides the output). At `n = 10^5` and `k` around `5*10^4`, that's on the order of `5*10^9` comparisons — far too slow. It is only acceptable when `k` is a small constant.

A slightly smarter brute force keeps a running max for consecutive same-direction sliding using a multiset/balanced tree, giving O(n log k), which passes the constraint but is not the intended optimal.

### Optimal

Maintain a deque (double-ended queue) of array **indices**, not values, such that the values at those indices are in strictly decreasing order from front to back. The front of the deque is always the index of the current window's maximum. For each new index `i`: first evict any index from the front that has fallen out of the window (`index <= i - k`); then evict any index from the back whose value is smaller than `nums[i]` (they can never be the answer again as long as `nums[i]` stays in the window); then push `i` onto the back. Once the window is fully formed (`i >= k - 1`), the front of the deque is the answer for that window.

**Key invariant:** the deque always holds, front to back, exactly the indices currently inside the window whose values could still possibly become the window's maximum in the future, in strictly decreasing order of their values, which means the front is always the index of the current maximum.

Proof sketch: an index `j` can only be evicted from the back when a later index `i > j` (which will remain in the window at least as long as `j` would have, since `i` is more recent) has `nums[i] >= nums[j]`; at that point `j` can never be the maximum of any future window that still contains `i`, because `i` is both more recent (so it leaves the window later) and at least as large. So discarding `j` loses no information. Any index kept in the deque, on the other hand, is genuinely the largest value among all indices from itself to the current position, since anything smaller between it and now would already have been evicted from the back before it was even pushed — that is exactly how the decreasing order is built and preserved. Combined with evicting from the front anything that slides out of the window on the left, the front of the deque is always both in-window and the largest value still eligible, i.e. the true window maximum.

### Step-by-step trace

Trace on `nums = [1,3,-1,-3,5,3,6,7]`, `k = 3` (expected `[3,3,5,5,6,7]`). Deque holds indices; shown as values in parentheses for readability.

| i | nums[i] | evict front (out of window) | evict back (smaller values) | deque after push | output emitted |
|---|---|---|---|---|---|
| 0 | 1 | — | — | [0(1)] | — |
| 1 | 3 | — | pop 0(1) | [1(3)] | — |
| 2 | -1 | — | — | [1(3), 2(-1)] | window done → 3 |
| 3 | -3 | — | — | [1(3), 2(-1), 3(-3)] | 3 |
| 4 | 5 | pop 1 (index 1 ≤ 4-3=1) | pop 3(-3), pop 2(-1) | [4(5)] | 5 |
| 5 | 3 | — | — | [4(5), 5(3)] | 5 |
| 6 | 6 | — | pop 5(3), pop 4(5) | [6(6)] | 6 |
| 7 | 7 | — | pop 6(6) | [7(7)] | 7 |

Final output: `[3,3,5,5,6,7]`.

## Java 8 solution

```java
import java.util.*;

public class SlidingWindowMaximum {

    public static int[] solve(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<Integer>(); // stores indices, values strictly decreasing front-to-back
        for (int i = 0; i < n; i++) {
            // drop indices that fell out of the window on the left
            while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }
            // maintain the decreasing invariant: anything smaller than nums[i] can never be
            // the max again while nums[i] is still in the window, so it is safe to discard
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }
            deque.offerLast(i);
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{1,3,-1,-3,5,3,6,7}, 3, new int[]{3,3,5,5,6,7}},
            {new int[]{1}, 1, new int[]{1}},
            {new int[]{1,-1}, 1, new int[]{1,-1}},
            {new int[]{9,11}, 2, new int[]{11}},
            {new int[]{4,-2}, 2, new int[]{4}},
            {new int[]{5,5,5,5}, 2, new int[]{5,5,5}},
            {new int[]{5,4,3,2,1}, 2, new int[]{5,4,3,2}},
            {new int[]{1,2,3,4,5}, 2, new int[]{2,3,4,5}},
            {new int[]{3,1,2}, 3, new int[]{3}},
            {new int[]{-7,-8,-3,-9,-1}, 3, new int[]{-3,-3,-1}}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            int k = (Integer) cases[i][1];
            int[] expected = (int[]) cases[i][2];
            int[] got = solve(nums, k);
            boolean ok = Arrays.equals(expected, got);
            if (ok) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected) + " got " + Arrays.toString(got));
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
```

## Complexity

- Time: O(n). Each index is pushed onto the deque exactly once and popped at most once (either from the front when it exits the window, or from the back when a later value beats it), so total deque operations are bounded by 2n — an amortized argument, even though a single step could in theory pop many elements from the back.
- Space: O(k) worst case for the deque (e.g. a strictly decreasing array keeps every index in the current window), plus O(n - k + 1) for the output array.

## Java 8 pitfalls for this problem

- Declaring the deque as `Deque<Integer> deque = new ArrayDeque<>();` and then calling `deque.peekFirst()` to get an `int` directly in an arithmetic comparison — this auto-unboxes, which is fine, but comparing two boxed `Integer` objects with `==` (e.g. `deque.peekFirst() == someInt`) instead of `.equals()` or unboxing first is a classic bug; here we avoid it by always comparing via `<=`/`<` which forces unboxing to `int`.
- `ArrayDeque` rejects `null` elements outright (throws `NullPointerException`), unlike `LinkedList`; storing plain `int` indices sidesteps this, but if you ever store boxed wrapper objects that could be `null`, `ArrayDeque` is the wrong choice.
- Using a `PriorityQueue` (max-heap via a reverse comparator) instead of a deque is tempting but does not efficiently support "remove this specific element that just fell out of the window on the left" — a `PriorityQueue` only lets you peek/poll the top, and `remove(Object)` for an arbitrary element is O(n), degrading the whole approach to O(n log n) or worse with lazy-deletion bookkeeping.
- Storing values in the deque instead of indices makes it impossible to tell whether the front value has fallen out of the window — you need the index to compare against `i - k`.
- Off-by-one on the window-exit check: it must be `index <= i - k` (equivalently `index < i - k + 1`), not `index < i - k`, or the oldest valid index gets evicted one step too early.

## Wrong approaches and why they fail

- **Track only the running max and running second-max without indices.** Counterexample: `[5,4,3,2,1]`, `k = 2`. Once `5` slides out of the window, you need to know the max of the *remaining* elements in the window, but a simple "top two values seen" scheme has no notion of position, so it cannot tell that `5` (not just some second-largest value) has left; it would incorrectly keep reporting `5` as still relevant after window `[5,4]` has passed.
- **Recompute the max by scanning the whole window every time it slides (brute force) and assume it is fast enough because "n is only 1e5".** Counterexample: any array with `k` around `n/2`, e.g. `n = 10^5`, `k = 5*10^4` — this gives roughly `5*10^9` comparisons, which will not finish in a reasonable time even though `n` itself looks modest.
- **Use a deque of indices but only evict from the front, never from the back.** Counterexample: `[1,3,-1,-3,5,3,6,7]`, `k = 3` — without evicting smaller trailing values from the back, the deque would fill with `[0,1,2,3,4,...]` in raw index order and the front would still point at stale small values (like index 0 holding value 1) long after a bigger value (3) has appeared later in the same window, giving a wrong (too small) answer.

## Variants

- **Return the sliding window minimum instead of the maximum.** Flip the eviction comparison: evict from the back any index whose value is *greater* than the incoming value, keeping the deque increasing instead of decreasing; the front is then always the window minimum.
- **Process the array as an infinite/streaming input where old elements must eventually be discarded but you don't know `n` in advance.** The same deque technique still works because it only ever needs the current window's worth of history — just make sure the eviction-by-index-age check uses a running count of elements seen so far instead of a fixed array length.
- **Support a `k` that changes over time (window size grows or shrinks dynamically).** This breaks the simple index-arithmetic eviction check; you would need to track window boundaries explicitly (e.g. an explicit `windowStart` maintained alongside events that change `k`) and evict from the front based on the current boundary rather than a fixed offset from `i`.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `nums=[1,3,-1,-3,5,3,6,7], k=3` | `[3,3,5,5,6,7]` | classic mixed-sign example |
| 2 | `nums=[1], k=1` | `[1]` | single element, k=1 |
| 3 | `nums=[1,-1], k=1` | `[1,-1]` | k=1 returns the array unchanged |
| 4 | `nums=[9,11], k=2` | `[11]` | two elements, k equals n |
| 5 | `nums=[4,-2], k=2` | `[4]` | k equals n, negative present |
| 6 | `nums=[5,5,5,5], k=2` | `[5,5,5]` | all values equal |
| 7 | `nums=[5,4,3,2,1], k=2` | `[5,4,3,2]` | strictly decreasing, max always leftmost |
| 8 | `nums=[1,2,3,4,5], k=2` | `[2,3,4,5]` | strictly increasing, max always rightmost |
| 9 | `nums=[3,1,2], k=3` | `[3]` | k equals n, single output window |
| 10 | `nums=[-7,-8,-3,-9,-1], k=3` | `[-3,-3,-1]` | all-negative values, "max" means closest to zero |
