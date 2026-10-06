# Best Time to Buy and Sell Stock with Cooldown
`ref: LC 309` · Difficulty: Medium · Pattern: a three-state machine over the days (holding a share, not holding and free to buy, not holding and cooling down)

## Problem

`prices[i]` is the price of one share of a stock on day `i`. You may buy and sell as often as you like, under two rules. You can hold at most one share at a time, so a share must be sold before the next one is bought. After you sell, you cannot buy on the very next day, which is a one-day cooldown. A buy or a sale happens at that day's price. Return the largest total profit, which is 0 if no trade makes money.

Input: an integer array `prices`, where entry `i` is the price on day `i`.
Output: a single integer, the largest profit over all legal sequences of buys and sales.

## Constraints

- `1 <= prices.length <= 5000` (the runner also tries an empty array and expects 0)
- `0 <= prices[i] <= 1000`
- Each day has three possible actions (rest, buy, sell) and at most two of them are legal, so trying every sequence still takes exponential time (the plain recursion below already makes about 3.8 million calls for 25 days). Only three situations can exist at the end of a day, so one stored value per day and situation is enough, and the work is linear.
- The profit is at most `5000 * 1000 = 5000000`, which fits in `int`.

## Worked examples

1. `prices = [1, 2, 3, 0, 2]` -> `3`. Buy on day 0 (price 1), sell on day 1 (price 2) for a profit of 1, rest on day 2, buy on day 3 (price 0), sell on day 4 (price 2) for a profit of 2. Selling on day 2 at the higher price 3 would make day 3 a cooldown day, and the buy at 0 would be blocked, so that plan earns only 2.
2. `prices = [1]` -> `0`. With one day there is no later day to sell on.
3. `prices = [6, 1, 3, 2, 4, 7]` -> `6`. Buy at 1 on day 1 and sell at 7 on day 5. Selling at 3 on day 2 makes day 3 a cooldown day, and the next buy would be at 4 on day 4, so that plan earns `2 + 3 = 5`. Without the cooldown rule, buying at 1, selling at 3, buying at 2 and selling at 7 would earn 7.
4. `prices = [5, 4, 3, 2, 1]` -> `0`. The price only falls, so the best plan is to do nothing.

## Edge cases checklist

- An empty array (the answer is 0). The constraints promise at least one day, but the loop handles it without a special case.
- A single day (the answer is 0).
- Two days, rising (`[1, 2]` is 1) and falling (the answer is 0).
- Flat prices (the answer is 0, because buying and selling at the same price gains nothing).
- Strictly falling prices (never trade, the answer is 0).
- Strictly rising prices (`[1, 2, 3, 4, 5]` is 4). One trade from the first day to the last is best, because every sale in the middle triggers a cooldown that costs a day of the climb.
- A cooldown day that blocks a good buy (`[1, 2, 3, 0, 2]`, `[6, 1, 3, 2, 4, 7]` and `[2, 1, 2, 0, 1]`).
- Several trades where the cooldown skips exactly one day between them (`[2, 1, 4, 5, 2, 9, 7]` is 10).
- A sale on the last day. The final answer has to include the state "sold today", not only the state "free".
- A price of 0 on some day (a share can cost nothing).
- The upper bound, 5000 days alternating between 0 and 1000 (not every low-high pair can be used, because of the cooldown).

## Approach

### Brute force

Walk through the days with `f(i, holding, cooling)`, the best profit from day `i` onward. On each day there are three possible actions, and at most two are legal. Rest: move to day `i + 1` with the cooldown over. Sell, if holding a share: earn `prices[i]` and move to day `i + 1` with the cooldown active. Buy, if not holding and not cooling down: pay `prices[i]` and move to day `i + 1` holding. Take the best of the actions that are allowed. With no memory this branches up to two ways per day, and the number of calls grows exponentially (about 3.8 million calls for 25 days). But `holding` and `cooling` never both hold, so only three situations exist per day, and the same `(i, situation)` pairs are reached over and over. Storing one answer for each removes the blow-up.

### Optimal

Name the three situations at the end of day `i`, and keep the best profit so far for each:

- `hold[i]`: a share is held. The profit already counts what the share cost.
- `sold[i]`: a share was sold today, so tomorrow is a cooldown day.
- `free[i]`: no share is held and nothing was sold today, so buying is allowed tomorrow.

Each day exactly one action is taken, and the action allowed depends only on yesterday's situation:

