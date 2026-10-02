// Maximum Subarray
// ref: LC 53
// Given an integer array nums, consider every non-empty run of adjacent elements
// (a subarray) and return the largest sum among them. The run must contain at
// least one element, so if every value is negative the answer is the largest
// single value.
// Array length is from 1 to 100000; each value is from -10000 to 10000.
// Examples: [-2, 1, -3, 4, -1, 2, 1, -5, 4] returns 6 (the run 4, -1, 2, 1);
// [-3, -1, -2] returns -1.
// Required: O(n) time, O(1) space.
// Run: javac --release 8 MaximumSubarray.java && java MaximumSubarray

import java.util.*;

class Solution {
    public int maxSubArray(int[] nums) {
        // TODO: implement
        return 0;
    }
}

public class MaximumSubarray {

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().maxSubArray(nums);
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

    private static int[] filled(int value, int count) {
        int[] a = new int[count];
        Arrays.fill(a, value);
        return a;
    }

    // 100000 values: a rich run, a dip worth crossing, another rich run, a poor tail.
    private static int[] generated() {
        int[] a = new int[100000];
        for (int i = 0; i < a.length; i++) {
            if (i < 40000) {
                a[i] = 10000;
            } else if (i < 50000) {
                a[i] = -10000;
            } else if (i < 90000) {
                a[i] = 10000;
            } else {
                a[i] = -10000;
            }
        }
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}, 6, fail, total);
        check(2, new int[]{1}, 1, fail, total);
        check(3, new int[]{5, 4, -1, 7, 8}, 23, fail, total);
        check(4, new int[]{-1}, -1, fail, total);
        check(5, new int[]{-10000}, -10000, fail, total);
        check(6, new int[]{10000}, 10000, fail, total);
        check(7, new int[]{-3, -2, -5, -1, -4}, -1, fail, total);
        check(8, new int[]{-2, -1}, -1, fail, total);
        check(9, new int[]{8, -19, 5, -4, 20}, 21, fail, total);
        check(10, new int[]{4, -10, 3, 3}, 6, fail, total);
        check(11, new int[]{2, -1, 2, -1, 2}, 4, fail, total);
        check(12, filled(10000, 100000), 1000000000, fail, total);
        check(13, generated(), 700000000, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
