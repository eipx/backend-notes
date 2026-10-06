# Burst Balloons
`ref: LC 312` · Difficulty: Hard · Pattern: interval DP with padded ends; choose the LAST balloon to burst inside an open interval

## Problem

You have a row of `n` balloons, and balloon `i` has the number `nums[i]` painted on it. You will burst all of them, one at a time, in any order you like. Bursting balloon `i` earns `left * nums[i] * right` coins, where `left` and `right` are the numbers on the nearest balloons that have not burst yet on each side of balloon `i`. If there is no unburst balloon on one side, that side counts as a balloon with the number `1`. That imaginary balloon cannot be burst and is not counted among the `n`. Return the largest total number of coins that any bursting order can earn.

Input: an integer array `nums` with the numbers on the balloons, in row order.
Output: a single integer, the maximum total number of coins over all bursting orders.

## Constraints

- `1 <= nums.length <= 300`
- `0 <= nums[i] <= 100`
- There are `n!` bursting orders, and even a memo over the set of balloons that are still left has `2^n` states, so both are far too slow at `n = 300`. The interval table below has one cell for each pair of positions `(i, j)` in the padded row, which is `45451` cells, and the work is one step per triple `i < k < j`, which is `4545100` steps at the limit.
- The answer cannot overflow `int`: it is at most `300 * 100 * 100 * 100 = 3 * 10^8`, and the largest real answer here (300 balloons, all `100`) is `298010100`.

## Worked examples

1. `nums = [3, 1, 5, 8]` -> `167`. Burst the `1` first (`3 * 1 * 5 = 15`), then the `5` (`3 * 5 * 8 = 120`), then the `3` (`1 * 3 * 8 = 24`), then the `8` (`1 * 8 * 1 = 8`). The total is `15 + 120 + 24 + 8 = 167`.
2. `nums = [1, 5]` -> `10`. Burst the `1` first (`1 * 1 * 5 = 5`), then the `5` (`1 * 5 * 1 = 5`). The other order earns only `5 + 1 = 6`.
3. `nums = [7]` -> `7`. With one balloon, both neighbors are the imaginary `1`, so the burst earns `1 * 7 * 1`.
4. `nums = [3, 0, 5]` -> `20`. Burst the `0` first. It earns nothing, but it was standing between the `3` and the `5`, and now they are neighbors. Then the `3` earns `1 * 3 * 5 = 15` and the `5` earns `1 * 5 * 1 = 5`. Any order that bursts the `3` or the `5` while the `0` is still beside it earns less.

## Edge cases checklist

- One balloon (both neighbors are the imaginary `1`, so the answer is the number itself).
- An empty row (outside the limits). Nothing can be burst, so the answer is 0, and the table code must cope with `n == 0`.
- Two balloons (each burst has at least one imaginary neighbor).
- Zeros: a `0` earns nothing when it bursts, but while it stands it blocks its two neighbors from becoming neighbors of each other. All zeros gives a total of 0.
- All ones, where every burst earns exactly `1`, so the total is `n`.
- A row that rises and the same row reversed. Reversing the row never changes the answer.
- The same value everywhere (a flat row), where ties between choices are everywhere.
- The largest values (`100` each), where one burst earns `10^6`.
- The upper bound `n = 300`, about 4.5 million inner steps (the largest case is among the tests).

## Approach

### Brute force

Try every order. Pick a balloon to burst first, add the coins it earns from its current neighbors, remove it from the row, and recurse on the shorter row. That tries every one of the `n!` orders. Remembering the best result for each set of balloons that are still left gives at most `2^n` states with up to `n` choices each, which is still impossible at `n = 300`.

The tempting way to cut the work is an interval table over the original row that picks the balloon to burst FIRST in an interval. It does not work. Once balloon `k` has burst, the balloons on its left and the balloons on its right become neighbors of each other, so the left part and the right part are no longer separate problems, and the coins for each side depend on what happens on the other.

### Optimal

Turn the choice around: pick the balloon that bursts LAST in an interval. First pad the row. Make an array `a` of length `n + 2` with `a[0] = 1`, `a[n + 1] = 1` and `a[1..n] = nums`. Now every real balloon has two neighbors in the array at all times, and the imaginary balloons are never burst.

Let `dp[i][j]`, for `0 <= i < j <= n + 1`, be the most coins that bursting every balloon strictly between positions `i` and `j` can earn, while positions `i` and `j` themselves are still standing. The interval is open: the two ends are not part of it.

