// Best Time to Buy and Sell Stock with Transaction Fee
// ref: LC 714
// prices[i] is the price of one share on day i, and fee is a fixed amount.
// You may buy and sell as many times as you like, but you can hold at most one
// share at a time (sell before you buy again), and every completed trade
// (a buy followed by its sell) costs fee once. Return the largest total profit.
// 1 <= prices.length <= 50000, 1 <= prices[i] < 50000, 0 <= fee < 50000.
// Required: O(n) time.
// Study page: ../best-time-to-buy-and-sell-stock-with-transaction-fee.md
// Run: javac --release 8 BestTimeToBuyAndSellStockWithTransactionFee.java && java BestTimeToBuyAndSellStockWithTransactionFee

import java.util.*;

class Solution {
    public int maxProfit(int[] prices, int fee) {
        // TODO: implement
        return 0;
    }
}

public class BestTimeToBuyAndSellStockWithTransactionFee {

    private static void check(int caseNum, int[] prices, int fee, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().maxProfit(prices, fee);
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
