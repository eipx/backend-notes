# Best Time to Buy and Sell Stock IV
`ref: LC 188` · Difficulty: Hard · Pattern: state machine with a transaction-count dimension (hold and cash for each transaction number), updated one day at a time; `k` at least `n / 2` collapses to the unlimited case

## Problem

You are given an integer `k` and an array `prices`, where `prices[i]` is the price of one share of a stock on day `i`. One transaction is a buy followed by a later sell. You may make at most `k` transactions, and you can hold at most one share at a time, so a share has to be sold before the next one is bought. Return the largest total profit you can make.

Input: an integer `k` (the most transactions allowed) and an integer array `prices`.
Output: a single integer, the maximum profit using at most `k` transactions. It is never negative, because making no transaction at all earns 0.

## Constraints

- `1 <= k <= 100`
- `1 <= prices.length <= 1000`
- `0 <= prices[i] <= 1000`
- On each day there is a choice between acting and waiting, so a plain recursion over every sequence of choices makes up to `2^n` paths, which is far too slow at `n = 1000`. Only `n * (k + 1) * 2` different situations exist (a day, the number of transactions used, and whether a share is held), at most about `200000`, so one table over those situations is fast.
- Once `k` reaches about half the number of days, the limit stops mattering, and a single pass over the prices is enough.
- The profit stays far below the `int` limit: at most 500 transactions of at most 1000 each, so at most `500000`.

## Worked examples

1. `k = 2`, `prices = [2, 4, 1]` -> `2`. Buy at 2 and sell at 4. A second transaction cannot earn anything, because after the 4 the price only falls.
2. `k = 2`, `prices = [3, 2, 6, 5, 0, 3]` -> `7`. Buy at 2 and sell at 6, which earns 4. Buy at 0 and sell at 3, which earns 3. The total is `4 + 3 = 7`.
3. `k = 1`, `prices = [3, 2, 6, 5, 0, 3]` -> `4`. With one transaction the best choice is the biggest gap with the buy before the sell: buy at 2 and sell at 6. The later climb from 0 to 3 is worth only 3.
4. `k = 2`, `prices = [1, 5, 2, 8, 3, 9]` -> `13`. There are three separate rises (1 to 5, 2 to 8, 3 to 9, worth 4, 6 and 6, so 16 with three transactions), but only two transactions are allowed. Buy at 1 and sell at 8 (holding through the dip from 5 to 2, which costs 3), then buy at 3 and sell at 9: `7 + 6 = 13`. Joining the last two rises instead would hold through the dip from 8 to 3, which costs 5, and gives only 11.

## Edge cases checklist

- A single day (the answer is 0, because a transaction needs two days).
- Prices that fall every day, or stay flat (the answer is 0).
- `k = 1`, the single-transaction problem: the best buy-before-sell gap, which is not the sum of the rises.
- `k` far above the number of days (the limit is not binding, and the answer is the sum of every day-to-day rise).
- `k` exactly equal to `n / 2` (the switch to the unlimited case happens right here), and `k` one below it (the table is still needed).
- An odd number of days: `n / 2` rounds down, so `n = 5` already switches at `k = 2`.
- One long rise: a steady climb needs only one transaction, however large `k` is.
- Price `0` (buying is free, and the profit equals the later price).
- Equal neighboring prices (flat steps inside a climb must not split it into extra transactions, and must not break it).
- A drop after the last peak (the best sell day is not always the last day).
- The upper bound, `k = 100` with `n = 1000`, about `100000` inner-loop steps (not among the tests here).
- The code also returns 0 for `k = 0` and for an empty array, although the constraints never send them.

## Approach

### Brute force

Recurse over the days with three pieces of state: the day `i`, the number of transactions still allowed, and whether a share is held. If a share is held, either sell today (add `prices[i]`, and the next state is not holding) or wait. If no share is held, either buy today (subtract `prices[i]`, use up one transaction, and the next state is holding) or wait. At the end of the days the answer is 0. Every day branches into two paths, so this makes up to `2^n` calls. The same triples `(day, transactions left, holding)` repeat constantly, and only about `n * k * 2` of them exist, so a table removes the blow-up.

### Optimal

Written as a full table, the state is (day, transaction number, holding or not). Each day's values depend only on the previous day's, so the day index is dropped and two arrays of length `k + 1` are updated in place, once per day. For each transaction number `t` from 1 to `k`:

- `hold[t]`: the best profit so far, if a share is held at the end of the day and at most `t` transactions have been started (the one in progress counts). The price paid for the share is already subtracted.
- `cash[t]`: the best profit so far, if no share is held at the end of the day and at most `t` transactions were used. `cash[0]` is always 0, because with no transactions there is no profit.