- `dp[i][i + 1] = 0`: there is no balloon between two adjacent positions.
- For `j - i >= 2`, try every position `k` with `i < k < j` as the LAST balloon of the interval to burst, and take the best: `dp[i][j] = max over k of (dp[i][k] + dp[k][j] + a[i] * a[k] * a[j])`.

Here is why this splits cleanly. When `k` is the last balloon to burst in the interval, every other balloon between `i` and `j` has already burst, so `k` has only `a[i]` and `a[j]` beside it, and it earns `a[i] * a[k] * a[j]`. Before that, the balloons between `i` and `k` were bursting while `i` and `k` were both standing, so none of them can see anything outside `i..k`, which is exactly the problem `dp[i][k]`. The balloons between `k` and `j` are the problem `dp[k][j]`. The two parts cannot affect each other, because `k` stays between them the whole time.

The answer is `dp[0][n + 1]`. The table is filled by the gap `j - i`, from 2 up to `n + 1`. A cell with gap `g` reads cells with smaller gaps, which are final by then.

**Key invariant:** for every pair `i < j`, `dp[i][j]` is the exact maximum number of coins over all orders that burst the balloons strictly between `i` and `j`, with `i` and `j` present throughout. The base gap is correct, every order of such an interval has a last balloon `k`, and the part before it splits into two independent problems with smaller gaps, so by induction on the gap the corner cell `dp[0][n + 1]` is the answer for the whole row.

### Step-by-step trace

For `nums = [3, 1, 5, 8]` the padded row is `a = [1, 3, 1, 5, 8, 1]` (positions 0 to 5). Filled table, row `i` and column `j`; cells with `j <= i` are not used, and the cells with `j = i + 1` are the empty intervals.

| i \ j | 1 | 2 | 3 | 4 | 5 |
|---|---|---|---|---|---|
| 0 | 0 | 3 | 30 | 159 | 167 |
| 1 | - | 0 | 15 | 135 | 159 |
| 2 | - | - | 0 | 40 | 48 |
| 3 | - | - | - | 0 | 40 |
| 4 | - | - | - | - | 0 |

A few cells worked out. `dp[1][3]` has only the balloon at position 2 (the `1`) between the `3` and the `5`, so it is `a[1] * a[2] * a[3] = 3 * 1 * 5 = 15`. `dp[1][4]` has the balloons `1` and `5` between the `3` and the `8`. With the `1` (position 2) last, it is `dp[1][2] + dp[2][4] + 3 * 1 * 8 = 0 + 40 + 24 = 64`. With the `5` (position 3) last, it is `dp[1][3] + dp[3][4] + 3 * 5 * 8 = 15 + 0 + 120 = 135`. The best is `135`. `dp[0][4]` has `3`, `1`, `5` between the two ends. With the `3` last it is `0 + dp[1][4] + 1 * 3 * 8 = 135 + 24 = 159`, with the `1` last it is `51`, and with the `5` last it is `70`, so the cell is `159`.

For the whole row, `dp[0][5]` tries each balloon as the last one. The `3` gives `dp[0][1] + dp[1][5] + 1 * 3 * 1 = 0 + 159 + 3 = 162`, the `1` gives `52`, the `5` gives `75`, and the `8` gives `dp[0][4] + dp[4][5] + 1 * 8 * 1 = 159 + 0 + 8 = 167`. The best is `167`. Walking back: the `8` bursts last (`8` coins), and before that the `3` was last among `3, 1, 5` (`1 * 3 * 8 = 24`), before that the `5` was last among `1, 5` (`3 * 5 * 8 = 120`), and before that the `1` burst alone (`3 * 1 * 5 = 15`). Read in time order, the bursts are `1`, `5`, `3`, `8`, which is the order in worked example 1, and the total is `15 + 120 + 24 + 8 = 167`.

