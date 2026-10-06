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
