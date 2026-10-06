# Best Time to Buy and Sell Stock with Transaction Fee
`ref: LC 714` · Difficulty: Medium · Pattern: state machine over days with two states (hold and cash), the fee paid once per trade, two rolling variables, O(1) space

## Problem

You are given an array `prices`, where `prices[i]` is the price of one share of a stock on day `i`, and a non-negative integer `fee`. You may buy and sell as many times as you like, but you can hold at most one share at a time, so a share has to be sold before the next one is bought. Every completed trade (one buy followed by its sell) costs `fee` once. Return the largest total profit you can make.

Input: an integer array `prices` and an integer `fee`.
Output: a single integer, the maximum profit after paying the fee for every trade. It is never negative, because making no trade at all earns 0.

## Constraints

- `1 <= prices.length <= 50000`
- `1 <= prices[i] < 50000`
- `0 <= fee < 50000`
- On each day there are two choices (act or wait), so a plain recursion over every sequence of choices makes `2^n` paths, which is hopeless at `n = 50000`. Only `2n` different situations exist (a day, and whether a share is held), so a single pass over the days with two running numbers is enough.
- The answer fits in an `int`. The largest possible profit comes from prices that alternate between `1` and `49999` with `fee = 0`, which is `25000 * 49998 = 1249950000`, below the `int` limit of `2147483647`.

## Worked examples

1. `prices = [1, 3, 2, 8, 4, 9]`, `fee = 2` -> `8`. Buy at 1 (day 0) and sell at 8 (day 3), which earns `8 - 1 - 2 = 5`. Buy at 4 (day 4) and sell at 9 (day 5), which earns `9 - 4 - 2 = 3`. The total is `5 + 3 = 8`.
2. `prices = [1, 3, 7, 5, 10, 3]`, `fee = 3` -> `6`. Buy at 1 and sell at 10, which earns `10 - 1 - 3 = 6`. Selling at 7 and buying again at 5 earns only `(7 - 1 - 3) + (10 - 5 - 3) = 5`, because the second fee costs more than the dip saves.
3. `prices = [5]`, `fee = 3` -> `0`. With a single day no trade is possible.
4. `prices = [1, 5, 4, 6]`, `fee = 2` -> `3`. Hold through the dip from 5 to 4: buy at 1 and sell at 6 earns `6 - 1 - 2 = 3`. Selling at 5 and buying again at 4 earns `(5 - 1 - 2) + (6 - 4 - 2) = 2`.

## Edge cases checklist

- A single day (the answer is 0, and the loop body never runs).
- Prices that fall every day (the answer is 0, never buy).
- All prices equal (the answer is 0).
- `fee = 0` (the problem becomes the unlimited-trades problem, and the answer is the sum of every day-to-day rise).
- A fee larger than the best possible gain (the answer is 0, because every trade loses money).
- A trade whose gain equals the fee exactly (`[1, 3]` with `fee = 2`): the profit is 0, so skipping the trade and taking it give the same answer.
- Prices that rise in many small steps, each smaller than the fee (`[1, 2, 3, 4, 5]` with `fee = 2`): one long trade is worth it even though no single day beats the fee.
- A small dip inside a climb (`[1, 5, 4, 6]` with `fee = 2`): holding through it wins, because selling and buying again would pay a second fee.
- A deep dip between two climbs (`[1, 5, 1, 5]` with `fee = 2`): two separate trades beat one long hold.
- The upper bound, `50000` days with prices swinging between `1` and `49999`: the largest profit is about 1.25 billion, still inside `int` (not among the tests here).

## Approach

### Brute force

Walk the days from left to right and remember whether a share is held. Let `f(i, holding)` be the best profit that can still be made from day `i` onward. If no share is held, there are two choices: buy today (`-prices[i] + f(i + 1, true)`) or wait (`f(i + 1, false)`). If a share is held, there are two choices: sell today (`prices[i] - fee + f(i + 1, false)`) or wait (`f(i + 1, true)`). When `i == n` the answer is 0, because a share that is still held at the end is worth nothing. Every day doubles the number of paths, so this makes `2^n` calls. Only `2n` different pairs `(i, holding)` exist, and the same pairs are reached over and over, so storing the answers in a table removes the blow-up.