| yesterday | action today | today | profit change |
|---|---|---|---|
| free | rest | free | 0 |
| free | buy | hold | `-price` |
| hold | rest | hold | 0 |
| hold | sell | sold | `+price` |
| sold | rest | free | 0 |

There is no row that goes from `sold` to `hold`. That missing row is the cooldown rule. Reading the table by the situation the day ends in, with `price = prices[i]`:

- `hold[i] = max(hold[i - 1], free[i - 1] - price)`: keep the share, or buy today from the free situation.
- `sold[i] = hold[i - 1] + price`: sell the share that was held at the end of yesterday.
- `free[i] = max(free[i - 1], sold[i - 1])`: stay free, or finish the cooldown that yesterday's sale started.

Base case: before day 0 nothing has happened, so `free = 0`, and `hold` and `sold` are impossible, which is written as minus infinity. The days are processed from first to last, and each day reads only the previous day's three values, so three variables are enough. The answer is `max(free, sold)` after the last day. The situation `hold` is left out because ending with a share in hand never beats selling it: prices are not negative, so selling on the last day is worth at least as much, and a share bought on the last day only lowers the profit.

**Key invariant:** after day `i`, `hold`, `sold` and `free` are the exact best profits over all legal action sequences for days `0` to `i` that end in that situation, or minus infinity when no such sequence exists. Every legal sequence for days `0` to `i` is a legal sequence for days `0` to `i - 1` followed by one action on day `i`, and the table above lists exactly which actions each situation allows and where they lead. So each new value is a maximum over the exact previous values that lead to it, and by induction the three values stay exact. The cooldown is built into the table, because a sale can only lead to `sold` and then to `free`, never straight to `hold`.

### Step-by-step trace

Run on `prices = [1, 2, 3, 0, 2]`. The row `start` is the state before day 0, and `-inf` stands for an impossible state.

| day | price | hold | sold | free |
|---|---|---|---|---|
| start | | -inf | -inf | 0 |
| 0 | 1 | -1 | -inf | 0 |
| 1 | 2 | -1 | 1 | 0 |
| 2 | 3 | -1 | 2 | 1 |
| 3 | 0 | 1 | -1 | 2 |
| 4 | 2 | 1 | 3 | 2 |

A few cells worked out. Day 0: `hold = max(-inf, 0 - 1) = -1` (buy for 1), and `sold` stays impossible because nothing was held yesterday. Day 1: `sold = hold + 2 = -1 + 2 = 1`, which is the sale for a profit of 1. Day 2: `free = max(0, sold of day 1 = 1) = 1`, because yesterday's sale has finished its cooldown. Day 3: `hold = max(-1, free of day 2 - 0) = max(-1, 1) = 1`. The buy reads the `free` value of day 2, which is 1, and not the `sold` value of day 2, which is 2. That is the cooldown at work: the sale made on day 2 cannot be followed by a buy on day 3. Day 4: `sold = hold + 2 = 1 + 2 = 3`.

The answer is `max(free, sold) = max(2, 3) = 3`. Walking back from the `3`: it came from `hold` on day 3 (a profit of 1) plus the sale at 2. That `hold` came from `free` on day 2 (a profit of 1, bought at 0). That `free` came from `sold` on day 1 (the sale at 2). That `sold` came from `hold` on day 0 (bought at 1). So the plan is: buy on day 0, sell on day 1, rest on day 2, buy on day 3, sell on day 4, which is worked example 1.

