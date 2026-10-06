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
