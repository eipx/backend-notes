# Longest Increasing Subsequence
`ref: LC 300` · Difficulty: Medium · Pattern: 1-D DP over subsequence end positions (O(n^2) table), or a tails array with binary search (O(n log n))

## Problem

Given an integer array `nums`, find the length of the longest subsequence whose values strictly increase from left to right. A subsequence is what remains after deleting zero or more elements without changing the order of the rest, so the kept elements do not have to sit next to each other. Two equal values do not count as an increase.

Input: an integer array `nums`.
Output: a single integer, the length of the longest strictly increasing subsequence of `nums`.

## Constraints

- `1 <= nums.length <= 2500`
- `-10000 <= nums[i] <= 10000`
- An array of `n` elements has `2^n` subsequences, so checking each one is impossible at `n = 2500`. A quadratic pass makes about 3.1 million pair checks at this size, which is fast. An `O(n log n)` method also exists and is the follow-up.

## Worked examples

1. `nums = [10, 9, 2, 5, 3, 7, 101, 18]` -> `4`. One longest choice is `2, 3, 7, 18` (indices 2, 4, 5, 7). Other choices of length 4 exist, such as `2, 5, 7, 101`. The answer is the length, not the choice.
2. `nums = [7, 7, 7, 7, 7, 7, 7]` -> `1`. Equal values do not increase, so no two of them can share a subsequence. Any single element is a valid subsequence of length 1.
3. `nums = [4, 10, 4, 3, 8, 9]` -> `3`. The subsequence `4, 8, 9` works, and so does `3, 8, 9`. The large early value `10` is a dead end because nothing after it is bigger.
4. `nums = [-1, -2, 0, -3, 1]` -> `3`. The subsequence `-1, 0, 1` works, and so does `-2, 0, 1`. Negative values need no special handling, since only the order of the values matters.

## Edge cases checklist

- `n == 1` (the array itself is the only subsequence, so the answer is 1).
- All values equal (strictness: the answer is 1, not `n`).
- Strictly increasing input (the answer is `n`).
- Strictly decreasing input (the answer is 1).
- Duplicates inside an otherwise increasing run, such as `[2, 2, 3, 3, 4, 4]` (each value can be used at most once in a row, so the answer is 3, not 6).
- Negative values, and the extreme values `-10000` and `10000` (only comparisons are made, so nothing can overflow).
- A large early value that leads nowhere (`10` in `[4, 10, 4, 3, 8, 9]`), which defeats any approach that commits to the first element it sees.
- The best subsequence ends before the last index, so the answer is the largest entry of `dp`, not `dp[n - 1]`.
- Several different subsequences tie for the best length (any one of them gives the same answer).
- The upper bound `n == 2500`, where an exponential search is hopeless and a quadratic table is still comfortable.

## Approach

### Brute force

For each of the `2^n` subsets of indices, check whether the chosen values strictly increase, and remember the largest subset size that passes. This is correct but costs `O(2^n * n)`. A recursive "take it or skip it" search that carries the last chosen value is still `O(2^n)`, because the same situation (current index, last chosen value) is reached again and again by different paths. Both are only useful for checking small inputs.

### Optimal

**Table form (the main solution, `solve`).** Let `dp[i]` be the length of the longest strictly increasing subsequence that ends exactly at index `i`, so `nums[i]` is its last element. Every element alone is a subsequence of length 1, so each `dp[i]` starts at 1. A longer one is built by picking an earlier index `j < i` with `nums[j] < nums[i]` and putting `nums[i]` after the best subsequence that ends at `j`:

`dp[i] = 1 + max(dp[j])` over all `j < i` with `nums[j] < nums[i]`, or `1` when no such `j` exists.

The answer is the largest value in `dp`, because the best subsequence can end at any index. The table is filled from left to right. When `dp[i]` is computed, every `dp[j]` it reads (with `j < i`) is already final.

