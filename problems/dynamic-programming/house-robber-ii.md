# House Robber II
`ref: LC 213` · Difficulty: Medium · Pattern: linear DP run twice (exclude the first house, exclude the last house), two rolling variables, O(1) space

## Problem

Houses stand in a circle, and `nums[i]` is the amount of money stored in house `i`. Adjacent houses cannot both be taken, and because the houses form a circle, the first and the last house are also adjacent to each other. Return the maximum total amount that can be taken without ever taking two adjacent houses.

Input: an integer array `nums`, where each entry is the money in the house at that position, arranged in a circle.
Output: a single integer, the maximum total that can be taken under the no-two-adjacent-houses rule, including the circular adjacency between the first and last positions.

## Constraints

- `1 <= nums.length <= 100`
- `0 <= nums[i] <= 1000`
- With `n` up to `100`, a brute-force search over all subsets is `2^100`, far too slow regardless of how small the bound looks; a linear-time approach is expected.

## Worked examples

1. `nums = [2, 10, 2]` -> `10`. With only three houses arranged in a circle, every house is adjacent to both of the other two (0-1, 1-2, 2-0), so at most one house can ever be taken. The largest single house wins.
2. `nums = [3, 6, 4, 8, 3]` -> `14`. The optimal set is houses at indices 1 and 3 (values 6 and 8). Neither the first house (index 0, value 3) nor the last house (index 4, value 3) belongs to the optimal set, even though both are available and non-adjacent to each other in isolation.
3. `nums = [5]` -> `5`. A single house has no other house to conflict with, so it is always taken in full.
4. `nums = [10, 1, 10, 1, 10, 1]` -> `30`. The optimal set takes every other house starting from index 0: indices 0, 2, and 4, for a total of 30. Index 5 (the last house) is left untaken, which is exactly why taking index 0 together with indices 2 and 4 does not violate the circular adjacency between index 0 and index 5.

## Edge cases checklist

- `n == 1` (a single house; there is no second house for it to conflict with, and the "first and last are adjacent" rule is vacuous since the first and last house are the same house).
- `n == 2` (two houses, each adjacent to the other on both sides of the circle; the answer reduces to the larger of the two values).
- `n == 3` with the largest value in the middle house (all three houses are mutually adjacent, so only one house can ever be taken).
- All values equal (ties do not change how many non-adjacent houses fit around the circle, only which specific ones are chosen).
- All values zero (the total is 0 no matter which legal set of houses is chosen).
- The optimal set includes the first house and excludes the last.
- The optimal set includes the last house and excludes the first.
- The optimal set includes neither end house, favoring interior houses instead.
- Values that alternate between large and small amounts, which stresses whether the DP correctly steps past a small house to keep collecting large houses two apart.
- The upper bound of the input size, `n == 100`, and the extreme per-house values `0` and `1000`, to confirm nothing overflows or behaves differently at the stated limits (a 100-entry array of values up to 1000 sums to at most 100000, well within `int` range).

## Approach

### Brute force

Recurse over the houses from index 0 to `n - 1`, and at each house choose to take it or skip it, carrying along whether index 0 was taken so that the decision at index `n - 1` can forbid taking it when index 0 was already taken. This correctly accounts for the circular adjacency, but it explores two branches at every house, giving `O(2^n)` time in the worst case. For `n` up to 100 this is not a matter of being merely slow; `2^100` is far beyond what could ever finish, so this approach is only useful as a way to reason about correctness, never as something to actually run for anything but the smallest inputs.

### Optimal

Split the circle into two linear ranges and solve each with the ordinary (non-circular) House Robber DP:

- Range A: indices `0` through `n - 2` (the whole array except the last house).
- Range B: indices `1` through `n - 1` (the whole array except the first house).

Run the linear DP on each range and take the larger of the two results.

This split is exhaustive because in any circle, the first house (index 0) and the last house (index `n - 1`) are adjacent, so no valid selection can ever take both of them at once. That means every valid selection falls into at least one of two groups: selections that do not take the last house (fully contained in Range A), and selections that do not take the first house (fully contained in Range B). A selection that takes neither end is contained in both ranges, which is fine since the two ranges are solved independently and only their best results are compared; nothing is added together across them. Because every valid circular selection lives in at least one of the two ranges, and each range is solved exactly (no circular constraint applies inside a range that already excludes one of the two adjacent ends), the larger of the two range results is the true circular optimum.

