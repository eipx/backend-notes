// Coin Change
// ref: LC 322
// Given an array of distinct coin values and a target amount, return the fewest
// coins that add up to exactly that amount. You may use as many coins of each
// value as you like. If no combination of coins makes the amount, return -1.
// An amount of 0 needs 0 coins.
// There are 1 to 12 coin values; each value is from 1 to 2147483647 (2^31 - 1);
// the amount is from 0 to 10000.
// Required: O(amount * coins.length) time, O(amount) space.
// Study page: ../coin-change.md
// Run: javac --release 8 CoinChange.java && java CoinChange

import java.util.*;

class Solution {
    public int coinChange(int[] coins, int amount) {
        // TODO: implement
        return 0;
    }
}

public class CoinChange {

    private static void check(int caseNum, int[] coins, int amount, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().coinChange(coins, amount);
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

        check(1, new int[]{1, 2, 5}, 11, 3, fail, total);
        check(2, new int[]{2}, 3, -1, fail, total);
        check(3, new int[]{1}, 0, 0, fail, total);
        check(4, new int[]{1}, 1, 1, fail, total);
        check(5, new int[]{1}, 2, 2, fail, total);
        check(6, new int[]{1, 3, 4}, 6, 2, fail, total);
        check(7, new int[]{1, 5, 6, 9}, 11, 2, fail, total);
        check(8, new int[]{2, 5, 10, 1}, 27, 4, fail, total);
        check(9, new int[]{5, 10}, 3, -1, fail, total);
        check(10, new int[]{2, 4}, 7, -1, fail, total);
        check(11, new int[]{2147483647}, 2, -1, fail, total);
        check(12, new int[]{186, 419, 83, 408}, 6249, 20, fail, total);
        check(13, new int[]{1, 2, 5}, 10000, 2000, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
