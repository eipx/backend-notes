// Best Time to Buy and Sell Stock
// ref: LC 121
// prices[i] is the price of one share on day i. Pick one day to buy and a
// strictly later day to sell, and return the largest possible profit (sell price
// minus buy price). If no trade can earn anything, return 0 (not trading at all
// is allowed).
// Input: int[] prices. Output: int, the best profit, or 0.
// Examples:
//   [7,1,5,3,6,4] gives 5 (buy at 1 on day 1, sell at 6 on day 4).
//   [7,6,4,3,1] gives 0 (the price only falls, so do not trade).
// Constraints: 1 <= prices.length <= 100000, 0 <= prices[i] <= 10000
// Required complexity: O(n) time, O(1) extra space
// Run: javac --release 8 BestTimeToBuyAndSellStock.java && java BestTimeToBuyAndSellStock

import java.util.*;

class Solution {
    public int maxProfit(int[] prices) {
        // TODO: implement
        return 0;
    }
}

public class BestTimeToBuyAndSellStock {

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

    // 100000 days: the price slides from 10000 down to 5001, then climbs back up
    // to 10000. Best trade: buy at 5001, sell at 10000.
    private static int[] valleyThenClimb() {
        int[] a = new int[100000];
        for (int i = 0; i < 50000; i++) {
            a[i] = 10000 - i / 10;
        }
        for (int i = 50000; i < 100000; i++) {
            a[i] = 5001 + (i - 50000) / 10;
        }
        return a;
    }

    // 100000 days of a price that never rises: 10000 down to 1.
    private static int[] longSlide() {
        int[] a = new int[100000];
        for (int i = 0; i < a.length; i++) {
            a[i] = 10000 - i / 10;
        }
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{7, 1, 5, 3, 6, 4}, 5, fail, total);
        check(2, new int[]{7, 6, 4, 3, 1}, 0, fail, total);
        check(3, new int[]{5}, 0, fail, total);
        check(4, new int[]{0, 10000}, 10000, fail, total);
        check(5, new int[]{10000, 0}, 0, fail, total);
        check(6, new int[]{2, 4, 1}, 2, fail, total);
        check(7, new int[]{3, 3, 5, 0, 0, 3, 1, 4}, 4, fail, total);
        check(8, new int[]{1, 2}, 1, fail, total);
        check(9, new int[]{2, 1, 2, 0, 1}, 1, fail, total);
        check(10, new int[]{4, 4, 4, 4}, 0, fail, total);
        check(11, new int[]{3, 2, 6, 5, 0, 3}, 4, fail, total);
        check(12, valleyThenClimb(), 4999, fail, total);
        check(13, longSlide(), 0, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
