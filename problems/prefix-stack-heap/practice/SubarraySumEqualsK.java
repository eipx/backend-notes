// Subarray Sum Equals K
// ref: LC 560
// Given an array of integers and a target k, count how many contiguous
// subarrays sum exactly to k. A subarray is a run of consecutive elements;
// two subarrays differ if their index ranges differ, even with equal sums.
// Required: O(n) time, O(n) space.
// Study page: ../subarray-sum-equals-k.md
// Run: javac --release 8 SubarraySumEqualsK.java && java SubarraySumEqualsK

import java.util.*;

class Solution {
    public int subarraySum(int[] nums, int k) {
        // TODO: implement
        return -1;
    }
}

public class SubarraySumEqualsK {

    private static void check(int caseNum, int[] nums, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().subarraySum(nums, k);
            if (Integer.compare(got, expected) == 0) {
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

        check(1, new int[]{1, 1, 1}, 2, 2, fail, total);
        check(2, new int[]{1, 2, 3}, 3, 2, fail, total);
        check(3, new int[]{1}, 1, 1, fail, total);
        check(4, new int[]{1}, 0, 0, fail, total);
        check(5, new int[]{-1, -1, 1}, 0, 1, fail, total);
        check(6, new int[]{0, 0, 0, 0}, 0, 10, fail, total);
        check(7, new int[]{1, -1, 0}, 0, 3, fail, total);
        check(8, new int[]{1, 2, -3, 3}, 3, 3, fail, total);
        check(9, new int[]{-1, -2, -3}, -6, 1, fail, total);
        check(10, new int[]{5, -5, 5, -5}, 0, 4, fail, total);
        check(11, new int[]{1, 2, 3}, 100, 0, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
