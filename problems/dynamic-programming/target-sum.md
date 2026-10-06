# Target Sum
`ref: LC 494` · Difficulty: Medium · Pattern: count subsets with a fixed sum (subset-sum knapsack) after the algebra that turns +/- signs into one subset

## Problem

You are given an array `nums` of non-negative integers and an integer `target`. Put a `+` or a `-` sign in front of every number in `nums`, then add up the signed numbers. Return the number of different sign choices that give a total of exactly `target`. Two choices are different when at least one position gets a different sign, even if the numbers at those positions are equal. A `0` can take either sign, and both signs count as different choices.

Input: an integer array `nums` and an integer `target`.
Output: a single integer, the number of sign choices whose signed total equals `target`.

## Constraints

- `1 <= nums.length <= 20`
- `0 <= nums[i] <= 1000`
- `0 <= sum(nums) <= 1000`
- `-1000 <= target <= 1000`
- Trying both signs for every number makes `2^n` totals, about a million at `n = 20`. That still runs at these limits, but it doubles with every extra number. The reduction below needs only about `n * sum`, at most `20 * 1000 = 20000` steps, and it keeps working when `n` is large as long as the sum stays small. The count is at most `2^20 = 1048576`, so it fits in an `int`.

## Worked examples

1. `nums = [1, 1, 1, 1, 1]`, `target = 3` -> `5`. The total is 5. To reach 3, exactly one of the five 1s must get a minus sign (`5 - 2 = 3`). There are five positions to choose from, so there are five choices.
2. `nums = [1]`, `target = 1` -> `1`. The only choice that works is `+1`.
3. `nums = [1, 2]`, `target = 2` -> `0`. The four choices give `3`, `1`, `-1` and `-3`. None of them is 2.
4. `nums = [1, 2, 3]`, `target = 0` -> `2`. The two choices are `+1 +2 -3` and `-1 -2 +3`.
5. `nums = [0, 0, 0, 0, 0, 0, 0, 0, 1]`, `target = 1` -> `256`. The `1` must be positive. Each of the eight zeros can take either sign without changing the total, so there are `2^8 = 256` choices.

## Edge cases checklist

- `|target| > sum(nums)` (for example `[1]` with 3). The signed total can never be that large, so the answer is 0.
- `sum(nums) + target` odd (`[1, 2]` with 2). The answer is 0, even though `|target|` is not larger than the sum.
- A negative `target` so low that `sum(nums) + target` is negative (`[1, 2, 3]` with -8). It must be rejected before any array is sized from it.
- A negative odd `sum(nums) + target` (`[1, 2, 3]` with -7). It is 0, and the oddness test must work for negative numbers.
- `target` equal to the sum (all plus, `[1, 2, 3]` with 6 is 1) and equal to minus the sum (all minus, `[1, 2, 3]` with -6 is 1).
- Symmetry: flipping every sign turns `target` into `-target`, so both give the same answer (`[1, 1, 1, 1, 1]` with 3 and with -3 are both 5). It is a useful self-check.
- Zeros: each zero doubles the count (`[0]` with 0 is 2, and eight zeros plus a 1 is 256). A solution that skips zeros is wrong.
- Equal values at different positions count as different choices (`[1, 1, 1, 1, 1]` gives 5, not 1).
- A single element (`[1]` with 1 and with 3).
- `target == 0` with a nonzero total (`[1, 2, 3]` is 2).
- The upper bound: 20 numbers with a sum of 1000 (not among the tests here).

## Approach

### Brute force

Walk through the array and, at each position, try both signs. Let `go(i, running)` be the number of ways to finish from position `i` when the signed total so far is `running`. At the end (`i == n`) it is 1 if `running == target` and 0 otherwise. Otherwise `go(i, running) = go(i + 1, running + nums[i]) + go(i + 1, running - nums[i])`. This visits `2^n` leaves. It is fine for `n = 20`, but it is a poor model for the pattern. Only `n * (2 * sum + 1)` different `(i, running)` pairs exist, and a table over them (shifting `running` by `sum` so negative totals get an index) is already fast. The reduction below does better, with one dimension and about half the width.

### Optimal

Let `P` be the sum of the numbers that get a plus sign and `N` the sum of the numbers that get a minus sign, with `N` written as a positive number. Every number is in exactly one of the two groups, and the signed total is `P - N`:

- `P + N = total`, where `total` is the sum of all of `nums`
- `P - N = target`

Add the two lines: `2 * P = total + target`, so `P = (total + target) / 2`.

