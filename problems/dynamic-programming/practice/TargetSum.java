// Target Sum
// ref: LC 494
// Given an array of non-negative integers nums and an integer target, put a plus
// or a minus sign in front of every number and add them up. Return the number of
// different sign assignments whose total equals target. Two assignments are
// different when they differ at any position, even if the numbers there are equal.
// The array length is from 1 to 20; each value is from 0 to 1000; the sum of all
// values is at most 1000; target is from -1000 to 1000.
// Required: O(n * sum) time.
// Study page: ../target-sum.md
// Run: javac --release 8 TargetSum.java && java TargetSum

import java.util.*;

class Solution {
    public int findTargetSumWays(int[] nums, int target) {
        // TODO: implement
        return 0;
    }
}

public class TargetSum {

    private static void check(int caseNum, int[] nums, int target, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().findTargetSumWays(nums, target);
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

        check(1, new int[]{1, 1, 1, 1, 1}, 3, 5, fail, total);
        check(2, new int[]{1}, 1, 1, fail, total);
        check(3, new int[]{1, 1, 1, 1, 1}, -3, 5, fail, total);
        check(4, new int[]{1}, 3, 0, fail, total);
        check(5, new int[]{0}, 0, 2, fail, total);
        check(6, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1}, 1, 256, fail, total);
        check(7, new int[]{1, 2}, 2, 0, fail, total);
        check(8, new int[]{1, 2, 3}, 6, 1, fail, total);
        check(9, new int[]{1, 2, 3}, -6, 1, fail, total);
        check(10, new int[]{1, 2, 3}, 0, 2, fail, total);
        check(11, new int[]{1, 2, 3}, -8, 0, fail, total);
        check(12, new int[]{1, 2, 3}, -7, 0, fail, total);
        check(13, new int[]{100, 200, 300, 400}, 200, 2, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