Base case (day 0): `cash[t] = 0` for every `t`, and `hold[t] = -prices[0]` for `t >= 1`, because the only possible move on day 0 is to buy the first share.

On each later day `i`, for each `t` from 1 to `k`:

- `cash[t] = max(cash[t], hold[t] + prices[i])`: either stay in cash, or sell the share that is held today.
- `hold[t] = max(hold[t], cash[t - 1] - prices[i])`: either keep holding, or buy today out of a position that has used at most `t - 1` transactions. This is the move that spends a transaction, which is why it reads layer `t - 1`.

The days are filled left to right. Inside one day, `t` runs from `k` down to `1`. Going downward means every read is yesterday's value: `cash[t - 1]` has not been touched yet today when `hold[t]` reads it, and `hold[t]` is not overwritten until after `cash[t]` has used it. The answer is `cash[k]`, the best profit with at most `k` transactions and no share in hand.

**The collapse for large `k`.** The best profit with unlimited transactions is the sum of every day-to-day rise. Group the rises into maximal climbs (stretches where each day is higher than the one before). Each climb is one transaction, buying at its start and selling at its end. Two climbs never share a day, because the step right after a climb's last day is not a rise, and each climb covers at least two days. So there are at most `n / 2` climbs (rounded down), and the unlimited answer needs at most `n / 2` transactions. When `k >= n / 2`, the limit cannot bind, and the answer is the plain sum of rises in one pass. That also keeps the arrays small: without it, a huge `k` would allocate arrays of that size.

**Key invariant:** after day `i`, for every `t`, `cash[t]` is the best profit over days `0` to `i` using at most `t` transactions and ending without a share, and `hold[t]` is the best profit over days `0` to `i` ending with a share held and at most `t` transactions started (the one in progress counts). Each value is the best of its two moves, and each move reads a value from the previous day (the descending order of `t` makes sure of that), so by induction on the day the final `cash[k]` is the answer.

### Step-by-step trace

Values after each day for `k = 2`, `prices = [3, 2, 6, 5, 0, 3]` (here `n = 6` and `n / 2 = 3`, so `k = 2` is below the switch and the table is used).

| day | price | hold[1] | hold[2] | cash[1] | cash[2] |
|---|---|---|---|---|---|
| 0 | 3 | -3 | -3 | 0 | 0 |
| 1 | 2 | -2 | -2 | 0 | 0 |
| 2 | 6 | -2 | -2 | 4 | 4 |
| 3 | 5 | -2 | -1 | 4 | 4 |
| 4 | 0 | 0 | 4 | 4 | 4 |
| 5 | 3 | 0 | 4 | 4 | 7 |

A few cells worked out. On day 1 the price 2 is lower than the 3 paid on day 0, so `hold[1] = max(-3, cash[0] - 2) = -2`, and in the same way `hold[2] = max(-3, cash[1] - 2) = -2`. On day 2 selling at 6 gives `cash[1] = max(0, -2 + 6) = 4`, and `cash[2]` becomes 4 as well. On day 3 `hold[2] = max(-2, cash[1] - 5) = -1`: the second transaction starts from the 4 already earned and spends 5, which is better than the old `-2`. On day 4 the price is 0, so `hold[2] = max(-1, cash[1] - 0) = 4` and `hold[1] = max(-2, cash[0] - 0) = 0`. On day 5 selling at 3 gives `cash[2] = max(4, 4 + 3) = 7`. The answer is `cash[2] = 7`.

Walking back from the end: `cash[2] = 7` came from selling on day 5 the share with `hold[2] = 4`; that `hold[2]` was set on day 4 by buying at 0 out of `cash[1] = 4`; and `cash[1] = 4` was set on day 2 by selling at 6 the share with `hold[1] = -2`, bought at 2 on day 1. Those are the two transactions of worked example 2.

