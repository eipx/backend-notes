// House Robber
// ref: LC 198
// Houses stand in a row; nums[i] is the money in house i. Two neighboring
// houses cannot both be robbed. Return the maximum money that can be robbed.
// Input: an integer array nums with 1 to 100 entries, each in 0..400.
// Output: the largest total as an int.
// Example 1: [1,2,3,1] gives 4 (houses 0 and 2).
// Example 2: [2,7,9,3,1] gives 12 (houses 0, 2 and 4).
// Required: O(n) time, O(1) space.
// Study page: ../house-robber.md
// Run: javac --release 8 HouseRobber.java && java HouseRobber

import java.util.*;

class Solution {
    public int rob(int[] nums) {
        // TODO: implement
        return 0;
    }
}

public class HouseRobber {

    // A row of n houses that all hold the same amount.
    private static int[] filled(int n, int value) {
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = value;
        }
        return nums;
    }

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().rob(nums);
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

        check(1, new int[]{1, 2, 3, 1}, 4, fail, total);
        check(2, new int[]{2, 7, 9, 3, 1}, 12, fail, total);
        check(3, new int[]{5}, 5, fail, total);
        check(4, new int[]{0}, 0, fail, total);
        check(5, new int[]{2, 1}, 2, fail, total);
        check(6, new int[]{1, 2}, 2, fail, total);
        check(7, new int[]{0, 0, 0, 0}, 0, fail, total);
        check(8, new int[]{4, 4, 4, 4, 4}, 12, fail, total);
        check(9, new int[]{2, 3, 2}, 4, fail, total);
        check(10, new int[]{1, 100, 1}, 100, fail, total);
        check(11, new int[]{2, 1, 1, 2}, 4, fail, total);
        check(12, new int[]{5, 1, 1, 5, 1, 1, 5}, 15, fail, total);
        check(13, filled(100, 400), 20000, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