## Java 8 solution
```java
public class BurstBalloons {

    // Maximum coins from bursting every balloon. Bursting balloon i earns
    // nums[i - 1] * nums[i] * nums[i + 1] using the neighbors that are still
    // there, and a missing neighbor past either end counts as 1.
    //
    // The balloons are copied into a padded array a with a 1 at each end, so
    // that every real balloon always has two neighbors. dp[i][j] is the most
    // coins from bursting every balloon strictly between positions i and j of
    // a, while positions i and j themselves are still standing. The question
    // for each interval is which balloon k in it bursts LAST. At that moment
    // only a[i] and a[j] are left beside it, so it earns a[i] * a[k] * a[j],
    // and the two sides (i..k and k..j) are independent problems because k is
    // still standing between them the whole time.
    public static int solve(int[] nums) {
        int n = nums.length;
        int[] a = new int[n + 2];
        a[0] = 1;
        a[n + 1] = 1;
        for (int i = 0; i < n; i++) {
            a[i + 1] = nums[i];
        }

        // Cells with j == i + 1 hold an interval with no balloon inside, and
        // stay 0 from the allocation.
        int[][] dp = new int[n + 2][n + 2];
        for (int gap = 2; gap <= n + 1; gap++) {
            for (int i = 0; i + gap <= n + 1; i++) {
                int j = i + gap;
                // Coins are never negative, so 0 is a safe starting maximum.
                int best = 0;
                for (int k = i + 1; k < j; k++) {
                    int coins = dp[i][k] + dp[k][j] + a[i] * a[k] * a[j];
                    if (coins > best) {
                        best = coins;
                    }
                }
                dp[i][j] = best;
            }
        }
        return dp[0][n + 1];
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

        check(1, new int[]{3, 1, 5, 8}, 167, fail, total);
        check(2, new int[]{1, 5}, 10, fail, total);
        check(3, new int[]{}, 0, fail, total);
        check(4, new int[]{7}, 7, fail, total);
        check(5, new int[]{0, 0, 0}, 0, fail, total);
        check(6, new int[]{1, 1, 1, 1}, 4, fail, total);
        check(7, new int[]{3, 0, 5}, 20, fail, total);
        check(8, new int[]{2, 3, 4}, 36, fail, total);
        check(9, new int[]{1, 2, 3, 4, 5}, 110, fail, total);
        check(10, new int[]{5, 4, 3, 2, 1}, 110, fail, total);
        check(11, new int[]{2, 5, 1, 7, 3}, 179, fail, total);
        check(12, new int[]{100, 100, 100}, 1010100, fail, total);

        // 300 balloons worth 100 each: the largest input the limits allow.
        int[] big = new int[300];
        for (int i = 0; i < big.length; i++) {
            big[i] = 100;
        }
        check(13, big, 298010100, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n^3): there is one inner step for every triple of positions `i < k < j` in the padded row of `n + 2` positions, which is `(n + 2)(n + 1)n / 6` steps (`4545100` at `n = 300`), with constant work per step. Space O(n^2) for the table of `(n + 2) * (n + 2)` ints (`91204` ints at `n = 300`). The padded copy of the row adds O(n). A cell reads cells from many different intervals, so there is no simple way to cut the table down to a few rows.

## Java 8 pitfalls for this problem

- Missing the padding. Reading `nums[i - 1]` and `nums[i + 1]` directly throws an `ArrayIndexOutOfBoundsException` at the two ends, and patching it with `if` checks at every read makes the code long and error-prone. Copy the row into a new array of length `n + 2` with a `1` at each end. Java arrays cannot grow, so the copy is needed, and it also leaves the caller's array unchanged.
- Padding with 0 instead of 1. The imaginary balloon counts as `1`, and a `0` would make every burst at the ends earn nothing.
- Array sizing and the answer cell. The table is `new int[n + 2][n + 2]` and the answer is `dp[0][n + 1]`, the interval that covers all `n` real balloons. Reading `dp[0][n]` leaves out the last real balloon, and `dp[1][n]` leaves out both end balloons.
- Loop bounds. The gap runs from `2` to `n + 1`, and the start runs while `i + gap <= n + 1`. Writing `<` instead of `<=` in either place never fills the whole-row cell, and the method returns 0.
- Filling in the wrong order. Looping `i` upward with `j` inside reads `dp[k][j]` for `k > i` before it has been filled, and it is still 0. Fill by the gap, so that every cell reads only smaller gaps.
- Integer overflow. One burst earns at most `100 * 100 * 100 = 10^6` and the total is at most `3 * 10^8`, so `int` is safe. If the numbers could reach 1000, one product would be `10^9` and a total of 300 of them would overflow, so use `long`.
- Recursion depth. A top-down version goes at most `n = 300` levels deep (each call makes an interval smaller), which is safe on the default stack.
- Mutable default values. A new `int[][]` is full of zeros. That is exactly right for the empty intervals (`j == i + 1`), but `0` is also a legal answer for a non-empty interval (all balloons `0`), so a top-down memo cannot use `0` to mean "not computed yet". Use `-1`, which no answer can be, because coins are never negative.
- The starting value of the maximum. `best = 0` is safe here only because coins are never negative. With negative numbers it would hide a negative best result.

## Wrong approaches and why they fail

1. **Greedy: always burst the smallest balloon, or always the one with the best payoff right now.** A small balloon is not worth little, because it multiplies its neighbors, and bursting it early throws that away. Counterexample: `nums = [3, 1, 5, 8]`. Smallest first earns `15 + 15 + 40 + 8 = 78`, and the correct answer is `167`. For `[2, 3, 4]` smallest first earns `22`, and the correct answer is `36`. The best-payoff-now rule on `[5, 4, 3, 2, 1]` (ties go to the leftmost balloon) earns `60 + 30 + 10 + 2 + 1 = 103`, and the correct answer is `110`.
2. **Pick the balloon that bursts FIRST in an interval and add the two sides.** After the first burst, the two sides become neighbors, so each side's payoff depends on the other side. A table that ignores this charges every balloon the product of its original neighbors. Counterexample: `nums = [2, 3, 4]`. That method adds `1 * 2 * 3 + 2 * 3 * 4 + 3 * 4 * 1 = 6 + 24 + 12 = 42`, which no real order can reach, and the correct answer is `36`. On `[3, 1, 5, 8]` the same idea gives `98`, and the correct answer is `167`.
3. **Pad the row with 0 instead of 1.** Every burst that touches an end then earns nothing. Counterexample: `nums = [7]` gives `0`, and the correct answer is `7`. `[1, 5]` gives `0`, and the correct answer is `10`.
4. **Fill the table by the start index `i` upward instead of by the gap.** A cell then reads `dp[k][j]` from rows that are not filled yet, and they are still 0. Counterexample: `nums = [3, 1, 5, 8]` gives `63`, and the correct answer is `167`.

## Variants

1. **Recover the bursting order.** Store the best `k` for every cell. For an interval, the order is the order of the left part, then the order of the right part, then `k` itself, because `k` bursts last.
2. **Top-down version.** The same recurrence as a recursion over `(i, j)` with a memo table. It touches only the intervals it needs, and it needs a "not computed" marker other than 0.
3. **Minimum Cost to Cut a Stick (LC 1547).** The same table over the sorted cut positions, with the two ends of the stick as the padded ends. There, choosing the FIRST cut splits the stick into two independent pieces, which is the role the LAST balloon plays here.
4. **Negative numbers on the balloons.** Start each cell's maximum from `Integer.MIN_VALUE` instead of `0`. The terms added to it are ordinary ints, so the start value is never added to.
5. **A different value for the imaginary neighbors.** Change the two pad values `a[0]` and `a[n + 1]`. The rest of the table is unchanged.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `nums=[3,1,5,8]` | `167` | the first example; smallest-first greedy gives 78 |
| 2 | `nums=[1,5]` | `10` | the second example; padding with 0 gives 0 |
| 3 | `nums=[]` | `0` | outside the limits; an empty row, and the table code must not fail |
| 4 | `nums=[7]` | `7` | one balloon with two imaginary neighbors; padding with 0 gives 0 |
| 5 | `nums=[0,0,0]` | `0` | all zeros |
| 6 | `nums=[1,1,1,1]` | `4` | all ones, every burst earns 1 |
| 7 | `nums=[3,0,5]` | `20` | a zero in the middle; burst it first so that the neighbors meet |
| 8 | `nums=[2,3,4]` | `36` | three balloons; smallest-first greedy gives 22, and a first-burst split gives 42 |
| 9 | `nums=[1,2,3,4,5]` | `110` | a rising row |
| 10 | `nums=[5,4,3,2,1]` | `110` | the same row reversed gives the same answer; best-payoff-now greedy (ties to the left) gives 103 |
| 11 | `nums=[2,5,1,7,3]` | `179` | a mixed five-balloon row; smallest-first greedy gives 108 |
| 12 | `nums=[100,100,100]` | `1010100` | the largest values; bursting the middle first earns 1000000 |
| 13 | `nums=` 300 balloons, all `100` | `298010100` | the largest input the limits allow; the answer still fits in `int` |
