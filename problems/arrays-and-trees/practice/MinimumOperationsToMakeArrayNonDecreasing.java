// Minimum Operations to Make Array Non-Decreasing
// ref: LC 3914
// You are given an array nums of positive integers. One operation picks a
// contiguous range of positions l..r (both ends included) and a positive integer
// x, and adds x to every element in that range. The cost of the operation is x
// itself (the amount added), no matter how long the range is. Return the smallest
// total cost of any sequence of operations that leaves the array non-decreasing,
// meaning nums[i] <= nums[i + 1] for every i.
// Input: int[] nums (positions are 0-based). Output: long, the smallest total
// cost (0 if the array is already non-decreasing). The answer can be larger than
// the int range.
// Examples:
//   [3,3,2,1] gives 2 (add 1 to positions 2..3 to get [3,3,3,2], then add 1 to
//   position 3 to get [3,3,3,3]).
//   [5,1,2,3] gives 4 (add 4 to positions 1..3 to get [5,5,6,7]).
// Constraints: 1 <= nums.length <= 100000, 1 <= nums[i] <= 1000000000
// Required complexity: O(n) time
// Run: javac --release 8 MinimumOperationsToMakeArrayNonDecreasing.java && java MinimumOperationsToMakeArrayNonDecreasing

import java.util.*;

class Solution {
    public long minOperations(int[] nums) {
        // TODO: implement
        return 0L;
    }
}

public class MinimumOperationsToMakeArrayNonDecreasing {

    private static void check(int caseNum, int[] nums, long expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            long got = new Solution().minOperations(nums);
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

    // 6 elements alternating 1000000000 and 1: three drops of 999999999.
    private static int[] sixSwings() {
        return new int[]{1000000000, 1, 1000000000, 1, 1000000000, 1};
    }

    // 100000 elements alternating 1000000000 and 1: 50000 drops of 999999999.
    private static int[] longSwings() {
        int[] a = new int[100000];
        for (int i = 0; i < a.length; i++) {
            a[i] = (i % 2 == 0) ? 1000000000 : 1;
        }
        return a;
    }

    // 100000 elements in 30 flat steps; each step is 30000000 lower than the
    // one before it (29 drops in total).
    private static int[] longStaircase() {
        int[] a = new int[100000];
        for (int i = 0; i < a.length; i++) {
            a[i] = 1000000000 - 30000000 * (i / 3334);
        }
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{3, 3, 2, 1}, 2L, fail, total);
        check(2, new int[]{5, 1, 2, 3}, 4L, fail, total);
        check(3, new int[]{1}, 0L, fail, total);
        check(4, new int[]{1, 2, 3, 4, 5}, 0L, fail, total);
        check(5, new int[]{2, 2, 2}, 0L, fail, total);
        check(6, new int[]{1000000000, 1}, 999999999L, fail, total);
        check(7, new int[]{5, 4, 3, 2, 1}, 4L, fail, total);
        check(8, new int[]{1, 5, 2, 6, 3, 7}, 6L, fail, total);
        check(9, new int[]{10, 3, 7, 1, 9, 2}, 20L, fail, total);
        check(10, sixSwings(), 2999999997L, fail, total);
        check(11, new int[]{4, 1, 4, 1, 4, 1}, 9L, fail, total);
        check(12, longSwings(), 49999999950000L, fail, total);
        check(13, longStaircase(), 870000000L, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