**Fast form (`solveFast`, O(n log n)).** Keep an array `tails` where `tails[k]` is the smallest possible last value among all strictly increasing subsequences of length `k + 1` found so far. The array is strictly increasing, because every subsequence of length `k + 2` contains one of length `k + 1` whose last value is smaller. For each new value `x`, find the first position `pos` with `tails[pos] >= x` using a binary search (a lower bound), and set `tails[pos] = x`. If no such position exists, `x` is larger than every stored tail, so it extends the longest subsequence: it goes at the end and the count grows by one. After the last value, the count of stored entries is the answer. Searching for `>=` (not `>`) is what keeps the increase strict: a value equal to an existing tail replaces it instead of extending past it.

`tails` is bookkeeping, not a subsequence of the input. Its length is the answer, but its contents may not be a real subsequence.

**Key invariant:** in the table form, once iteration `i` finishes, `dp[i]` is the exact length of the longest strictly increasing subsequence ending at index `i`, because every candidate predecessor `j < i` was already final and the inner loop tries all of them. In the fast form, after each value is processed, `tails[0..size-1]` is strictly increasing and `tails[k]` is the smallest last value of any strictly increasing subsequence of length `k + 1` among the values seen so far. Replacing `tails[pos]` by a value that is smaller or equal never hurts, because a smaller last value can be extended by at least as many later values.

### Step-by-step trace

Table form for `nums = [4, 10, 4, 3, 8, 9]`:

| i | nums[i] | earlier j with nums[j] < nums[i] | best dp[j] among them | dp[i] = 1 + best |
|---|---|---|---|---|
| 0 | 4 | none | 0 | 1 |
| 1 | 10 | 0 | 1 | 2 |
| 2 | 4 | none (the earlier 4 is equal, not smaller) | 0 | 1 |
| 3 | 3 | none | 0 | 1 |
| 4 | 8 | 0, 2, 3 | 1 | 2 |
| 5 | 9 | 0, 2, 3, 4 | 2 | 3 |

`dp = [1, 2, 1, 1, 2, 3]`, and the largest entry is `3`, matching worked example 3.

Fast form on the same input:

| x | position found (first tail >= x) | tails after |
|---|---|---|
| 4 | 0 (tails is empty, so this is the end) | [4] |
| 10 | 1 (end) | [4, 10] |
| 4 | 0 (tails[0] equals 4, so it is replaced) | [4, 10] |
| 3 | 0 | [3, 10] |
| 8 | 1 | [3, 8] |
| 9 | 2 (end) | [3, 8, 9] |

Three entries are stored, so the answer is `3`, the same as the table form.

