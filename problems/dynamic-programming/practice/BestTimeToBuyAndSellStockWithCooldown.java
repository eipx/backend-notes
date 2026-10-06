// Best Time to Buy and Sell Stock with Cooldown
// ref: LC 309
// prices[i] is the price of one share of a stock on day i. You may make as many
// transactions as you like (a transaction is buying one share and selling it
// later), under two rules: you may hold at most one share at a time, so a share
// must be sold before the next one is bought, and after you sell you cannot buy
// on the very next day (a one-day cooldown). Return the maximum profit.
// Input: an integer array prices with 1 to 5000 entries, each in 0..1000.
// Output: the largest profit as an int (0 when no trade makes money).
// Example 1: [1,2,3,0,2] gives 3 (buy on day 0, sell on day 1, rest on day 2,
//            buy on day 3, sell on day 4).
// Example 2: [1] gives 0.
// The runner also tries an empty array, which should give 0.
// Required: O(n) time, O(1) space.
// Study page: ../best-time-to-buy-and-sell-stock-with-cooldown.md
// Run: javac --release 8 BestTimeToBuyAndSellStockWithCooldown.java && java BestTimeToBuyAndSellStockWithCooldown

import java.util.*;

class Solution {
    public int maxProfit(int[] prices) {
        // TODO: implement
        return 0;
    }
}

public class BestTimeToBuyAndSellStockWithCooldown {

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
        try {
            int got = new Solution().maxProfit(prices);
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
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
