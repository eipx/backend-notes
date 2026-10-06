# Coin Change II
`ref: LC 518` · Difficulty: Medium · Pattern: unbounded knapsack counting combinations; coins outer loop so each combination is counted once

## Problem

You are given an integer `amount` and an array `coins` of distinct positive integers, where each integer is the value of one kind of coin. You have an unlimited supply of every kind of coin. Return the number of different combinations of coins whose values add up to exactly `amount`. Two combinations are the same when they use every coin value the same number of times, so the order of the coins does not matter. If no combination adds up to `amount`, return `0`.

Input: an integer `amount` and an integer array `coins`, in that order.
Output: a single integer, the number of combinations. An `amount` of `0` has exactly one combination, the empty one. The answer is guaranteed to fit in a 32-bit signed integer.

## Constraints

- `1 <= coins.length <= 300`
- `1 <= coins[i] <= 5000`, and all values are different. The array is not necessarily sorted.
- `0 <= amount <= 5000`
- The answer fits in a signed 32-bit integer.
- A plain recursion that decides coin by coin makes at least as many calls as the answer, because every counted combination is the end of one call chain. Test 12 below has an answer of about 35 million. Only `coins.length * (amount + 1)` different states exist (at most `300 * 5001`, about `1.5 million`), so a table is fast.

## Worked examples

1. `amount = 5`, `coins = [1, 2, 5]` -> `4`. The four combinations are `5`, `2 + 2 + 1`, `2 + 1 + 1 + 1` and `1 + 1 + 1 + 1 + 1`.
2. `amount = 3`, `coins = [2]` -> `0`. Sums of 2s are even, so 3 cannot be made.
3. `amount = 10`, `coins = [10]` -> `1`. One coin of 10 is the only combination.
4. `amount = 3`, `coins = [1, 2]` -> `2`. The combinations are `1 + 1 + 1` and `1 + 2`. The sequence `2 + 1` is the same combination as `1 + 2`, so it is not counted again.
5. `amount = 0`, `coins = [7]` -> `1`. Using no coins at all is one valid combination.

## Edge cases checklist

- `amount == 0` (the answer is 1, the empty combination, whatever the coins are).
- No combination exists (`[2]` with 3, `[2, 4]` with 7). The answer is 0.
- Every coin larger than the amount (`[2, 3, 5]` with 1). The answer is 0 and the loops never run.
- A coin exactly equal to the amount (`[10]` with 10). The answer is 1.
- Order must not matter: `1 + 2` and `2 + 1` are one combination (`[1, 2]` with 3 is 2, not 3).
- A coin can be used many times (`[1, 2, 5]` with 10 has combinations with up to ten coins).
- Coins not sorted, with a coin larger than the amount in front (`[5, 1, 2]` with 3). Do not assume a sorted order.
- Large answers (tests 11 and 12 reach 73682 and 35502874). They must not be cut off by a too-small type, and with the stated guarantee they fit in an `int`.
- A single coin that divides the amount (`[1]` with 5000 is 1) or does not.
- The upper bound: 300 coins and amount 5000, about 1.5 million table steps (not among the tests here).

## Approach

### Brute force

Decide the coins one at a time, in the order they appear. Let `ways(i, r)` be the number of combinations that make remaining amount `r` using only coins `i` and later. When `r == 0` there is one way (use nothing more). When there are no coins left and `r > 0`, there are none. Otherwise there are two choices for coin `i`: leave it out and go on to coin `i + 1`, which is `ways(i + 1, r)`, or take at least one more copy and stay on coin `i`, which is `ways(i, r - coin[i])`. The two cases are different and cover everything, so `ways(i, r)` is their sum.

Because the search moves on to coin `i + 1` for good and never goes back, each combination is produced in only one way (its copies of the first coin, then of the second, and so on). That is why the order of coins does not matter in the count. Without memory, every call chain ends at a counted combination or a dead end, so the number of calls is at least the answer, and many calls repeat the same `(i, r)`. A table over those pairs removes the blow-up.

### Optimal

Let `dp[s]` be the number of combinations that make sum `s` using only the coin values processed so far. The table has `amount + 1` cells, and the base is `dp[0] = 1` (the empty combination) with every other cell `0` before any coin is processed.

