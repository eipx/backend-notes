# Coin Change
`ref: LC 322` · Difficulty: Medium · Pattern: 1-D DP over the amount; unlimited copies of each coin; minimum count

## Problem

You are given an array `coins` of distinct positive integers, where each integer is the value of one kind of coin, and an integer `amount`. You have an unlimited supply of every kind of coin. Return the fewest coins whose values add up to exactly `amount`. If no combination of coins adds up to `amount`, return `-1`.

Input: an integer array `coins` and an integer `amount`.
Output: a single integer, the fewest coins that make up `amount` exactly. It is `0` when `amount` is `0`, and `-1` when the amount cannot be made.

## Constraints

- `1 <= coins.length <= 12`
- `1 <= coins[i] <= 2^31 - 1`, and all values are different. The array is not necessarily sorted.
- `0 <= amount <= 10^4`
- A plain recursion that tries every coin at every step makes a number of calls that grows exponentially with `amount`, which is hopeless at `10000`. Only `amount + 1` different remaining amounts exist, so a table with one cell per amount needs about `12 * 10000 = 120000` steps.

## Worked examples

1. `coins = [1, 2, 5]`, `amount = 11` -> `3`. Take `5 + 5 + 1`. No two coins make 11, because the largest pair is `5 + 5 = 10`.
2. `coins = [2]`, `amount = 3` -> `-1`. Every sum made of 2s is even, so 3 can never be reached.
3. `coins = [1]`, `amount = 0` -> `0`. The amount is already made with no coins at all.
4. `coins = [1, 3, 4]`, `amount = 6` -> `2`. Take `3 + 3`. Grabbing the biggest coin first gives `4 + 1 + 1`, which is 3 coins and not the best.

## Edge cases checklist

- `amount == 0` (the answer is 0, not `-1`, and the table is a single cell).
- One coin that divides the amount (`[1]` with 2 is 2) and one that does not (`[2]` with 3 is `-1`).
- Every coin larger than the amount (`[5, 10]` with 3, and `[2147483647]` with 2). The answer is `-1`. Coin values reach `2^31 - 1`, so compare a coin with the amount before using it in an index or an addition.
- A parity trap: all coins even and the amount odd (`[2, 4]` with 7).
- A set where grabbing the largest coin first fails (`[1, 3, 4]` with 6 and `[1, 5, 6, 9]` with 11).
- Coins not in sorted order (`[2, 5, 10, 1]`). Do not rely on the order.
- A coin of value 1 is present, so every amount is reachable and the answer is at most `amount`.
- The upper bound `amount = 10000` (test 13 uses `[1, 2, 5]`, which needs 2000 coins of 5).

## Approach

### Brute force

Let `f(a)` be the fewest coins that make amount `a`. Then `f(0) = 0`. For `a > 0`, some coin `c <= a` is the last one added, and what remains is amount `a - c`, so `f(a) = 1 + min f(a - c)` over the coins that fit. If no coin fits, or every branch fails, `f(a)` is "impossible". With no memory, each call branches into up to `k` calls (`k` is the number of coin kinds) and the same remaining amounts come back again and again. With `[1, 2, 5]` and amount 11, the remaining amount 6 is reached after taking `5`, after taking `2 + 2 + 1`, after `2 + 1 + 2`, after `1 + 2 + 2`, and in many other orders, and each of those calls repeats all the work below it. The call count grows exponentially with `amount`. The recursion can also go `amount` levels deep when a coin of 1 exists. Only `amount + 1` different values of `a` exist, so storing each answer once removes the blow-up.

### Optimal

Let `dp[a]` be the fewest coins that add up to exactly `a`, for `a` from `0` to `amount`. The table has `amount + 1` cells.

- Base case: `dp[0] = 0`. Zero coins make amount 0.
- "Impossible" is stored as `amount + 1`. A reachable amount never needs more than `amount` coins (the worst case is all coins of value 1), so any stored value above `amount` can only mean "cannot be made". The value `amount + 1` can also be added to without overflow, which `Integer.MAX_VALUE` cannot.
- For each `a` from `1` to `amount`, try every coin `c` with `c <= a` as the last coin: `dp[a] = min(dp[a], dp[a - c] + 1)`. If `dp[a - c]` is "impossible", then `dp[a - c] + 1` is `amount + 2`, which is larger than the starting value `amount + 1`, so it never replaces it and the cell stays "impossible".

