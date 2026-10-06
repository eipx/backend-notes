// Coin Change II
// ref: LC 518
// Given a target amount and an array of distinct coin values, return the number
// of different combinations of coins that add up to exactly that amount. You may
// use as many coins of each value as you like. Two combinations are the same
// when they use each coin value the same number of times (1 + 2 and 2 + 1 are one
// combination). If no combination makes the amount, return 0. An amount of 0 has
// exactly one combination, the empty one.
// There are 1 to 300 coin values; each value is from 1 to 5000; the amount is
// from 0 to 5000. The answer is guaranteed to fit in a 32-bit signed integer.
// Required: O(amount * coins.length) time, O(amount) space.
// Study page: ../coin-change-ii.md
// Run: javac --release 8 CoinChangeII.java && java CoinChangeII

import java.util.*;

class Solution {
    public int change(int amount, int[] coins) {
        // TODO: implement
        return 0;
    }
}

public class CoinChangeII {

    private static void check(int caseNum, int amount, int[] coins, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().change(amount, coins);
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

        check(1, 5, new int[]{1, 2, 5}, 4, fail, total);
        check(2, 3, new int[]{2}, 0, fail, total);
        check(3, 10, new int[]{10}, 1, fail, total);
        check(4, 0, new int[]{7}, 1, fail, total);
        check(5, 3, new int[]{1, 2}, 2, fail, total);
        check(6, 4, new int[]{1, 2, 3}, 4, fail, total);
        check(7, 7, new int[]{2, 4}, 0, fail, total);
        check(8, 1, new int[]{2, 3, 5}, 0, fail, total);
        check(9, 10, new int[]{1, 2, 5}, 10, fail, total);
        check(10, 3, new int[]{5, 1, 2}, 2, fail, total);
        check(11, 200, new int[]{1, 2, 5, 10, 20, 50, 100, 200}, 73682, fail, total);
        check(12, 500, new int[]{3, 5, 7, 8, 9, 10, 11}, 35502874, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