### Optimal

Think of each day as one step of a small machine that is always in one of two states: holding no share (`cash`) or holding one share (`hold`). Two numbers are kept after each day:

- `cash`: the best profit so far, if no share is held at the end of the day.
- `hold`: the best profit so far, if one share is held at the end of the day. The price paid for that share is already subtracted, so `hold` is usually negative.

Base case (day 0): `cash = 0`, because nothing has happened, and `hold = -prices[0]`, because the only way to hold a share on day 0 is to buy it then.

On each later day `i`, each state can be reached in two ways, and the better one is kept:

- `cash` after day `i`: stay in cash (`cash`), or sell the held share today (`hold + prices[i] - fee`).
- `hold` after day `i`: keep holding (`hold`), or buy today out of cash (`cash - prices[i]`).

The fee is charged on the sell, so a trade pays it exactly once. Charging it on the buy instead gives the same answer, because the answer is read from `cash`, and every trade that counts has been sold. Both new values read only yesterday's two numbers, so compute them first and assign afterwards. The days are filled left to right in one pass, and the answer is `cash` after the last day. Ending with a share in hand is never better: it paid for a share and never got the money back, so selling it or never buying it is at least as good.

Selling and buying again on the same day is not a move of the machine. It would turn `hold` into `hold - fee`, which is worse than simply keeping the share, so leaving it out loses nothing.

**Key invariant:** after day `i`, `cash` is the best profit over days `0` to `i` that ends with no share, and `hold` is the best profit over days `0` to `i` that ends with one share. Every way of reaching a state on day `i` comes from one of the two states on day `i - 1` plus a single move, and the update takes the best of exactly those, so by induction on the day the last `cash` is the best total profit.

### Step-by-step trace

Rolling values for `prices = [1, 3, 2, 8, 4, 9]`, `fee = 2`. The columns `sell` and `buy` are the two candidates computed from the previous day's `cash` and `hold`.

| day | price | sell = hold + price - fee | buy = cash - price | cash after | hold after |
|---|---|---|---|---|---|
| 0 | 1 | | | 0 | -1 |
| 1 | 3 | 0 | -3 | 0 | -1 |
| 2 | 2 | -1 | -2 | 0 | -1 |
| 3 | 8 | 5 | -8 | 5 | -1 |
| 4 | 4 | 1 | 1 | 5 | 1 |
| 5 | 9 | 8 | -4 | 8 | 1 |

Day 0 starts with `hold = -1`, the share bought at 1. On days 1 and 2 selling would give 0 and -1, which does not beat staying in cash at 0, so `cash` stays 0. On day 3 selling at 8 gives `-1 + 8 - 2 = 5`, so `cash` becomes 5. On day 4 buying at 4 out of that cash leaves `5 - 4 = 1`, which beats the old `hold` of -1, so the machine switches to the second share. On day 5 selling at 9 gives `1 + 9 - 2 = 8`. The answer is `8`.

Walking back from the end: the final `cash = 8` came from selling on day 5 the share whose `hold = 1` was set on day 4 by buying out of `cash = 5`; that `cash = 5` came from selling on day 3 the share whose `hold = -1` was set on day 0. Those are the two trades of worked example 1.