Choosing a sign for every number is the same as choosing which positions go into the plus group. Every set of positions with sum `P` gives exactly one sign choice that reaches `target` (everything else gets a minus sign), and every such sign choice gives one set. So the answer is the number of subsets of `nums`, chosen by position, whose sum is exactly `P`. Equal numbers at different positions are different subsets, and a zero can be in the subset or out of it.

Before counting, check that `P` is a valid sum:

- If `|target| > total`, the answer is 0.
- If `total + target` is odd, `P` would be a fraction, so the answer is 0.
- Once both checks pass, `total + target` is not negative and is even, so `P` is a whole number from `0` to `total`.

Then count subsets with sum `P`. Let `dp[s]` be the number of subsets of the numbers processed so far whose sum is exactly `s`, for `s` from `0` to `P`. Only the empty subset exists at the start, so `dp[0] = 1` and every other cell is 0. For each number `x`, run `s` from `P` down to `x`:

`dp[s] += dp[s - x]`

The answer is `dp[P]`.

**Direction matters.** The loop runs from `P` down to `x`. When it reaches sum `s`, every cell below `s` still holds its value from before `x` was processed, so `dp[s - x]` counts only subsets that do not contain this position. That limits each position to one use. An upward loop would let one position be used again and again. Counterexample: `nums = [1, 3]`, `target = 0`. The total is 4 and `P = 2`. The upward loop sets `dp[1] = 1` and then `dp[2] = dp[1] = 1`, so the single `1` is used twice and the method says 1. The correct answer is 0, because `+1 +3`, `+1 -3`, `-1 +3` and `-1 -3` give 4, -2, 2 and -4.

**Zeros.** When `x == 0` the loop runs from `P` down to 0 and does `dp[s] += dp[s]` on every cell, which doubles the whole table. That is the right effect: a zero can sit in the plus group or the minus group.

**Key invariant:** after the first `k` numbers have been processed, `dp[s]` is the exact number of subsets of those `k` positions whose sum is `s`, for every `s` from `0` to `P`. While number `k + 1` (value `x`) is processed and the loop stands at `s`, every cell below `s` is still at its value from after `k` numbers. So `dp[s - x]` counts the subsets of the first `k` positions with sum `s - x`, and adding `x` to each gives the subsets that do contain position `k + 1`. Together with the old `dp[s]`, which counts the subsets that do not contain it, the two groups cover all subsets of the first `k + 1` positions exactly once.

### Step-by-step trace

Trace for `nums = [1, 1, 1, 1, 1]` and `target = 3`. The total is 5, so `P = (5 + 3) / 2 = 4`, and the table has cells `0` to `4`.

| after processing | s=0 | s=1 | s=2 | s=3 | s=4 |
|---|---|---|---|---|---|
| start (only the empty subset) | 1 | 0 | 0 | 0 | 0 |
| first 1 | 1 | 1 | 0 | 0 | 0 |
| second 1 | 1 | 2 | 1 | 0 | 0 |
| third 1 | 1 | 3 | 3 | 1 | 0 |
| fourth 1 | 1 | 4 | 6 | 4 | 1 |
| fifth 1 | 1 | 5 | 10 | 10 | 5 |

While the third 1 is processed, the loop visits `s = 4, 3, 2, 1`. At `s = 4` it reads `dp[3]`, which is still 0. At `s = 3` it reads `dp[2] = 1` and sets `dp[3] = 1`. At `s = 2` it reads `dp[1] = 2` and sets `dp[2] = 3`. At `s = 1` it reads `dp[0] = 1` and sets `dp[1] = 3`. Each row is a row of Pascal's triangle, which is right: the number of subsets of `k` equal numbers with sum `s` is the number of ways to choose `s` of the `k` positions. The answer is `dp[4] = 5`, the number of ways to choose which four of the five 1s get the plus sign, which matches worked example 1.