## Java 8 solution
```java
public class LongestIncreasingSubsequence {

    // Length of the longest strictly increasing subsequence, table form.
    // dp[i] is the length of the longest strictly increasing subsequence that
    // ends exactly at index i (it must use nums[i] as its last element).
    // The answer is the largest value anywhere in dp, not dp[n - 1].
    public static int solve(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        int best = 0;
        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i] && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                }
            }
            if (dp[i] > best) {
                best = dp[i];
            }
        }
        return best;
    }

    // Same answer in O(n log n). tails[k] is the smallest last value of any
    // strictly increasing subsequence of length k + 1 among the elements seen
    // so far. Only tails[0..size-1] is meaningful, and it is strictly
    // increasing, so each new value can be placed with a binary search.
    public static int solveFast(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;
        for (int x : nums) {
            int pos = lowerBound(tails, size, x);
            tails[pos] = x;
            if (pos == size) {
                size++;
            }
        }
        return size;
    }

    // First index in tails[0..size-1] whose value is >= target, or size when
    // every stored value is smaller than target.
    private static int lowerBound(int[] tails, int size, int target) {
        int lo = 0;
        int hi = size;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (tails[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums);
        int gotFast = solveFast(nums);
        if (got == expected && gotFast == expected) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            if (got != expected) {
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            } else {
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + gotFast + " (solveFast)");
            }
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{10, 9, 2, 5, 3, 7, 101, 18}, 4, fail, total);
        check(2, new int[]{0, 1, 0, 3, 2, 3}, 4, fail, total);
        check(3, new int[]{7, 7, 7, 7, 7, 7, 7}, 1, fail, total);
        check(4, new int[]{1}, 1, fail, total);
        check(5, new int[]{1, 2, 3, 4, 5}, 5, fail, total);
        check(6, new int[]{5, 4, 3, 2, 1}, 1, fail, total);
        check(7, new int[]{4, 10, 4, 3, 8, 9}, 3, fail, total);
        check(8, new int[]{1, 3, 6, 7, 9, 4, 10, 5, 6}, 6, fail, total);
        check(9, new int[]{-1, -2, 0, -3, 1}, 3, fail, total);
        check(10, new int[]{2, 2, 3, 3, 4, 4}, 3, fail, total);
        check(11, new int[]{3, 5, 6, 2, 5, 4, 19, 5, 6, 7, 12}, 6, fail, total);
        check(12, new int[]{10000, -10000, 0, 10000}, 3, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

`solve`: time O(n^2). For each index `i` the inner loop looks at at most `i` earlier indices, so there are about `n(n - 1) / 2` pair checks in total (roughly 3.1 million at `n = 2500`). Space O(n) for `dp`.

`solveFast`: time O(n log n). Each of the `n` values does one binary search over at most `n` stored entries. Space O(n) for `tails`.

## Java 8 pitfalls for this problem

- Forgetting to set `dp[i] = 1`. A new `int[]` is filled with 0, so without the explicit reset every answer is one too small, and an input with no increasing pair would report 0.
- Returning `dp[n - 1]` instead of the maximum over all of `dp`. For `[1, 3, 6, 7, 9, 4, 10, 5, 6]`, `dp[8]` is `5` but the answer is `6`.
- Using `<=` instead of `<` when comparing `nums[j]` with `nums[i]`, or an upper-bound search (first entry greater than `x`) instead of a lower-bound search in the fast form. Both quietly turn "strictly increasing" into "non-decreasing", and give `7` instead of `1` for seven equal values.
- Searching the whole `tails` array instead of only its first `size` entries. The unused slots hold `0`, which is a real value in this problem (and can be smaller or larger than the input values), so the binary search would be corrupted. Always bound the search by `size`.
- Using `Arrays.binarySearch` for the tails step. When the value is missing it returns `-(insertion point) - 1`, and decoding that is easy to get wrong. The hand-written `lowerBound` returns the insertion point directly and treats a match and a miss the same way.
- Keeping `tails` in a `List<Integer>` and comparing with `==` (for example `tails.get(mid) == x`). On two `Integer` objects `==` compares references, and values outside `-128..127` are not cached, so equal values can compare as different. Use an `int[]`, or unbox before comparing.
- Writing `int mid = (lo + hi) / 2`. It cannot overflow at these sizes, but `lo + (hi - lo) / 2` is the form that stays safe when the bounds grow.

## Wrong approaches and why they fail

1. **Greedy: start at the first element and keep taking the next element that is larger than the last one taken.** A large early value pulls the walk off course. Counterexample: `nums = [4, 10, 4, 3, 8, 9]`. The greedy walk takes 4, then 10, and then nothing is larger, so it reports 2. The correct answer is `3` (`4, 8, 9`).
2. **Return `dp[n - 1]`, the value at the last index, instead of the maximum over `dp`.** `dp[n - 1]` only counts subsequences that end at the last element. Counterexample: `nums = [1, 3, 6, 7, 9, 4, 10, 5, 6]`. Here `dp[8]` is `5`, but the subsequence `1, 3, 6, 7, 9, 10` ends earlier and has length `6`, which is the correct answer.
3. **Compare with `<=` (table form) or use an upper-bound search (fast form).** Both accept equal values as an increase. Counterexample: `nums = [7, 7, 7, 7, 7, 7, 7]`. The wrong versions report `7`, and the correct answer is `1`. The input `[2, 2, 3, 3, 4, 4]` also exposes it: the wrong versions report `6`, and the correct answer is `3`.
4. **Look for the longest run of neighboring elements that increase (a contiguous subarray) instead of a subsequence.** Skipping elements is allowed, so the best subsequence can be much longer than any run. Counterexample: `nums = [0, 1, 0, 3, 2, 3]`. The longest increasing run has length `2` (`0, 1`, or `0, 3`, or `2, 3`), but `0, 1, 2, 3` skips over the dips and has length `4`.
5. **Read the final `tails` array as the answer subsequence.** Its length is right, but its contents may not be a subsequence of the input. Counterexample: `nums = [-1, -2, 0, -3, 1]`. The final `tails` is `[-3, 0, 1]`, but `-3` sits after `0` in the input, so `-3, 0, 1` is not a subsequence. The length `3` is still the correct answer (`-1, 0, 1`). Rebuilding a real subsequence needs predecessor links (see Variants).

## Variants

1. **Return the subsequence itself, not just its length.** In the table form keep a `parent[i]` that holds the index `j` that gave the best `dp[i]`, then walk back from the index with the largest `dp`. In the fast form store the input index of each tail, and for every value also store the index of the tail just to its left at the moment it was placed; walk back from the last stored tail.
2. **Count how many longest increasing subsequences exist (LC 673).** Keep a second array `count[i]` next to `dp[i]`. When a longer predecessor is found, reset the count to the predecessor's count; when an equally long one is found, add its count.
3. **Non-decreasing version (equal values allowed).** In the table form change `<` to `<=`. In the fast form switch from the lower-bound search to an upper-bound search (first entry strictly greater than `x`).
4. **Rolling-array note.** Unlike the two-row tricks in other tables, this table has no smaller form: `dp[i]` may read any earlier cell, so all `n` cells stay in use. The `tails` array is also O(n) space, so its gain is speed, not memory.
5. **Nested envelopes (LC 354).** Sort the pairs by width ascending and, for equal widths, by height descending, then run the strict LIS on the heights. The descending tie-break stops two envelopes of the same width from being chained.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `nums=[10,9,2,5,3,7,101,18]` | `4` | mixed input where several subsequences tie for the best length |
| 2 | `nums=[0,1,0,3,2,3]` | `4` | repeated values and dips; the best subsequence skips elements (the longest run gives only 2) |
| 3 | `nums=[7,7,7,7,7,7,7]` | `1` | all equal, strictness (a non-strict comparison gives 7) |
| 4 | `nums=[1]` | `1` | n == 1 |
| 5 | `nums=[1,2,3,4,5]` | `5` | strictly increasing, answer is n |
| 6 | `nums=[5,4,3,2,1]` | `1` | strictly decreasing |
| 7 | `nums=[4,10,4,3,8,9]` | `3` | large early value is a dead end; greedy from the first element gives 2 |
| 8 | `nums=[1,3,6,7,9,4,10,5,6]` | `6` | best subsequence ends before the last index, so the answer is max of dp, not dp[n-1] (which is 5) |
| 9 | `nums=[-1,-2,0,-3,1]` | `3` | negative values; the final tails array `[-3,0,1]` is not a real subsequence |
| 10 | `nums=[2,2,3,3,4,4]` | `3` | duplicates in runs; a non-strict comparison gives 6 |
| 11 | `nums=[3,5,6,2,5,4,19,5,6,7,12]` | `6` | longer input; the best subsequence `2,4,5,6,7,12` skips the early `3,5,6` |
| 12 | `nums=[10000,-10000,0,10000]` | `3` | values at the stated bounds |