## Java 8 solution
```java
public class BestTimeToBuyAndSellStockWithTransactionFee {

    // Largest profit from buying and selling one share at a time over the
    // given days, when every completed trade costs a fixed fee.
    //
    // Two states are tracked after each day:
    //   cash = best profit so far while holding no share
    //   hold = best profit so far while holding one share (the price paid for
    //          it is already subtracted)
    // The fee is charged at the moment of selling, so a trade pays it exactly
    // once. The answer is read from cash, because a share still held at the
    // end is worth nothing.
    public static int solve(int[] prices, int fee) {
        // Day 0: no profit yet, or the share bought on day 0.
        int cash = 0;
        int hold = -prices[0];

        for (int i = 1; i < prices.length; i++) {
            // Both new values read only yesterday's cash and hold, so compute
            // them first and assign afterwards.
            int newCash = Math.max(cash, hold + prices[i] - fee);
            int newHold = Math.max(hold, cash - prices[i]);
            cash = newCash;
            hold = newHold;
        }
        return cash;
    }

    private static void check(int caseNum, int[] prices, int fee, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(prices, fee);
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

        check(1, new int[]{1, 3, 2, 8, 4, 9}, 2, 8, fail, total);
        check(2, new int[]{1, 3, 7, 5, 10, 3}, 3, 6, fail, total);
        check(3, new int[]{5}, 3, 0, fail, total);
        check(4, new int[]{1, 2, 3, 4, 5}, 0, 4, fail, total);
        check(5, new int[]{5, 4, 3, 2, 1}, 1, 0, fail, total);
        check(6, new int[]{1, 3}, 2, 0, fail, total);
        check(7, new int[]{1, 4}, 2, 1, fail, total);
        check(8, new int[]{3, 3, 3, 3}, 1, 0, fail, total);
        check(9, new int[]{1, 3, 2, 8, 4, 9}, 0, 13, fail, total);
        check(10, new int[]{1, 3, 2, 8, 4, 9}, 10, 0, fail, total);
        check(11, new int[]{1, 5, 4, 6}, 2, 3, fail, total);
        check(12, new int[]{1, 5, 1, 5}, 2, 4, fail, total);
        check(13, new int[]{2, 1, 4, 4, 2, 3, 2, 5, 1, 2}, 1, 4, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n): one pass over the days with constant work per day. Space O(1): two integers. A table with one row per day and two columns would use O(n) space, and it is only needed to recover the actual buy and sell days (see Variants).

## Java 8 pitfalls for this problem

- Starting `hold` at 0 instead of `-prices[0]`. A freshly allocated `int[n][2]` table is all zeros too, so its `hold` column means "a share held for free". The sell move then cashes in a share that was never bought, and a falling market such as `[5, 4, 3, 2, 1]` with `fee = 1` reports a profit of 3 (or 4 if the loop also runs day 0) instead of 0. Set the day 0 values explicitly.
- Using `Integer.MIN_VALUE` as negative infinity for `hold`. The sell candidate is `hold + prices[i] - fee`, and `Integer.MIN_VALUE + 1 - 2` wraps around to `2147483647`, which wins every comparison. Starting from `-prices[0]` avoids the problem, and no placeholder is needed.
- Charging the fee on both the buy and the sell. The fee belongs to a trade, and a trade has one buy and one sell, so the fee appears in exactly one of the two formulas.
- Updating `cash` and `hold` in place, one after the other. In this problem the order happens not to change the answer (buying out of today's new `cash` means selling and buying on the same day, which never beats holding), but the habit breaks the cooldown variant, where the old value is the one that must be read. Compute both new values into temporaries first.
- Writing `Math.max(a, b, c)`. `Math.max` takes exactly two arguments in Java 8, and every update here is a two-way choice.
- A recursive solution with `n` up to `50000` can overflow the default stack, since each day adds a frame. The loop has no recursion and no such risk.
- Allocating a table as `new int[n + 1][2]` out of habit from prefix tables. Rows here stand for days `0` to `n - 1`, so `n` rows are enough, and an extra row of zeros would be read as a real day.
- Intermediate sums such as `cash - prices[i]` stay between about `-50000` and `1250000000`, which is safe in `int`. If the limits were raised, switch the two running values to `long`.

## Wrong approaches and why they fail

1. **Add up every day-to-day rise that is larger than the fee, minus the fee.** This looks at one day at a time, so a climb made of small steps never beats the fee, and a trade that continues through a small dip is cut in two. Counterexample: `prices = [1, 2, 3, 4, 5]`, `fee = 2`. Every daily rise is 1, never larger than 2, so the method returns `0`. The correct answer is `2` (buy at 1, sell at 5, `5 - 1 - 2`). For `[1, 5, 4, 6]` with `fee = 2` it returns `2`, and the correct answer is `3`.
2. **Compute the no-fee answer (the sum of all rises) and subtract one fee.** The fee is paid once per trade, not once overall. Counterexample: `prices = [1, 5, 1, 5]`, `fee = 2`. The rises add up to 8, and subtracting one fee gives `6`. The correct answer is `4`, two trades earning `2` each. For `[1, 3, 2, 8, 4, 9]` with `fee = 2` it gives `11`, and the correct answer is `8`.
3. **Charge the fee when buying and again when selling.** That doubles the fee. Counterexample: `prices = [1, 5, 1, 5]`, `fee = 2`. Each trade would earn `5 - 1 - 4 = 0`, so the method returns `0`. The correct answer is `4`. For `[1, 3, 2, 8, 4, 9]` with `fee = 2` it gives `4`, and the correct answer is `8`.
4. **Start `hold` at 0.** The machine then starts out holding a share it never paid for. Counterexample: `prices = [5, 4, 3, 2, 1]`, `fee = 1`. Running the loop from day 0 with `hold = 0` sells that free share at 5, and the method returns `4`. The correct answer is `0`.

## Variants

1. **Table version.** Keep a table with one row per day and two columns (`cash`, `hold`). It uses O(n) space and is needed only if the buy and sell days must be recovered.
2. **Recover the trades.** Walk back from the last `cash`. At each day, check whether the value came from staying in the same state or from the move out of the other state, and record the sell and buy days along the way.
3. **Fee charged on the buy.** Start `hold` at `-prices[0] - fee`, replace the buy move by `cash - prices[i] - fee`, and replace the sell move by `hold + prices[i]`. The answer does not change, because `cash` only counts completed trades.
4. **No fee (LC 122).** With `fee = 0` the answer is the sum of every day-to-day rise. See test case 9.
5. **At most one trade (LC 121).** Drop the `cash - prices[i]` dependence on earlier profit: the buy move becomes `-prices[i]`.
6. **At most `k` trades (LC 188).** Add a transaction count to the state. See `best-time-to-buy-and-sell-stock-iv.md`.
7. **With a one-day cooldown after each sell (LC 309).** Add a third state for the day right after a sell, and buy only out of cash that was already settled the day before.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `prices=[1,3,2,8,4,9], fee=2` | `8` | LeetCode example 1; two separate trades |
| 2 | `prices=[1,3,7,5,10,3], fee=3` | `6` | LeetCode example 2; hold through the dip from 7 to 5 (selling and buying again gives 5) |
| 3 | `prices=[5], fee=3` | `0` | a single day, no trade possible |
| 4 | `prices=[1,2,3,4,5], fee=0` | `4` | no fee, steadily rising: one trade from the first day to the last |
| 5 | `prices=[5,4,3,2,1], fee=1` | `0` | falling prices, never trade (a `hold` that starts at 0 gives 3, or 4 if the loop also runs day 0) |
| 6 | `prices=[1,3], fee=2` | `0` | the gain equals the fee exactly, so the profit is 0 |
| 7 | `prices=[1,4], fee=2` | `1` | one trade whose gain beats the fee by 1 |
| 8 | `prices=[3,3,3,3], fee=1` | `0` | flat prices |
| 9 | `prices=[1,3,2,8,4,9], fee=0` | `13` | zero fee turns the problem into the sum of all rises (2 + 6 + 5) |
| 10 | `prices=[1,3,2,8,4,9], fee=10` | `0` | a fee larger than any possible gain, so never trade |
| 11 | `prices=[1,5,4,6], fee=2` | `3` | hold through a small dip (selling and buying again gives 2) |
| 12 | `prices=[1,5,1,5], fee=2` | `4` | a deep dip, so two separate trades win (one long hold gives 2, subtracting one fee only gives 6) |
| 13 | `prices=[2,1,4,4,2,3,2,5,1,2], fee=1` | `4` | longer mixed case with flat steps and several dips |
