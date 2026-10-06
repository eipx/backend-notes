// 0-1 Knapsack
// ref: classic
// You are given n items. Item i has a weight weights[i] and a value values[i].
// A bag holds a total weight of at most capacity. Choose a set of items, each
// at most once, whose total weight is at most capacity, so that the total value
// is as large as possible. Return that largest total value.
// 0 <= n <= 100, weights.length == values.length, 1 <= weights[i] <= 10000,
// 0 <= values[i] <= 1000000, 0 <= capacity <= 10000.
// Required: O(n * capacity) time.
// Study page: ../zero-one-knapsack.md
// Run: javac --release 8 ZeroOneKnapsack.java && java ZeroOneKnapsack

import java.util.*;

class Solution {
    public int knapsack(int[] weights, int[] values, int capacity) {
        // TODO: implement
        return 0;
    }
}

public class ZeroOneKnapsack {

    private static void check(int caseNum, int[] weights, int[] values, int capacity, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().knapsack(weights, values, capacity);
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

        check(1, new int[]{1, 3, 4, 5}, new int[]{1, 4, 5, 7}, 7, 9, fail, total);
        check(2, new int[]{2, 3, 4, 5}, new int[]{3, 4, 5, 6}, 5, 7, fail, total);
        check(3, new int[]{10, 20, 30}, new int[]{60, 100, 120}, 50, 220, fail, total);
        check(4, new int[]{6, 5, 5}, new int[]{10, 7, 7}, 10, 14, fail, total);
        check(5, new int[]{}, new int[]{}, 10, 0, fail, total);
        check(6, new int[]{3, 4}, new int[]{5, 6}, 0, 0, fail, total);
        check(7, new int[]{5}, new int[]{10}, 5, 10, fail, total);
        check(8, new int[]{6}, new int[]{10}, 5, 0, fail, total);
        check(9, new int[]{1, 2, 3}, new int[]{6, 10, 12}, 6, 28, fail, total);
        check(10, new int[]{2}, new int[]{5}, 10, 5, fail, total);
        check(11, new int[]{3, 4}, new int[]{4, 5}, 10, 9, fail, total);
        check(12, new int[]{3, 34, 4, 12, 5, 2}, new int[]{3, 34, 4, 12, 5, 2}, 9, 9, fail, total);
        check(13, new int[]{1, 2, 3, 10}, new int[]{10, 10, 10, 100}, 10, 100, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