Handle the coins one at a time, as the outer loop. For the current coin of value `c`, run `s` from `c` up to `amount` and do

`dp[s] += dp[s - c]`

This is the one-dimensional form of a two-dimensional table, `dp[i][s] = dp[i - 1][s] + dp[i][s - c]`. The old value of `dp[s]` is the number of combinations that do not use the new coin at all. The term `dp[s - c]` is the number of combinations that use at least one copy of the new coin: remove one copy and what is left is a combination for `s - c` from the coins up to and including this one.

Two choices make this right, and each can be told apart from the wrong version by a small example.

- **Coins on the outside.** With coins as the outer loop, every combination is built in a fixed coin order, so it is counted once. If the amount is the outer loop and the coins the inner one, a combination is counted once for every order of its coins, which is a different problem (the number of ordered sequences). Take `amount = 3`, `coins = [1, 2]`. The amount-first loops give `dp[1] = 1`, `dp[2] = dp[1] + dp[0] = 2`, `dp[3] = dp[2] + dp[1] = 3`, which counts `1 + 1 + 1`, `1 + 2` and `2 + 1`. The correct answer is 2.
- **Upward inner loop.** When the loop reaches `s`, the cell `dp[s - c]` has already been updated for this coin, so it includes combinations that already use the coin. That is what allows any number of copies. A downward loop would leave `dp[s - c]` at its old value and allow each coin only once, the way a 0/1 subset-sum table does.

The answer is `dp[amount]`. A coin larger than `amount` never enters the inner loop, so it changes nothing.

**Key invariant:** after the first `k` coin values have been processed, `dp[s]` is exactly the number of combinations of those `k` values that sum to `s`, for every `s` from `0` to `amount`. When the next coin `c` is processed, a combination of the first `k + 1` values either has no copy of `c` (counted by the old `dp[s]`) or has at least one, and removing one copy gives a combination for `s - c` using the first `k + 1` values (counted by the already updated `dp[s - c]`). Those two groups do not overlap and together make all combinations, so the new `dp[s]` is exact. Cell `dp[0]` stays 1 throughout, because the inner loop never touches it.

### Step-by-step trace

Trace for `amount = 5` and `coins = [1, 2, 5]`. Each row is the whole table after one coin has been processed.

| after processing | s=0 | s=1 | s=2 | s=3 | s=4 | s=5 |
|---|---|---|---|---|---|---|
| start (only the empty combination) | 1 | 0 | 0 | 0 | 0 | 0 |
| coin 1 | 1 | 1 | 1 | 1 | 1 | 1 |
| coin 2 | 1 | 1 | 2 | 2 | 3 | 3 |
| coin 5 | 1 | 1 | 2 | 2 | 3 | 4 |

A few cells worked out. After coin 1, every sum has exactly one combination, all ones. While processing coin 2, `dp[2] = 1 + dp[0] = 2` (`1 + 1` and `2`). Then `dp[3] = 1 + dp[1] = 2` (`1 + 1 + 1` and `1 + 2`). Then `dp[4] = 1 + dp[2] = 1 + 2 = 3`, where `dp[2]` is already the new value, and the three combinations are `1 + 1 + 1 + 1`, `1 + 1 + 2` and `2 + 2`. The combination `2 + 2` is exactly the one that reuses the coin, which is why the loop runs upward. Then `dp[5] = 1 + dp[3] = 1 + 2 = 3`. Finally coin 5 changes only `dp[5]`: `dp[5] = 3 + dp[0] = 4`. The answer is `dp[5] = 4`, which matches worked example 1.

