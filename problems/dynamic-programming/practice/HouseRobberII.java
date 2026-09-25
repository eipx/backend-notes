// House Robber II
// ref: LC 213
// Houses stand in a circle; nums[i] is the money in house i. Adjacent houses
// cannot both be robbed, and the first and last houses are adjacent. Return
// the maximum money that can be robbed.
// Required: O(n) time, O(1) space.
// Study page: ../house-robber-ii.md
// Run: javac --release 8 HouseRobberII.java && java HouseRobberII

import java.util.*;

class Solution {
    public int rob(int[] nums) {
        // TODO: implement
        return 0;
    }
}

public class HouseRobberII {

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

        check(1, new int[]{5}, 5, fail, total);
        check(2, new int[]{2, 3}, 3, fail, total);
        check(3, new int[]{2, 10, 2}, 10, fail, total);
        check(4, new int[]{4, 4, 4, 4, 4, 4}, 12, fail, total);
        check(5, new int[]{0, 0, 0, 0, 0}, 0, fail, total);
        check(6, new int[]{10, 1, 1, 1}, 11, fail, total);
        check(7, new int[]{1, 1, 1, 10}, 11, fail, total);
        check(8, new int[]{3, 6, 4, 8, 3}, 14, fail, total);
        check(9, new int[]{10, 1, 10, 1, 10, 1}, 30, fail, total);
        check(10, new int[]{5, 1, 1, 5}, 6, fail, total);
        check(11, new int[]{5, 1, 5, 1, 1}, 10, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
