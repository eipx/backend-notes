// Partition Equal Subset Sum
// ref: LC 416
// Given an array of positive integers, return true if the elements can be
// split into two groups whose sums are equal, and false otherwise. Every
// element must go into exactly one of the two groups.
// Array length is from 1 to 200; each value is from 1 to 100.
// Required: O(n * sum) time, O(sum) space.
// Study page: ../partition-equal-subset-sum.md
// Run: javac --release 8 PartitionEqualSubsetSum.java && java PartitionEqualSubsetSum

import java.util.*;

class Solution {
    public boolean canPartition(int[] nums) {
        // TODO: implement
        return false;
    }
}

public class PartitionEqualSubsetSum {

    private static void check(int caseNum, int[] nums, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            boolean got = new Solution().canPartition(nums);
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

        check(1, new int[]{1, 5, 11, 5}, true, fail, total);
        check(2, new int[]{1, 2, 3, 5}, false, fail, total);
        check(3, new int[]{1}, false, fail, total);
        check(4, new int[]{2, 2}, true, fail, total);
        check(5, new int[]{1, 2}, false, fail, total);
        check(6, new int[]{1, 1, 1, 1}, true, fail, total);
        check(7, new int[]{3, 3, 3, 4, 5}, true, fail, total);
        check(8, new int[]{1, 2, 5}, false, fail, total);
        check(9, new int[]{2, 2, 3, 5}, false, fail, total);
        check(10, new int[]{14, 9, 8, 4, 3, 2}, true, fail, total);
        check(11, new int[]{1, 2, 3, 4, 5}, false, fail, total);
        check(12, new int[]{99, 1, 100}, true, fail, total);
        check(13, new int[]{100, 100, 100, 100, 100, 100, 100, 100}, true, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
