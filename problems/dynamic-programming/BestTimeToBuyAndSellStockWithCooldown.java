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