The linear DP on a range uses two rolling variables instead of a table. Walking the range left to right, `prev1` holds the best total using houses seen so far that is allowed to end by taking the current house, and `prev2` holds the best total using houses strictly before the current one. At each step, `cur = max(prev1, prev2 + nums[i])`: either skip house `i` and keep the best total that already ends at or before `i - 1` (`prev1`), or take house `i` and add it to the best total that stops before `i - 1` (`prev2 + nums[i]`, since taking house `i` forbids also taking house `i - 1`). After the last index in the range, `prev1` holds the answer for that whole range.

`n == 1` needs explicit handling before this split runs. With only one house, that house is simultaneously the first and the last, so both ranges as defined above degenerate to an empty range (Range A becomes indices `0` through `-1`, and Range B becomes indices `1` through `0`), which would incorrectly report a total of 0 for a house that should simply be taken. A direct check, "if there is exactly one house, take it," avoids this.

`n == 2` does not need a separate special case. Range A becomes the single index `0`, and Range B becomes the single index `1`; the linear DP over a one-house range simply returns that house's value, so the two-range formula already reduces to `max(nums[0], nums[1])` on its own.

**Key invariant:** for a linear (non-circular) range of houses, at every position `i` inside that range, `prev1` equals the best achievable total using only the houses from the start of the range up through `i`, with no restriction on whether house `i` itself is included, and `prev2` equals that same best achievable total restricted to houses from the start of the range up through `i - 1`. Because `cur` is always computed as `max(prev1, prev2 + nums[i])`, it never combines house `i` with house `i - 1` in the same total, and it always considers both "skip house `i`" and "take house `i`" against the correct earlier state. By the time the loop reaches the last index of the range, `prev1` holds the true maximum for the entire range, which is exactly what each of the two range calls needs to return.

### Step-by-step trace

Trace of the linear DP over Range A (indices 0 through 3) for `nums = [3, 6, 4, 8, 3]` (`n = 5`, so Range A excludes index 4):

| i | nums[i] | prev2 before | prev1 before | cur = max(prev1, prev2 + nums[i]) | prev2 after | prev1 after |
|---|---|---|---|---|---|---|
| 0 | 3 | 0 | 0 | 3 | 0 | 3 |
| 1 | 6 | 0 | 3 | 6 | 3 | 6 |
| 2 | 4 | 3 | 6 | 7 | 6 | 7 |
| 3 | 8 | 6 | 7 | 14 | 7 | 14 |

Range A returns `14`. Range B (indices 1 through 4, excluding index 0) also returns `14` for this array, so the overall answer is `max(14, 14) = 14`, matching worked example 2. The path that achieves 14 in Range A takes indices 1 and 3 (values 6 and 8), not index 0, which is consistent with worked example 2's note that neither end house is part of the optimal set.