The answer is `dp[amount]`, or `-1` when that cell is still above `amount`. The table is filled from small amounts to large ones. Every coin is at least 1, so `dp[a - c]` has a smaller index than `dp[a]` and is already final.

Why the last-coin choice is enough: take an optimal set of coins for `a` and remove any one coin `c`. What is left must be an optimal set for `a - c`. If a smaller set existed for `a - c`, putting `c` back would beat the optimal set for `a`. So the best answer for `a` is built from the best answers of smaller amounts.

**Key invariant:** when the loop has finished amount `a`, `dp[a]` is the exact fewest number of coins for `a`, or a value above `amount` when `a` cannot be made. The base cell is right, and each later cell reads only cells with smaller indexes that are already final, so by induction the last cell is the answer for the whole amount.

### Step-by-step trace

Trace for `coins = [1, 3, 4]` and `amount = 6`. Each row is one amount. A column shows `1 + dp[a - c]` for that coin when the coin fits (`c <= a`), and `-` when it does not. The `dp[a]` column is the smallest of them.

| a | via coin 1 | via coin 3 | via coin 4 | dp[a] |
|---|---|---|---|---|
| 0 | - | - | - | 0 |
| 1 | 1 | - | - | 1 |
| 2 | 2 | - | - | 2 |
| 3 | 3 | 1 | - | 1 |
| 4 | 2 | 2 | 1 | 1 |
| 5 | 2 | 3 | 2 | 2 |
| 6 | 3 | 2 | 3 | 2 |

A few cells worked out. `dp[3]` tries coin 1 (`1 + dp[2] = 3`) and coin 3 (`1 + dp[0] = 1`), and the coin 3 wins. `dp[5]` tries coin 1 (`1 + dp[4] = 2`), coin 3 (`1 + dp[2] = 3`) and coin 4 (`1 + dp[1] = 2`), and the best is 2. `dp[6]` tries coin 1 (`1 + dp[5] = 3`), coin 3 (`1 + dp[3] = 2`) and coin 4 (`1 + dp[2] = 3`), and the best is 2, so the answer is 2.

Walking back from the corner shows the coins: `dp[6]` came from coin 3 and leaves amount 3. `dp[3]` came from coin 3 and leaves amount 0. The coins are `3 + 3`, which is worked example 4. The largest-coin-first idea would have taken 4 at amount 6, left amount 2, and needed two more coins.

