// Best Time to Buy and Sell Stock IV
// ref: LC 188
// prices[i] is the price of one share on day i, and k is the most transactions
// you may make. One transaction is a buy followed by a later sell, and you can
// hold at most one share at a time (sell before you buy again). Return the
// largest total profit using at most k transactions.
// 1 <= k <= 100, 1 <= prices.length <= 1000, 0 <= prices[i] <= 1000.
// Required: O(n * k) time, and O(n) time when k is at least n / 2.
// Study page: ../best-time-to-buy-and-sell-stock-iv.md
// Run: javac --release 8 BestTimeToBuyAndSellStockIV.java && java BestTimeToBuyAndSellStockIV

import java.util.*;

class Solution {
    public int maxProfit(int k, int[] prices) {
        // TODO: implement
        return 0;
    }
}

public class BestTimeToBuyAndSellStockIV {

    private static void check(int caseNum, int k, int[] prices, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().maxProfit(k, prices);
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
