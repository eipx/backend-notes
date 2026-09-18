# Max Consecutive Ones III
`ref: LC 1004` · Difficulty: Medium · Pattern: variable-size sliding window with a zero-count budget

## Problem

You are given a binary array `nums` (every entry is `0` or `1`) and an integer `k`. You may flip at most `k` of the zeros to ones. After flipping, report the length of the longest run of consecutive ones you can produce. You never have to decide up front which zeros to flip; the question only asks for the best achievable length.

Input: an integer array `nums` of `0`s and `1`s, and an integer `k >= 0`.
Output: a single integer, the maximum length of a contiguous run of ones achievable after flipping at most `k` zeros within that run.

## Constraints

- `1 <= nums.length <= 10^5`
- `nums[i]` is either `0` or `1`.
- `0 <= k <= nums.length`
- With `n` up to `1e5`, checking every possible window and counting its zeros from scratch is `O(n^2)` in the worst case (roughly `1e10` operations), which is too slow. An `O(n)` single pass is expected.

## Worked examples

1. `nums = [1,1,1,0,0,0,1,1,1,1,0]`, `k = 2` -> `6`. Flipping the two zeros at positions 4 and 5 (0-indexed) merges the run `[1,1,1]` (positions 6-9 plus the flips) into a run of length 6 starting at position 4 (`0,0` flipped, then `1,1,1,1` already there).
2. `nums = [0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1]`, `k = 3` -> `10`. The best window uses three of the scattered zeros as "glue" between the longer runs of ones in the middle of the array.
3. `nums = [0,0,0,1]`, `k = 0` -> `1`. No flips are allowed, so the answer is just the length of the longest existing run of ones, which is the single trailing `1`.
4. `nums = [0,0,0]`, `k = 3` -> `3`. Every zero can be flipped, so the whole array becomes one run of ones.

## Edge cases checklist

- `k = 0` (no flips allowed at all; reduces to "longest existing run of ones").
- `k >= count of zeros in the array` (the whole array becomes one run).
- All ones already (answer is `nums.length` regardless of `k`).
- All zeros (answer is `min(k, nums.length)`).
- Single-element array, both as `0` and as `1`.
- `k` equal to `nums.length` (every element may be flipped).
- A window whose zero count overflows the budget by exactly one, requiring the left edge to skip past several `1`s before it reaches the zero that must be dropped (the shrink step is not always a single step even though the overflow itself is always by exactly one).
- Zeros spread out with no way to "chain" more than one flip into the same run economically (tests that the window correctly abandons a stale left edge instead of getting stuck).

## Approach

### Brute force

For every pair of start and end indices, count the zeros inside that subarray and check whether the count is `<= k`; track the longest subarray that qualifies. Counting the zeros in each window from scratch is `O(n)` per window over `O(n^2)` windows, giving `O(n^3)` naively, or `O(n^2)` if zero counts are precomputed with prefix sums so each window's count is `O(1)`. Either way this is too slow for `n` up to `1e5`.

### Optimal

Slide a window `[left, right]` across the array while keeping a running count of zeros currently inside it. Expand `right` one step at a time; whenever the zero count exceeds `k`, advance `left` (removing elements from the window and decrementing the zero count when a removed element is a zero) until the zero count is back to `k` or fewer. After each expansion (and any necessary shrink), record the window size if it is the best seen so far.

**Key invariant:** at the end of processing each `right`, the window `[left, right]` contains at most `k` zeros, and `left` is the smallest possible value for which that holds: the window is the longest valid window that *ends* at `right`. Since the zero count can only ever be exceeded by exactly one (each step adds at most one zero before the check), advancing `left` is always sufficient to restore validity; it never needs to jump ahead of `right`.

Proof sketch: initially the window is empty, which trivially satisfies "at most `k` zeros." Every step either extends `right` (adding at most one zero, so the count can exceed `k` by at most 1) or shrinks from `left` (removing exactly one element at a time until back within budget). Because `left` only ever moves forward and the window is always shrunk to the minimum extent needed, the recorded `right - left + 1` at each step is the true maximum for that `right`, so the overall maximum across all `right` values is the true answer.

### Step-by-step trace

Trace on `nums = [1,1,1,0,0,0,1,1,1,1,0]`, `k = 2` (indices 0..10):

| right | nums[right] | zero count | shrink? | left after | window size | best |
|---|---|---|---|---|---|---|
| 0 | 1 | 0 | no | 0 | 1 | 1 |
| 1 | 1 | 0 | no | 0 | 2 | 2 |
| 2 | 1 | 0 | no | 0 | 3 | 3 |
| 3 | 0 | 1 | no | 0 | 4 | 4 |
| 4 | 0 | 2 | no | 0 | 5 | 5 |
| 5 | 0 | 3 | yes, left 0->4 (drops the zero at index 3) | 4 | 2 | 5 |
| 6 | 1 | 2 | no | 4 | 3 | 5 |
| 7 | 1 | 2 | no | 4 | 4 | 5 |
| 8 | 1 | 2 | no | 4 | 5 | 5 |
| 9 | 1 | 2 | no | 4 | 6 | 6 |
| 10 | 0 | 3 | yes, left 4->5 (drops the zero at index 4) | 5 | 6 | 6 |

Final answer: `6`, matching worked example 1.

