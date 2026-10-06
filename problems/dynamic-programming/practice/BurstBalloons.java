// Burst Balloons
// ref: LC 312
// There are n balloons in a row, and nums[i] is the number painted on balloon
// i. You must burst all of them. Bursting balloon i earns
// nums[i - 1] * nums[i] * nums[i + 1] coins, where i - 1 and i + 1 are the
// neighbors that are still unburst at that moment. A neighbor past either end
// of the row counts as a balloon painted with 1. Return the maximum number of
// coins that bursting all the balloons can earn.
// 1 <= n <= 300; 0 <= nums[i] <= 100. One test uses an empty row, outside
// these limits.
// Required: O(n^3) time, O(n^2) space.
// Study page: ../burst-balloons.md
// Run: javac --release 8 BurstBalloons.java && java BurstBalloons

import java.util.*;

class Solution {
    public int maxCoins(int[] nums) {
        // TODO: implement
        return 0;
    }
}

public class BurstBalloons {

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().maxCoins(nums);
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

        check(1, new int[]{3, 1, 5, 8}, 167, fail, total);
        check(2, new int[]{1, 5}, 10, fail, total);
        check(3, new int[]{}, 0, fail, total);
        check(4, new int[]{7}, 7, fail, total);
        check(5, new int[]{0, 0, 0}, 0, fail, total);
        check(6, new int[]{1, 1, 1, 1}, 4, fail, total);
        check(7, new int[]{3, 0, 5}, 20, fail, total);
        check(8, new int[]{2, 3, 4}, 36, fail, total);
        check(9, new int[]{1, 2, 3, 4, 5}, 110, fail, total);
        check(10, new int[]{5, 4, 3, 2, 1}, 110, fail, total);
        check(11, new int[]{2, 5, 1, 7, 3}, 179, fail, total);
        check(12, new int[]{100, 100, 100}, 1010100, fail, total);

        // 300 balloons worth 100 each: the largest input the limits allow.
        int[] big = new int[300];
        for (int i = 0; i < big.length; i++) {
            big[i] = 100;
        }
        check(13, big, 298010100, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