## Java 8 solution
```java
public class HouseRobberII {

    // Maximum money that can be taken from houses arranged in a circle, where
    // the first and last houses are adjacent and no two adjacent houses can
    // both be taken.
    public static int solve(int[] nums) {
        int n = nums.length;
        if (n == 1) {
            return nums[0];
        }
        return Math.max(robLine(nums, 0, n - 2), robLine(nums, 1, n - 1));
    }

    // Classic linear House Robber DP over the inclusive range [start, end] of
    // nums, using two rolling variables instead of a table. Returns 0 when
    // the range is empty (start > end).
    private static int robLine(int[] nums, int start, int end) {
        int prev2 = 0;
        int prev1 = 0;
        for (int i = start; i <= end; i++) {
            int cur = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums);
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

        check(1, new int[]{5}, 5, fail, total);
        check(2, new int[]{2, 3}, 3, fail, total);
        check(3, new int[]{2, 10, 2}, 10, fail, total);
        check(4, new int[]{4, 4, 4, 4, 4, 4}, 12, fail, total);
        check(5, new int[]{0, 0, 0, 0, 0}, 0, fail, total);
        check(6, new int[]{10, 1, 1, 1}, 11, fail, total);
        check(7, new int[]{1, 1, 1, 10}, 11, fail, total);
        check(8, new int[]{3, 6, 4, 8, 3}, 14, fail, total);
        check(9, new int[]{10, 1, 10, 1, 10, 1}, 30, fail, total);
        check(10, new int[]{5, 1, 1, 5}, 6, fail, total);
        check(11, new int[]{5, 1, 5, 1, 1}, 10, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n): each of the two `robLine` calls makes a single pass over its range, and a range is never longer than `n`, so the two calls together do at most `2n` work, which is `O(n)`. Space O(1): each call only keeps two rolling integer variables; there is no table, recursion stack, or auxiliary array proportional to `n`.

## Java 8 pitfalls for this problem

- Skipping the explicit `n == 1` check. Both ranges as defined, `[0, n - 2]` and `[1, n - 1]`, become empty when `n == 1` (the first range is `[0, -1]`), and an empty range correctly returns 0 from `robLine`. Without the direct check, a single house with a positive value would be reported as an answer of 0 instead of that house's own value.
- Off-by-one in the range bounds passed into `robLine`. The two ranges are `[0, n - 2]` and `[1, n - 1]`, both inclusive on both ends; writing `n - 1` instead of `n - 2` for the first range's upper bound would let it include the last house, defeating the entire point of excluding it.
- Looping with `i < end` instead of `i <= end` inside `robLine`. Since `end` is an inclusive bound, a strict `<` silently drops the last house of whichever range is being processed.
- Not resetting `prev1` and `prev2` to `0` between the two `robLine` calls. Since the reference implementation declares them as fresh local variables inside `robLine` itself, this is not an issue here, but a version that tries to reuse shared variables across both calls (for example, to "save" a couple of lines) would leak the first call's final state into the second call's starting state and corrupt the second range's result.
- Treating the two ranges as something to combine rather than compare. The correct combination is `Math.max(...)` of the two range results, not a sum; each range already produces a complete, independent answer for the whole circle, not a partial answer meant to be added to the other range's partial answer.

## Wrong approaches and why they fail

1. **Run the ordinary linear House Robber DP once over the entire array, with no adjustment for the circular adjacency between the first and last house.** This lets the DP freely combine the first and last houses in the same total whenever that sum is large, which is invalid once the array wraps into a circle. Counterexample: `nums = [5, 1, 1, 5]`. The plain linear DP over the whole array finds it can take index 0 and index 3 together for `5 + 5 = 10`, but those two houses are adjacent once the array is treated as a circle, so that selection is not allowed. The correct circular answer, found by excluding one end at a time, is `6`.
2. **Take every house at an even index, take every house at an odd index, and report the larger of the two sums.** This assumes even and odd indices never sit next to each other around the circle, which fails whenever `n` is odd: index 0 and index `n - 1` are adjacent, and when `n` is odd both of those indices are even, so the "even" sum can silently combine two adjacent houses. Counterexample: `nums = [5, 1, 5, 1, 1]` (`n = 5`). The even-index sum takes indices 0, 2, and 4 for `5 + 5 + 1 = 11`, but index 0 and index 4 are adjacent around this five-house circle, so that total is not achievable by any legal selection. The correct answer, found by excluding one end at a time, is `10`.
3. **Apply the two-range formula, `max(robLine(0, n - 2), robLine(1, n - 1))`, without a special case for `n == 1`.** Both ranges degenerate to empty when `n == 1`, and an empty range's total is 0, so this reports 0 for any single house regardless of its value. Counterexample: `nums = [5]`. The formula alone gives `0`; the correct answer is `5`, since a lone house has nothing adjacent to it and can always be taken in full.

## Variants

1. **House Robber I** (the non-circular original) is the same linear DP used here as `robLine`, applied once to the whole array with no adjacency between the first and last house.
2. **Return which houses are taken, not just the total.** This requires keeping a parent pointer or a boolean choice array alongside the rolling totals for at least one of the two ranges (whichever produced the larger result), since the two-variable version by itself only remembers the totals, not the choices that produced them.
3. **Houses arranged in a circle, but any two houses within a distance of 2 conflict, not just direct neighbors.** This changes the linear sub-problem's transition from looking one step back (`prev2 + nums[i]`) to looking two steps back, and the circular exclusion has to rule out more than just the single adjacent pair at the seam.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `nums=[5]` | `5` | n == 1, no adjacency possible |
| 2 | `nums=[2,3]` | `3` | n == 2, reduces to the larger value |
| 3 | `nums=[2,10,2]` | `10` | n == 3 with the largest value in the middle |
| 4 | `nums=[4,4,4,4,4,4]` | `12` | all equal values |
| 5 | `nums=[0,0,0,0,0]` | `0` | zeros everywhere |
| 6 | `nums=[10,1,1,1]` | `11` | optimal set includes the first house, excludes the last |
| 7 | `nums=[1,1,1,10]` | `11` | optimal set includes the last house, excludes the first |
| 8 | `nums=[3,6,4,8,3]` | `14` | optimal set includes neither end house |
| 9 | `nums=[10,1,10,1,10,1]` | `30` | alternating large and small values |
| 10 | `nums=[5,1,1,5]` | `6` | counterexample to ignoring the circular adjacency entirely |
| 11 | `nums=[5,1,5,1,1]` | `10` | counterexample to the even/odd-index-sum approach on odd n |