## Java 8 solution
```java
public class MaxConsecutiveOnesIII {

    // Longest run of 1s achievable after flipping at most k zeros to 1.
    public static int solve(int[] nums, int k) {
        int left = 0;
        int zeroCount = 0;
        int best = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                zeroCount++;
            }
            while (zeroCount > k) {
                if (nums[left] == 0) {
                    zeroCount--;
                }
                left++;
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    private static void check(int caseNum, int[] nums, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums, k);
        if (got == expected) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{1,1,1,0,0,0,1,1,1,1,0}, 2, 6, fail, total);
        check(2, new int[]{0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1}, 3, 10, fail, total);
        check(3, new int[]{0,0,0,1}, 0, 1, fail, total);
        check(4, new int[]{1,1,1,1}, 0, 4, fail, total);
        check(5, new int[]{0,0,0}, 3, 3, fail, total);
        check(6, new int[]{0}, 0, 0, fail, total);
        check(7, new int[]{1}, 0, 1, fail, total);
        check(8, new int[]{1,0,1,0,1,0,1}, 1, 3, fail, total);
        check(9, new int[]{0,1}, 0, 1, fail, total);
        check(10, new int[]{1,1,0,0,1,1}, 1, 3, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n): `right` advances exactly `n` times, and `left` advances at most `n` times total across the whole run (it never resets backward), so the total work is bounded by `2n`. Space O(1): only a few scalar counters are used beyond the input array.

## Java 8 pitfalls for this problem

- The shrink loop must be a `while`, not an `if`. A single `right` step only ever adds one zero, but restoring validity can still require advancing `left` past several `1`s before it reaches the zero that needs to leave the window, so more than one `left++` per step is normal, not a bug.
- Off-by-one on the window size: it is `right - left + 1`, computed *after* any shrinking for the current `right`, not before.
- The condition inside the shrink loop is `zeroCount > k`, strictly greater. Using `>=` would shrink the window even when it is already within budget, silently truncating valid windows.
- `nums[left]` must be checked for `== 0` before incrementing `left`, and in that order (check, then decrement if applicable, then move `left`); swapping the order of the decrement and the `left++` is easy to get backward under time pressure.
- No boxing concerns here since everything is primitive `int`, but if this were adapted to work over a `List<Integer>`, remember that `.equals(0)` or auto-unboxed `== 0` both work safely for comparing to a small literal. The usual `Integer` cache boundary (`-128..127`) is irrelevant here since we only ever compare against the literal `0`.

## Wrong approaches and why they fail

1. **Scan forward from index 0 only, stopping the first time more than `k` zeros have been seen, without ever considering a window that starts later than index 0.** Counterexample: `nums = [0,0,0,1,1,1,1,1]`, `k = 1`. This approach hits a second zero almost immediately and reports a short answer, while the true best window skips the leading zeros entirely and uses one zero plus five ones starting further in, for a length of `6`.
2. **Use the total zero count in the whole array and subtract the "excess" from the full length: `answer = n - max(0, totalZeros - k)`.** This assumes the zeros that must stay unflipped can always be pushed to the two ends of the array, which is not true when the zeros are scattered. Counterexample: `nums = [1,0,1,0,1,0,1]`, `k = 1`: total zeros is `3`, so the formula gives `7 - (3-1) = 5`, but the true answer (found by the sliding window) is `3`. No single window of length 5 in this array contains only one zero.
3. **When the zero budget is exceeded, "jump over" the offending zero by skipping it while leaving the window's other boundary untouched, treating the result as if it were still one contiguous run.** This silently creates a subarray with a gap, which is not a valid contiguous run at all. Counterexample: `nums = [1,1,0,0,1,1]`, `k = 1`. Skipping the second zero without shrinking `left` would claim a run of length 5 (`1,1,[skip],1,1` plus the first zero), but no actual contiguous window of length 5 in this array has only one zero; the true answer is `3`.

## Variants

1. **Max Consecutive Ones II** (a smaller, older problem) is exactly the `k = 1` special case of this one.
2. **Return the actual window (its start and end index), not just its length.** Track `bestLeft` alongside `best` and update both together whenever a new maximum is found.
3. **"Exactly `k` flips must be used" instead of "at most `k`."** This changes the problem meaningfully: if `k` is larger than the number of zeros available, the extra flips have nothing left to do, so the answer needs a separate case analysis rather than a direct reuse of the same sliding window.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `nums=[1,1,1,0,0,0,1,1,1,1,0], k=2` | `6` | general case, multi-step shrink |
| 2 | `nums=[0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1], k=3` | `10` | longer array, several scattered zeros |
| 3 | `nums=[0,0,0,1], k=0` | `1` | k=0, no flips allowed |
| 4 | `nums=[1,1,1,1], k=0` | `4` | already all ones |
| 5 | `nums=[0,0,0], k=3` | `3` | k covers every zero |
| 6 | `nums=[0], k=0` | `0` | single zero, no flips |
| 7 | `nums=[1], k=0` | `1` | single one, no flips |
| 8 | `nums=[1,0,1,0,1,0,1], k=1` | `3` | alternating pattern, tests the totalZeros-subtraction wrong approach |
| 9 | `nums=[0,1], k=0` | `1` | minimal array with a leading zero |
| 10 | `nums=[1,1,0,0,1,1], k=1` | `3` | tests the "skip the zero" wrong approach |
