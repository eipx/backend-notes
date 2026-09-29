// Longest Increasing Subsequence
// ref: LC 300
// Given an integer array, return the length of the longest sequence of its
// elements, taken in their original order, in which every element is strictly
// larger than the one before it. The elements do not have to be next to each
// other. Equal values do not count as increasing.
// Array length is from 1 to 2500; each value is from -10000 to 10000.
// Required: O(n^2) time is accepted; O(n log n) is the follow-up.
// Study page: ../longest-increasing-subsequence.md
// Run: javac --release 8 LongestIncreasingSubsequence.java && java LongestIncreasingSubsequence

import java.util.*;

class Solution {
    public int lengthOfLIS(int[] nums) {
        // TODO: implement
        return 0;
    }
}

public class LongestIncreasingSubsequence {

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().lengthOfLIS(nums);
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

        check(1, new int[]{10, 9, 2, 5, 3, 7, 101, 18}, 4, fail, total);
        check(2, new int[]{0, 1, 0, 3, 2, 3}, 4, fail, total);
        check(3, new int[]{7, 7, 7, 7, 7, 7, 7}, 1, fail, total);
        check(4, new int[]{1}, 1, fail, total);
        check(5, new int[]{1, 2, 3, 4, 5}, 5, fail, total);
        check(6, new int[]{5, 4, 3, 2, 1}, 1, fail, total);
        check(7, new int[]{4, 10, 4, 3, 8, 9}, 3, fail, total);
        check(8, new int[]{1, 3, 6, 7, 9, 4, 10, 5, 6}, 6, fail, total);
        check(9, new int[]{-1, -2, 0, -3, 1}, 3, fail, total);
        check(10, new int[]{2, 2, 3, 3, 4, 4}, 3, fail, total);
        check(11, new int[]{3, 5, 6, 2, 5, 4, 19, 5, 6, 7, 12}, 6, fail, total);
        check(12, new int[]{10000, -10000, 0, 10000}, 3, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