## Java 8 solution
```java
public class CoinChange {

    // Fewest coins that add up to exactly amount, using as many copies of each
    // coin value as needed. Returns -1 when no combination of coins reaches
    // amount.
    //
    // dp[a] is the fewest coins that add up to exactly a, or amount + 1 when a
    // cannot be made. A reachable amount never needs more than amount coins
    // (the worst case is all coins of value 1), so amount + 1 is larger than
    // every real answer. It can stand for "unreachable" and still be safe to
    // add 1 to without overflowing.
    public static int solve(int[] coins, int amount) {
        int unreachable = amount + 1;
        int[] dp = new int[amount + 1];
        for (int a = 1; a <= amount; a++) {
            dp[a] = unreachable;
        }
        // Zero coins make amount 0. This is the only base case.
        dp[0] = 0;

        for (int a = 1; a <= amount; a++) {
            for (int coin : coins) {
                // Test coin <= a before subtracting, so a - coin is a valid
                // index. Coin values can be as large as Integer.MAX_VALUE.
                if (coin <= a) {
                    dp[a] = Math.min(dp[a], dp[a - coin] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    private static void check(int caseNum, int[] coins, int amount, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(coins, amount);
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

        check(1, new int[]{1, 2, 5}, 11, 3, fail, total);
        check(2, new int[]{2}, 3, -1, fail, total);
        check(3, new int[]{1}, 0, 0, fail, total);
        check(4, new int[]{1}, 1, 1, fail, total);
        check(5, new int[]{1}, 2, 2, fail, total);
        check(6, new int[]{1, 3, 4}, 6, 2, fail, total);
        check(7, new int[]{1, 5, 6, 9}, 11, 2, fail, total);
        check(8, new int[]{2, 5, 10, 1}, 27, 4, fail, total);
        check(9, new int[]{5, 10}, 3, -1, fail, total);
        check(10, new int[]{2, 4}, 7, -1, fail, total);
        check(11, new int[]{2147483647}, 2, -1, fail, total);
        check(12, new int[]{186, 419, 83, 408}, 6249, 20, fail, total);
        check(13, new int[]{1, 2, 5}, 10000, 2000, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(amount * k), where `k` is the number of coin kinds: each of the `amount` cells tries every coin once, with constant work per try. With the stated bounds that is at most `10000 * 12 = 120000` steps. Space O(amount) for one `int` array of `amount + 1` cells (at most `10001`).

## Java 8 pitfalls for this problem

- Using `Integer.MAX_VALUE` as the "impossible" value. Then `dp[a - coin] + 1` overflows to `Integer.MIN_VALUE`, the smallest `int`, and `Math.min` happily picks it. With `coins = [2]` and `amount = 3`, the cell for 3 reads the unreachable cell for 1 and becomes `-2147483648`, so the method returns that instead of `-1`. Use `amount + 1`, or test for the sentinel before adding.
- Leaving the table at its default. A new `int[]` is all zeros, and 0 reads as "zero coins", the best possible answer, so every `Math.min` keeps 0 and the method returns 0 for everything. Fill cells `1` to `amount` with the "impossible" value first, and set `dp[0] = 0` on purpose.
- Allocating `new int[amount]` instead of `new int[amount + 1]`. Cell `dp[amount]` must exist, and the failure is an `ArrayIndexOutOfBoundsException`. With `amount = 0` the array must still have one cell for `dp[0]`.
- Subtracting before comparing. `dp[a - coin]` with `coin > a` is a negative index and throws. Coin values go up to `2^31 - 1`, so a push-style update such as `dp[a + coin]` can overflow into a negative number. Check `coin <= a` in the pull style used here, or `coin <= amount - a` in a push style.
- Writing the final test as `dp[amount] >= amount`. The real answer can equal `amount` (for example `[1]` with 2 is 2, and `[1]` with 1 is 1), and `amount = 0` gives `dp[0] = 0`. Those valid answers would turn into `-1`. The test is `dp[amount] > amount`.
- Returning `dp[amount]` without mapping the "impossible" value to `-1`. With `[2]` and 3 the method returns 4, which is `amount + 1`.
- Breaking out of the coin loop at the first coin that is too big. That assumes sorted input. With `[5, 1, 2]` the loop would stop at 5 before trying 1 and 2. Skip such a coin with the `if`, as the solution does.
- A top-down recursion with a coin of 1 can go `amount = 10000` frames deep. That can raise `StackOverflowError` on a small stack. The bottom-up loops here use no recursion.
- A memo array left at its default 0 cannot tell "not computed yet" from "0 coins". Use a marker such as `-2`, or fill the array before the first call.
- `Math.min` takes exactly two arguments in Java 8.

## Wrong approaches and why they fail

1. **Greedy: always take the largest coin that still fits.** This works for some coin sets and fails for others, because a big coin early can leave a remainder that needs many small coins, or no coin at all. Counterexample: `coins = [1, 3, 4]`, `amount = 6`. Greedy takes `4`, then `1 + 1`, which is 3 coins. The correct answer is `2` (`3 + 3`). For `coins = [1, 5, 6, 9]` and `amount = 11` greedy takes `9 + 1 + 1` (3 coins) and the correct answer is `2` (`5 + 6`). For `coins = [4, 5]` and `amount = 8` greedy takes `5`, is left with 3, and gives up with `-1`, but `4 + 4` works, so the correct answer is `2`.
2. **Mark impossible cells with `Integer.MAX_VALUE` and add 1 without a guard.** The addition wraps to the most negative `int`. Counterexample: `coins = [2]`, `amount = 3`. The cell for 1 is impossible, the cell for 3 becomes `-2147483648`, and the method returns that value. The correct answer is `-1`.
3. **Return `dp[amount]` directly, without the `-1` step.** An impossible cell then leaks out as `amount + 1`. Counterexample: `coins = [2]`, `amount = 3` returns `4`, and `coins = [2, 4]`, `amount = 7` returns `8`. The correct answer for both is `-1`.
4. **Treat each coin as usable once (a 0/1 table with a downward inner loop).** The problem allows unlimited copies. Counterexample: `coins = [1, 2, 5]`, `amount = 11`. Using each coin at most once reaches at most `1 + 2 + 5 = 8`, so this approach says `-1`. The correct answer is `3`.
5. **Start with an all-zero table and only set `dp[0] = 0`.** Every `Math.min` then keeps the 0 it started with. Counterexample: `coins = [1, 2, 5]`, `amount = 11` returns `0`. The correct answer is `3`.

## Variants

1. **Recover the coins, not just the count.** Keep a second array `last[a]` that stores the coin that gave the best value for `a`. Start at `amount` and repeat: output `last[a]`, then move to `a - last[a]`, until `a` is 0.
2. **Count the combinations instead of the minimum (LC 518).** The same coin-and-amount table, with a sum in place of a minimum and the loops in a different order. See `coin-change-ii.md`.
3. **Perfect Squares (LC 279).** The coins are every square number up to `n` (1, 4, 9, 16, and so on), and the question is the fewest squares that add up to `n`. It is the same table.
4. **Top-down with memory.** Write `f(a) = 1 + min f(a - c)` as a recursion and store each answer. The work is the same, but mind the recursion depth and the default-0 memo trap above.
5. **Shortest path.** Treat each amount from `0` to `amount` as a node, with an edge from `a` to `a + c` for every coin. A breadth-first search from node 0 reaches `amount` after the fewest edges, and that number is the answer. It does the same amount of work and can stop early.
6. **Each coin at most once.** Put the coins on the outside and run the inner loop over the amount downward, as in `partition-equal-subset-sum.md`.
7. **A limited supply of each coin.** Each kind comes with a count. Treat every copy as its own coin (fine for small counts), or track how many copies of the current coin a state has used.
8. **When greedy is safe.** For some coin systems, such as `1, 5, 10, 25`, taking the largest coin first is provably optimal. The table above is the answer that needs no such proof, and it is the right choice for an arbitrary set.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `coins=[1,2,5], amount=11` | `3` | the standard example, `5 + 5 + 1` |
| 2 | `coins=[2], amount=3` | `-1` | unreachable; returning the sentinel gives 4, and an `Integer.MAX_VALUE` sentinel overflows to -2147483648 |
| 3 | `coins=[1], amount=0` | `0` | amount 0 needs no coins and is not `-1`; a final test of `>=` gets this wrong |
| 4 | `coins=[1], amount=1` | `1` | the smallest positive amount; a final test of `>=` gets this wrong too |
| 5 | `coins=[1], amount=2` | `2` | one coin used twice; a 0/1 table returns `-1` |
| 6 | `coins=[1,3,4], amount=6` | `2` | `3 + 3`; greedy gives 3 coins |
| 7 | `coins=[1,5,6,9], amount=11` | `2` | `5 + 6`; greedy gives 3 coins |
| 8 | `coins=[2,5,10,1], amount=27` | `4` | unsorted coins, `10 + 10 + 5 + 2` |
| 9 | `coins=[5,10], amount=3` | `-1` | every coin is larger than the amount |
| 10 | `coins=[2,4], amount=7` | `-1` | only even sums can be made |
| 11 | `coins=[2147483647], amount=2` | `-1` | a coin at the largest `int`; an index or sum built from it overflows or goes negative |
| 12 | `coins=[186,419,83,408], amount=6249` | `20` | a larger case with big values, where only the table finds the answer |
| 13 | `coins=[1,2,5], amount=10000` | `2000` | the upper bound on the amount, 2000 coins of 5 |