## Java 8 solution
```java
public class TargetSum {

    // Number of ways to put a + or - sign in front of every number in nums so
    // that the signed numbers add up to target. Two sign assignments are
    // different when they differ at any position, even if the numbers there are
    // equal (and +0 and -0 are different).
    //
    // Let P be the sum of the numbers that get a plus sign and N the sum of the
    // numbers that get a minus sign. Then P + N = total and P - N = target, so
    // P = (total + target) / 2. Choosing signs is the same as choosing which
    // positions form the plus group, so the answer is the number of subsets of
    // nums whose sum is exactly P.
    public static int solve(int[] nums, int target) {
        int total = 0;
        for (int x : nums) {
            total += x;
        }
        // |target| > total is out of reach. An odd total + target would make P
        // a fraction. Test with != 0, because % can return -1 for a negative
        // number. After this line total + target is not negative, so P is a
        // whole number from 0 to total.
        if (Math.abs(target) > total || (total + target) % 2 != 0) {
            return 0;
        }
        int plusSum = (total + target) / 2;

        // dp[s] is the number of subsets of the numbers processed so far that
        // sum to s. The empty subset gives sum 0, so dp[0] is 1.
        int[] dp = new int[plusSum + 1];
        dp[0] = 1;
        for (int x : nums) {
            // Walk the sums from high to low so that dp[s - x] still describes
            // subsets without x. That limits each position to one use. For
            // x == 0 the loop reaches s == 0 and doubles every cell, which is
            // right: a zero can sit in the plus group or the minus group.
            for (int s = plusSum; s >= x; s--) {
                dp[s] += dp[s - x];
            }
        }
        return dp[plusSum];
    }

    private static void check(int caseNum, int[] nums, int target, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums, target);
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

        check(1, new int[]{1, 1, 1, 1, 1}, 3, 5, fail, total);
        check(2, new int[]{1}, 1, 1, fail, total);
        check(3, new int[]{1, 1, 1, 1, 1}, -3, 5, fail, total);
        check(4, new int[]{1}, 3, 0, fail, total);
        check(5, new int[]{0}, 0, 2, fail, total);
        check(6, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1}, 1, 256, fail, total);
        check(7, new int[]{1, 2}, 2, 0, fail, total);
        check(8, new int[]{1, 2, 3}, 6, 1, fail, total);
        check(9, new int[]{1, 2, 3}, -6, 1, fail, total);
        check(10, new int[]{1, 2, 3}, 0, 2, fail, total);
        check(11, new int[]{1, 2, 3}, -8, 0, fail, total);
        check(12, new int[]{1, 2, 3}, -7, 0, fail, total);
        check(13, new int[]{100, 200, 300, 400}, 200, 2, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n * P), where `P = (total + target) / 2` is at most `total`: each of the `n` numbers triggers one pass over at most `P` cells, plus one pass of `n` steps to compute the total. With the stated bounds that is at most `20 * 1000 = 20000` steps. Like the other subset-sum tables, it is pseudo-polynomial, because it grows with the size of the numbers and not only with `n`. Space O(P): one `int` array of `P + 1` cells, at most `1001`.

## Java 8 pitfalls for this problem

- Dividing before checking parity. `(total + target) / 2` rounds an odd value toward zero, so `[1, 2]` with 2 would look for `P = 2`, find `{2}` and answer 1 instead of 0. Reject odd `total + target` first.
- Testing oddness with `% 2 == 1`. In Java the remainder keeps the sign of the left side, so `-1 % 2` is `-1` and the test misses negative odd values. Use `% 2 != 0`. Also, `-1 / 2` is `0` in Java (division truncates toward zero), so a `% 2 == 1` test combined with a skipped range check turns `[1, 2, 3]` with -7 into `P = 0` and an answer of 1, because the empty subset has sum 0. The correct answer is 0.
- Sizing the array from a negative `P`. For `[1, 2, 3]` with -8, `P = -1` gives `new int[0]`, and the write `dp[0] = 1` throws `ArrayIndexOutOfBoundsException`. When `P` is below `-1`, `new int[P + 1]` throws `NegativeArraySizeException`. Check `|target| > total` before allocating anything.
- Allocating `new int[P]` instead of `new int[P + 1]`. Cell `dp[P]` must exist, and the failure is an `ArrayIndexOutOfBoundsException`.
- Forgetting `dp[0] = 1`. A new `int[]` is all zeros, so nothing is ever counted and every answer is 0.
- Writing the loop bound as `s > x` instead of `s >= x`. That skips `s == x`, where the number alone fills the sum, and it skips `s == 0` when `x` is 0, so `[0]` with 0 gives 1 and not 2.
- Running the inner loop upward. It compiles and passes some inputs, but it reuses a position (see the `[1, 3]` counterexample above).
- Letting the loop go below `x`, for example `s >= 0` for a positive `x`. Then `s - x` is negative and the array access throws.
- Integer overflow is not a risk at these limits: the sum is at most 1000 and the count is at most `2^20`. If `n` could reach 31 or more, a count of up to `2^n` would no longer fit in an `int`, and the table would need `long`.
- Recursion depth is shallow here (at most 20 levels), so a top-down version is safe. A memo on `(position, running total)` needs the running total shifted by `total`, because negative totals have no array index, and its default 0 cannot tell "not computed" from "zero ways". Use a marker such as `-1`.

## Wrong approaches and why they fail

1. **Skip the parity check and use integer division for `P`.** An odd `total + target` rounds down to a value that can be reachable even though no sign choice works. Counterexample: `nums = [1, 2]`, `target = 2`. The total is 3, `P` rounds to `(3 + 2) / 2 = 2`, and `{2}` has sum 2, so this approach says `1`. The correct answer is `0`.
2. **Skip the range check on `target`.** Counterexample: `nums = [1, 2, 3]`, `target = -8`. Then `P = -1`, the table has zero cells, and the code crashes with `ArrayIndexOutOfBoundsException`. The correct answer is `0`.
3. **Run the inner loop upward.** A number can then be added again and again. Counterexample: `nums = [1, 3]`, `target = 0`. It returns `1`. The correct answer is `0`.
4. **Treat zeros as no-ops.** Skipping the zeros removes the doubling. Counterexample: `nums = [0]`, `target = 0` returns `1`, and the correct answer is `2` (`+0` and `-0`). For `nums = [0, 0, 0, 0, 0, 0, 0, 0, 1]` with `target = 1` it returns `1`, and the correct answer is `256`.
5. **Compute only which totals are reachable (a yes or no, or a set of totals).** The question asks for the number of sign choices, not whether one exists. Counterexample: `nums = [1, 1, 1, 1, 1]`, `target = 3`. The total 3 is reachable, so this approach says `1`, and the correct answer is `5`.

## Variants

1. **Signed-total table without the algebra.** Let `ways[i][t + total]` be the number of sign choices for the first `i` numbers with signed total `t`, and move each number to `t + x` and `t - x`. It needs no parity or range trick and works for any `target`, but it uses a table twice as wide.
2. **Is it possible at all?** The yes-or-no question for `target = 0` is Partition Equal Subset Sum (LC 416). See `partition-equal-subset-sum.md`.
3. **Count the subsets with a given sum.** This is the core of the page: the same table with `P` replaced by the wanted sum.
4. **Smallest absolute difference between the two groups (LC 1049).** Fill the same reachability table up to `total / 2`, take the largest reachable sum `s`, and the answer is `total - 2 * s`.
5. **List the sign choices.** A backtracking search that tries `+` and `-` at each position. The output can be as large as the count, so it is only practical for small cases.
6. **Negative numbers in the array.** The algebra still holds, but the table needs an offset because subset sums can be negative, and the range check becomes a check against the sum of the absolute values.
7. **Unlimited reuse of each number.** If every number may be used any number of times in the plus group, the loop turns upward and the problem becomes the one in `coin-change-ii.md`.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `nums=[1,1,1,1,1], target=3` | `5` | the standard example; equal values at different positions count separately |
| 2 | `nums=[1], target=1` | `1` | a single element |
| 3 | `nums=[1,1,1,1,1], target=-3` | `5` | negative target; the same answer as `+3` by symmetry |
| 4 | `nums=[1], target=3` | `0` | even `total + target`, but `P = 2` is more than the total |
| 5 | `nums=[0], target=0` | `2` | a zero gives two sign choices (fails if zeros are skipped or the loop bound is `s > x`) |
| 6 | `nums=[0,0,0,0,0,0,0,0,1], target=1` | `256` | eight zeros double the count eight times |
| 7 | `nums=[1,2], target=2` | `0` | odd `total + target`; integer division would answer 1 |
| 8 | `nums=[1,2,3], target=6` | `1` | `target` equals the total, all plus |
| 9 | `nums=[1,2,3], target=-6` | `1` | `target` equals minus the total, `P = 0` (the empty subset) |
| 10 | `nums=[1,2,3], target=0` | `2` | `+1 +2 -3` and `-1 -2 +3`; the target is zero but the total is not |
| 11 | `nums=[1,2,3], target=-8` | `0` | `P = -1`; sizing the table from it throws, so the range check must come first |
| 12 | `nums=[1,2,3], target=-7` | `0` | negative odd `total + target`; with a `% 2 == 1` test and no range check the answer comes out as 1 |
| 13 | `nums=[100,200,300,400], target=200` | `2` | larger values and a total of 1000, `P = 600`, subsets `{200, 400}` and `{100, 200, 300}` |
