// First Missing Positive
// ref: LC 41
// Given an unsorted integer array, find the smallest positive integer that
// does not appear in it.
// Required: O(n) time, O(1) extra space.
// Study page: ../first-missing-positive.md
// Run: javac --release 8 FirstMissingPositive.java && java FirstMissingPositive

import java.util.*;

class Solution {
    public int firstMissingPositive(int[] nums) {
        // TODO: implement
        return 0;
    }
}

public class FirstMissingPositive {

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().firstMissingPositive(nums);
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

        check(1, new int[]{1, 2, 0}, 3, fail, total);
        check(2, new int[]{3, 4, -1, 1}, 2, fail, total);
        check(3, new int[]{7, 8, 9, 11, 12}, 1, fail, total);
        check(4, new int[]{1, 2, 3}, 4, fail, total);
        check(5, new int[]{1}, 2, fail, total);
        check(6, new int[]{2}, 1, fail, total);
        check(7, new int[]{-1}, 1, fail, total);
        check(8, new int[]{1, 1}, 2, fail, total);
        check(9, new int[]{0, 0, 0}, 1, fail, total);
        check(10, new int[]{2, 3, 4, 5, 6}, 1, fail, total);
        check(11, new int[]{1, 2, 2, 3}, 4, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