## Java 8 solution
```java
public class CoinChangeII {

    // Number of different combinations of coins that add up to exactly amount,
    // with an unlimited supply of every coin value. Two combinations are the
    // same when they use each coin value the same number of times, so 1 + 2
    // and 2 + 1 count once. The problem guarantees that the answer fits in an
    // int.
    //
    // dp[s] is the number of combinations that make sum s using only the coin
    // values processed so far. The empty combination makes sum 0, so dp[0] is 1.
    public static int solve(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;

        // Coins form the outer loop. Each combination is then built in a fixed
        // coin order (some copies of the first coin, then some of the second,
        // and so on), so it is counted exactly once.
        for (int coin : coins) {
            // Walk the sums from low to high. dp[s - coin] has already been
            // updated for this coin, so it includes combinations that use the
            // coin again. That is what allows unlimited copies.
            for (int s = coin; s <= amount; s++) {
                dp[s] += dp[s - coin];
            }
        }
        return dp[amount];
    }

    private static void check(int caseNum, int amount, int[] coins, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(amount, coins);
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

        check(1, 5, new int[]{1, 2, 5}, 4, fail, total);
        check(2, 3, new int[]{2}, 0, fail, total);
        check(3, 10, new int[]{10}, 1, fail, total);
        check(4, 0, new int[]{7}, 1, fail, total);
        check(5, 3, new int[]{1, 2}, 2, fail, total);
        check(6, 4, new int[]{1, 2, 3}, 4, fail, total);
        check(7, 7, new int[]{2, 4}, 0, fail, total);
        check(8, 1, new int[]{2, 3, 5}, 0, fail, total);
        check(9, 10, new int[]{1, 2, 5}, 10, fail, total);
        check(10, 3, new int[]{5, 1, 2}, 2, fail, total);
        check(11, 200, new int[]{1, 2, 5, 10, 20, 50, 100, 200}, 73682, fail, total);
        check(12, 500, new int[]{3, 5, 7, 8, 9, 10, 11}, 35502874, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n * amount), where `n` is the number of coin kinds: each coin triggers one pass over at most `amount` cells, with constant work per cell. With the stated bounds that is at most `300 * 5000 = 1.5 million` steps. Space O(amount) for one `int` array of `amount + 1` cells (at most `5001`).

## Java 8 pitfalls for this problem

- Putting the amount loop outside and the coins inside. The code compiles and passes some inputs, but it counts ordered sequences, not combinations (`[1, 2]` with 3 gives 3, and `[1, 2, 5]` with 5 gives 9 instead of 4). The coin loop must be the outer one.
- Forgetting `dp[0] = 1`. A new `int[]` is all zeros, so every addition adds zero and every answer is 0, including the correct answer 1 for `amount = 0`.
- Allocating `new int[amount]` instead of `new int[amount + 1]`. Cell `dp[amount]` must exist, and the failure is an `ArrayIndexOutOfBoundsException`. With `amount = 0` the array still needs one cell for `dp[0]`.
- Writing the inner loop bound as `s < amount`. That skips the last cell, so `[10]` with 10 returns 0 instead of 1. Use `s <= amount`.
- Starting the inner loop below the coin value, such as `s = 0`. Then `s - coin` is negative and the array access throws. Start at `s = coin`, and a coin larger than `amount` then never runs the loop.
- Running the inner loop downward. It compiles, but each coin can then be used only once, as in a 0/1 subset-sum table. `[1, 2, 5]` with 5 gives 1 and not 4.
- Integer overflow. The statement guarantees only that the final answer fits in an `int`. A cell for a smaller amount can be larger, and `int` addition wraps around silently with no exception. This table only adds, so a wrapped cell cannot damage the final result (the result is right modulo `2^32`, and the true answer fits in that range). It would damage any code that compares, divides or takes a minimum of the cells. If a version of the problem asks for the count modulo some `m`, reduce after every addition, with `m` small enough that the sum of two reduced values still fits in an `int`.
- Breaking out of the coin loop at the first coin larger than `amount`. That assumes sorted input. With `[5, 1, 2]` and 3, the loop would stop at 5 and never reach 1 and 2, giving 0 and not 2. A coin that is too big already does nothing, because its inner loop is empty.
- A top-down recursion on `(coin index, remaining amount)` can go `amount / smallest coin` frames deep, up to 5000 when a coin of 1 exists, which can raise `StackOverflowError` on a small stack. The loops here use no recursion.
- A memo array left at its default 0 cannot tell "not computed yet" from "no combination". Use a marker such as `-1`.

## Wrong approaches and why they fail

1. **Loop over the amount on the outside and the coins on the inside.** This counts every ordering of a combination separately. Counterexample: `amount = 3`, `coins = [1, 2]`. It counts `1 + 1 + 1`, `1 + 2` and `2 + 1` and returns `3`. The correct answer is `2`. With `amount = 5` and `coins = [1, 2, 5]` it returns `9`, and the correct answer is `4`.
2. **Run the inner loop downward.** Each coin value can then be used at most once. Counterexample: `amount = 5`, `coins = [1, 2, 5]`. The only subset of `{1, 2, 5}` that sums to 5 is `{5}`, so it returns `1`. The correct answer is `4`. For `amount = 10` with the same coins it returns `0`, and the correct answer is `10`.
3. **Leave the base cell at 0.** Every cell is then a sum of zeros. Counterexample: `amount = 0`, `coins = [7]` returns `0`, and `amount = 5`, `coins = [1, 2, 5]` returns `0`. The correct answers are `1` and `4`.
4. **Stop at the first coin larger than the amount, assuming the coins are sorted.** Counterexample: `amount = 3`, `coins = [5, 1, 2]`. The loop stops at 5 and returns `0`. The correct answer is `2` (`1 + 1 + 1` and `1 + 2`).

## Variants

1. **Two-dimensional table.** `dp[i][s]` is the number of combinations of the first `i` coins that sum to `s`, with `dp[i][s] = dp[i - 1][s] + dp[i][s - coin]`. The one-dimensional array used here is this table rolled into one row, and the upward loop is what keeps the `dp[i][s - coin]` term legal. Keep the 2-D table if you want to see how the count changes as each coin is added.
2. **Count ordered sequences instead (LC 377, Combination Sum IV).** Swap the loops: the amount outside and the coins inside. The wrong approach above is the right answer to that problem.
3. **Minimum number of coins (LC 322).** The same table with a minimum in place of a sum. See `coin-change.md`.
4. **Each coin at most once.** Run the inner loop downward. Counting the subsets that reach a sum is the core of `target-sum.md`, and the yes-or-no form is `partition-equal-subset-sum.md`.
5. **Count modulo a number.** Replace the addition with `dp[s] = (dp[s] + dp[s - coin]) % m`, so the values stay small and the table never overflows.
6. **List the combinations, not the count.** A backtracking search that picks coins in non-decreasing index order. The output can be as large as the count, so it is only practical for small cases.
7. **A limited supply of each coin.** Each kind comes with a count. Process the copies of one coin together: the new `dp[s]` is the sum of the old cells `s`, `s - coin`, `s - 2 * coin`, and so on, down to `count` copies back. A running sum along that stride keeps it fast. For small counts, treat each copy as a separate 0/1 item instead.
8. **Exactly `k` coins.** Add a second dimension for the number of coins used, `dp[j][s]`, and update it from `dp[j - 1][s - coin]`.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `amount=5, coins=[1,2,5]` | `4` | the standard example; swapped loops give 9, a downward loop gives 1 |
| 2 | `amount=3, coins=[2]` | `0` | no combination exists |
| 3 | `amount=10, coins=[10]` | `1` | a coin equal to the amount (fails with `s < amount`) |
| 4 | `amount=0, coins=[7]` | `1` | the empty combination (fails without `dp[0] = 1`) |
| 5 | `amount=3, coins=[1,2]` | `2` | `1 + 2` and `2 + 1` count once; amount-first loops give 3 |
| 6 | `amount=4, coins=[1,2,3]` | `4` | `1+1+1+1`, `1+1+2`, `2+2`, `1+3` with three coin kinds |
| 7 | `amount=7, coins=[2,4]` | `0` | parity: only even sums can be made |
| 8 | `amount=1, coins=[2,3,5]` | `0` | every coin larger than the amount, so no inner loop runs |
| 9 | `amount=10, coins=[1,2,5]` | `10` | many copies of one coin; a downward loop gives 0 |
| 10 | `amount=3, coins=[5,1,2]` | `2` | unsorted input with a too-large coin first; stopping early gives 0 |
| 11 | `amount=200, coins=[1,2,5,10,20,50,100,200]` | `73682` | eight coin kinds, a count in the tens of thousands |
| 12 | `amount=500, coins=[3,5,7,8,9,10,11]` | `35502874` | a count in the tens of millions that still fits in an `int` |