## Java 8 solution
```java
public class BestTimeToBuyAndSellStockIV {

    // Largest profit from at most k transactions, where one transaction is a
    // buy followed by a later sell and only one share may be held at a time.
    //
    // For every transaction number t from 1 to k two values are kept, updated
    // once per day:
    //   hold[t] = best profit so far while holding a share, with at most t
    //             transactions started (the open one counts); the price paid
    //             is already subtracted
    //   cash[t] = best profit so far while holding nothing, using at most t
    //             transactions (cash[0] stays 0: no transactions, no profit)
    public static int solve(int k, int[] prices) {
        int n = prices.length;
        if (n < 2 || k < 1) {
            return 0;
        }
        // Every useful transaction needs its own buy day and sell day, so more
        // than n / 2 of them can never help. Past that point the limit is not
        // binding, and the unlimited-trades answer is exact.
        if (k >= n / 2) {
            return unlimited(prices);
        }

        int[] hold = new int[k + 1];
        int[] cash = new int[k + 1];
        // Day 0: the only possible move is to buy, whichever trade it counts as.
        for (int t = 1; t <= k; t++) {
            hold[t] = -prices[0];
        }

        for (int i = 1; i < n; i++) {
            // Walk t downward so that cash[t - 1] has not been touched yet
            // today when hold[t] reads it: every read is yesterday's value.
            for (int t = k; t >= 1; t--) {
                cash[t] = Math.max(cash[t], hold[t] + prices[i]);
                hold[t] = Math.max(hold[t], cash[t - 1] - prices[i]);
            }
        }
        return cash[k];
    }

    // With no limit on the number of trades, the best profit is the sum of
    // every day-to-day rise.
    private static int unlimited(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }
        return profit;
    }

    private static void check(int caseNum, int k, int[] prices, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(k, prices);
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

        check(1, 2, new int[]{2, 4, 1}, 2, fail, total);
        check(2, 2, new int[]{3, 2, 6, 5, 0, 3}, 7, fail, total);
        check(3, 1, new int[]{5}, 0, fail, total);
        check(4, 2, new int[]{1, 2, 3, 4, 5}, 4, fail, total);
        check(5, 2, new int[]{5, 4, 3, 2, 1}, 0, fail, total);
        check(6, 1, new int[]{3, 2, 6, 5, 0, 3}, 4, fail, total);
        check(7, 100, new int[]{1, 5, 2, 8, 3, 9}, 16, fail, total);
        check(8, 2, new int[]{1, 5, 2, 8, 3, 9}, 13, fail, total);
        check(9, 1, new int[]{4, 4, 4, 4}, 0, fail, total);
        check(10, 3, new int[]{1, 3, 2, 4, 3, 5}, 6, fail, total);
        check(11, 2, new int[]{1, 3, 2, 4, 3, 5}, 5, fail, total);
        check(12, 2, new int[]{3, 3, 5, 0, 0, 3, 1, 4}, 6, fail, total);
        check(13, 2, new int[]{1, 2, 4, 2, 5, 7, 2, 4, 9, 0}, 13, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n * k) when `k < n / 2`: each of the `n - 1` days updates `2k` values with constant work. When `k >= n / 2` the single pass is O(n), so overall the time is O(n * min(k, n / 2)). Space O(k) for the two arrays (O(1) in the unlimited case). The arrays are never indexed by the day, which is why a full `n * k` table is not needed.

## Java 8 pitfalls for this problem

- Allocating `new int[k]` instead of `new int[k + 1]`. Layer `k` must exist, and layer `0` is the "no transactions" layer that `hold[1]` reads. The failure is an `ArrayIndexOutOfBoundsException`.
- Leaving `hold` at its default of 0. A new `int[]` is all zeros, which is right for `cash` but wrong for `hold`: it means "a share held for free". With `k = 1` and `prices = [5, 4, 3, 2, 1]` that reports a profit of 4 instead of 0. Set `hold[t] = -prices[0]` before the day loop.
- Reading `cash[t]` instead of `cash[t - 1]` in the buy move. The buy has to start from a state with one transaction fewer. Reading the same layer lets one layer buy again and again, so `k` stops limiting anything.
- Using `Integer.MIN_VALUE` as negative infinity for `hold`. It works here, since only non-negative prices are added to it, but it wraps around to a huge positive number as soon as anything is subtracted from it (the fee variant does exactly that). Starting from `-prices[0]` needs no placeholder.
- Writing `k > n / 2` where `k >= n / 2` is meant, or `n / 3` where `n / 2` is meant. Integer division rounds down, so `n = 5` gives `2`. Switching too late only costs time, but switching too early gives wrong answers (test case 11).
- Skipping the switch for large `k`. With `k <= 100` it is only a speed-up, but the older form of this problem allowed `k` up to a billion, and `new int[k + 1]` would run out of memory. The switch makes the code independent of that bound.
- Sharing one array for both arrays by accident, as in `int[] hold = new int[k + 1]; int[] cash = hold;`. Both names then point at the same memory, and every update corrupts the other.
- Letting `prices.length` be 0 or 1 reach `prices[0]`. The early return `n < 2` covers both, and a single day correctly gives 0.
- Using `Math.max` with three arguments. In Java 8 it takes exactly two, and every update here is a two-way choice.
- A top-down recursion goes `n` frames deep, at most 1000 here, which is safe on the default stack. Its memo must be keyed on all three of day, transactions left and holding, or answers from different situations get mixed.

## Wrong approaches and why they fail

1. **Take the `k` largest day-to-day rises.** A transaction can span many days, so rises that follow each other must be chained, not counted separately. Counterexample: `k = 1`, `prices = [1, 2, 3, 4, 5]`. The largest single rise is 1, so the method returns `1`. The correct answer is `4`.
2. **Split the prices into climbs and take the `k` largest climbs.** This never joins two climbs across a small dip, which a transaction may do. Counterexample: `k = 1`, `prices = [1, 5, 4, 8, 0, 2]`. The climbs are worth 4, 4 and 2, so the method returns `4`. Buying at 1 and selling at 8 earns `7`, which is correct. With `k = 2` the method returns `8`, and the correct answer is `9` (1 to 8, then 0 to 2).
3. **Let the buy read `cash[t]` instead of `cash[t - 1]`.** The transaction count then never goes up, so one layer can trade as often as it likes. Counterexample: `k = 1`, `prices = [1, 3, 2, 4]`. The method makes two trades (1 to 3, then 2 to 4) and returns `4`. With one transaction the correct answer is `3`.
4. **Leave `hold` at zero.** The first sell then cashes in a share that was never bought. Counterexample: `k = 1`, `prices = [5, 4, 3, 2, 1]`. The method returns `4`, and the correct answer is `0`.
5. **Switch to the unlimited answer too early, for example at `k >= n / 3`.** Counterexample: `k = 2`, `prices = [1, 3, 2, 4, 3, 5]` has `n = 6`, so `n / 3 = 2` and the method switches. It returns the sum of rises, `6`. Only two transactions are allowed, and the correct answer is `5` (1 to 4, then 3 to 5).

## Variants

1. **Special cases.** LC 121 is `k = 1`, LC 122 is unlimited transactions, and LC 123 is `k = 2`. All three are answered by this code (test cases 6, 7 and 12).
2. **The answer for every limit at once.** After the last day, `cash[1]` to `cash[k]` hold the best profit for each limit from 1 to `k`.
3. **Recover the transactions.** Keep a full table over (day, `t`) with a record of which move won, and walk back from `cash[k]` on the last day.
4. **A fee per transaction (LC 714).** Subtract the fee in the sell move. See `best-time-to-buy-and-sell-stock-with-transaction-fee.md`. The switch for large `k` still applies.
5. **A cooldown day after each sell (LC 309).** Add a third state for the day right after a sell, per transaction number if `k` is limited.
6. **Prices that arrive one at a time.** The day loop reads only the current price, so the memory stays O(`k`) however long the stream is.
7. **A much larger `k` than the number of days.** The `k >= n / 2` switch handles it without allocating anything of size `k`.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `k=2, prices=[2,4,1]` | `2` | LeetCode example 1; one profitable rise, the second transaction earns nothing |
| 2 | `k=2, prices=[3,2,6,5,0,3]` | `7` | LeetCode example 2; two rises (2 to 6 and 0 to 3) and a limit that binds |
| 3 | `k=1, prices=[5]` | `0` | a single day, no transaction possible |
| 4 | `k=2, prices=[1,2,3,4,5]` | `4` | odd `n` with `k = n / 2` rounded down; one long climb needs one transaction |
| 5 | `k=2, prices=[5,4,3,2,1]` | `0` | falling prices, so no profit is possible; here `k = n / 2`, so the single-pass branch runs |
| 6 | `k=1, prices=[3,2,6,5,0,3]` | `4` | one transaction: the best single gap, not the sum of two rises |
| 7 | `k=100, prices=[1,5,2,8,3,9]` | `16` | `k` far above `n / 2`, unlimited trades, rises 4 + 6 + 6 |
| 8 | `k=2, prices=[1,5,2,8,3,9]` | `13` | three rises but two transactions: join the two across the cheapest dip (the largest-climbs method gives 12) |
| 9 | `k=1, prices=[4,4,4,4]` | `0` | flat prices (a `hold` left at 0 gives a profit of 4 here) |
| 10 | `k=3, prices=[1,3,2,4,3,5]` | `6` | `k = n / 2` exactly: three separate rises of 2 each |
| 11 | `k=2, prices=[1,3,2,4,3,5]` | `5` | one below the switch: the table is needed (switching at `n / 3` gives 6) |
| 12 | `k=2, prices=[3,3,5,0,0,3,1,4]` | `6` | repeated equal prices, two transactions (LC 123 example) |
| 13 | `k=2, prices=[1,2,4,2,5,7,2,4,9,0]` | `13` | longer mixed case ending on a drop; the best pair is 1 to 7 and 2 to 9 |