## Java 8 solution
```java
public class BestTimeToBuyAndSellStockWithCooldown {

    // Largest profit from buying and selling one share of a stock over the
    // given daily prices, when a share must be sold before the next one is
    // bought and the day right after a sale is a cooldown day (no buying).
    //
    // Three states describe the end of a day:
    //   hold: a share is held (best profit so far, minus what it cost)
    //   sold: a share was sold today, so tomorrow is a cooldown
    //   free: no share is held and nothing was sold today, so buying is allowed
    // Each holds the best profit so far in that state, or "minus infinity"
    // when the state cannot be reached yet.
    public static int solve(int[] prices) {
        // Half of the int range, so that adding a price to it cannot wrap around.
        final int unreachable = Integer.MIN_VALUE / 2;
        int hold = unreachable;
        int sold = unreachable;
        int free = 0;
        for (int i = 0; i < prices.length; i++) {
            int price = prices[i];
            // All three new values are built from the previous day's values,
            // so they are computed first and assigned together.
            int nextHold = Math.max(hold, free - price);   // keep holding, or buy from the free state
            int nextSold = hold + price;                   // sell the share held since yesterday
            int nextFree = Math.max(free, sold);           // stay free, or finish yesterday's cooldown
            hold = nextHold;
            sold = nextSold;
            free = nextFree;
        }
        // Ending while holding a share can never beat selling it, because
        // prices are not negative.
        return Math.max(free, sold);
    }

    // A price list of n days that alternates between low and high, starting low.
    private static int[] alternating(int n, int low, int high) {
        int[] prices = new int[n];
        for (int i = 0; i < n; i++) {
            prices[i] = (i % 2 == 0) ? low : high;
        }
        return prices;
    }

    private static void check(int caseNum, int[] prices, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(prices);
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

        check(1, new int[]{1, 2, 3, 0, 2}, 3, fail, total);
        check(2, new int[]{1}, 0, fail, total);
        check(3, new int[]{}, 0, fail, total);
        check(4, new int[]{5, 5, 5, 5}, 0, fail, total);
        check(5, new int[]{5, 4, 3, 2, 1}, 0, fail, total);
        check(6, new int[]{1, 2}, 1, fail, total);
        check(7, new int[]{1, 2, 3, 4, 5}, 4, fail, total);
        check(8, new int[]{1, 4, 2}, 3, fail, total);
        check(9, new int[]{6, 1, 3, 2, 4, 7}, 6, fail, total);
        check(10, new int[]{2, 1, 2, 0, 1}, 1, fail, total);
        check(11, new int[]{1, 5, 0, 3, 0, 4}, 8, fail, total);
        check(12, new int[]{2, 1, 4, 5, 2, 9, 7}, 10, fail, total);
        check(13, alternating(5000, 0, 1000), 1250000, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n): each day does a constant amount of work, three assignments and a few comparisons. Space O(1): three integer variables and no table. A version that keeps a table of the three values for every day uses O(n) space, and it is the one to keep if you also need to recover the days on which to trade (see Variants).

## Java 8 pitfalls for this problem

- Minus infinity. `Integer.MIN_VALUE` is a trap as the "impossible" value, because subtracting anything from it wraps around to a huge positive number that then wins every `Math.max`. This recurrence only ever adds to the impossible states, so it would survive, but a small change (a transaction fee, a different buy rule) would not. `Integer.MIN_VALUE / 2` is the safe choice, since it leaves room for thousands of additions and subtractions.
- Default zeros. A new `int[n]` is filled with 0, and a `hold` entry of 0 means "holding a share that cost nothing". That makes every price a pure profit: starting `hold` at 0 returns 5 for `[5, 4, 3, 2, 1]`, which should be 0. Set `hold` explicitly, to minus infinity before day 0, or to `-prices[0]` if the loop starts at day 1.
- Updating the variables one after another in place. The new `sold` needs yesterday's `hold`, and the new `free` needs yesterday's `sold`, and the new `hold` needs yesterday's `free`. If `free` is updated first, the buy on the same day reads a `free` that already contains yesterday's sale, so the cooldown disappears. Updating in the order `hold`, `sold`, `free` fails in a different way: `free` then reads today's `sold`, so a sale made today lets you buy tomorrow. Both orders return 4 for `[1, 2, 3, 0, 2]`, where the answer is 3. The code computes the three new values into temporaries and then assigns them together.
- Prefix index and day index. In the table form, a day-`i` cell reads cells for day `i - 1`, which is index `-1` on day 0 and throws an `ArrayIndexOutOfBoundsException`. A base row for "before day 0" fixes it. The two-state version that reads `free` from two days back needs a base row of two cells, and shifting the indices by two is easy to get wrong.
- Array sizing. A table with one cell per day breaks on an empty price list as soon as the base case is written as `table[0]`. With a base row the table needs `n + 1` cells. The rolling variables need no sizing and no empty-array check.
- Overflow. The profit is at most `5000000` here, so `int` is safe. The impossible-state value is the only number that can go wrong, as described in the first item.
- Recursion depth. A top-down version goes as many calls deep as there are days, which is 5000 here. That is usually fine, but it is close to the limit of a small stack, and a longer price list overflows it. The loop has no depth.
- Returning from the wrong state. Returning only `free` drops a sale made on the last day. `[1, 2]` would give 0, and the answer is 1.

## Wrong approaches and why they fail

1. **Ignore the cooldown and add up every rise between consecutive days.** That is the answer when trading has no cooldown, and it can use a buy on the day right after a sale. Counterexample: `prices = [1, 2, 3, 0, 2]`. The rises are `1 + 1 + 2 = 4`, which is more than the correct `3`, because the buy at 0 on day 3 follows a sale on day 2. For `[6, 1, 3, 2, 4, 7]` it gives `7`, and the correct answer is `6`.
2. **Allow only one trade, the best single buy-then-sell pair.** The rule allows many trades, and a second trade can add a lot. Counterexample: `prices = [1, 5, 0, 3, 0, 4]`. The best single trade is worth `4` (buy at 1 and sell at 5, or buy at 0 and sell at 4). Two trades, buying at 1 and selling at 5, then buying at 0 on day 4 and selling at 4, give `4 + 4 = 8`, which is the correct answer.
3. **Start with `hold = 0` instead of minus infinity.** That pretends a share is already in hand and cost nothing. Counterexample: `prices = [5, 4, 3, 2, 1]`. The method sells the imaginary share at 5 and returns `5`, and the correct answer is `0`. Even `[1]` returns `1` instead of `0`.
4. **Return `free` at the end, without looking at `sold`.** A sale on the last day ends in the `sold` situation, and `free` only picks it up one day later, which never comes. Counterexample: `prices = [1, 2]`. The method returns `0`, and the correct answer is `1`.

## Variants

1. **Two-state version.** Keep only `hold` and `free`, and read the cooldown from two days back. Here `free` means that no share is held, whether or not a sale happened yesterday, so it is wider than the `free` of the three-state version. The updates are `free[i] = max(free[i - 1], hold[i - 1] + price)` and `hold[i] = max(hold[i - 1], free[i - 2] - price)`. It gives the same answers and needs a little history.
2. **Best Time to Buy and Sell Stock II (LC 122).** No cooldown, as many trades as you like. The `sold` situation disappears, and the answer is the sum of every rise between consecutive days.
3. **Best Time to Buy and Sell Stock with Transaction Fee (LC 714).** No cooldown, but each trade costs a fee. Two situations are enough, and the fee is subtracted once per trade, on the sale.
4. **Best Time to Buy and Sell Stock IV (LC 188).** At most `k` trades. Add the number of trades used so far as a second index of the state, which makes the time `O(n * k)`.
5. **A cooldown of `c` days.** A sale on day `j` makes days `j + 1` to `j + c` cooldown days, so `free[i] = max(free[i - 1], sold[i - c])`, and the answer is the best of `free` and the `sold` values of the last `c` days. This needs the last `c` values of `sold`, so it needs a table or a short queue.
6. **Return the days to trade.** Keep the table of the three values for every day and walk back from the best final state, as in the step-by-step trace.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `prices=[1,2,3,0,2]` | `3` | first example; the cooldown blocks the buy on day 3 after a sale on day 2 (ignoring the cooldown gives 4) |
| 2 | `prices=[1]` | `0` | one day, nothing to sell on |
| 3 | `prices=[]` | `0` | empty array, handled without a special case |
| 4 | `prices=[5,5,5,5]` | `0` | flat prices |
| 5 | `prices=[5,4,3,2,1]` | `0` | falling prices (a start with `hold = 0` gives 5) |
| 6 | `prices=[1,2]` | `1` | a sale on the last day (returning only `free` gives 0) |
| 7 | `prices=[1,2,3,4,5]` | `4` | rising prices, one trade over the whole range |
| 8 | `prices=[1,4,2]` | `3` | the best sale is before a later fall |
| 9 | `prices=[6,1,3,2,4,7]` | `6` | one long trade beats two short ones under the cooldown (ignoring the cooldown gives 7) |
| 10 | `prices=[2,1,2,0,1]` | `1` | the cooldown removes one of two small trades (ignoring the cooldown gives 2) |
| 11 | `prices=[1,5,0,3,0,4]` | `8` | two trades with a blocked day between them (one trade only gives 4) |
| 12 | `prices=[2,1,4,5,2,9,7]` | `10` | sell early at 4, rest, then buy at 2 and sell at 9 (ignoring the cooldown gives 11) |
| 13 | `prices=[0,1000,0,1000,...]` (5000 days) | `1250000` | upper bound, one trade every fourth day |
