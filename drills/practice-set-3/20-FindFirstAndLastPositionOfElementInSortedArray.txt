// Find First and Last Position of Element in Sorted Array
// ref: LC 34
// Given an integer array nums sorted in non-decreasing order (duplicates allowed)
// and an integer target, return the index of the first occurrence and the index of
// the last occurrence of target as {first, last}. If target does not occur in nums,
// return {-1, -1}.
// Input: int[] nums, int target. Output: an int[] of length 2.
// Constraints: 0 <= nums.length <= 100000;
// -1000000000 <= nums[i], target <= 1000000000.
// Example: nums = [5,7,7,8,8,10], target = 8 returns {3,4}.
// Example: nums = [5,7,7,8,8,10], target = 6 returns {-1,-1}.
// Required complexity: O(log n) time, O(1) extra space
// Run: javac --release 8 FindFirstAndLastPositionOfElementInSortedArray.java && java FindFirstAndLastPositionOfElementInSortedArray

import java.util.*;

class Solution {
    public int[] searchRange(int[] nums, int target) {
        // TODO: implement
        return new int[0];
    }
}

public class FindFirstAndLastPositionOfElementInSortedArray {

    // Builds an array of count copies of value.
    private static int[] filled(int count, int value) {
        int[] a = new int[count];
        Arrays.fill(a, value);
        return a;
    }

    // Builds 0,0,...,1,1,...,2,2,... where every value appears times times.
    private static int[] repeatEach(int count, int times) {
        int[] a = new int[count];
        for (int i = 0; i < count; i++) {
            a[i] = i / times;
        }
        return a;
    }

    private static String brief(int[] a) {
        if (a.length <= 12) {
            return Arrays.toString(a);
        }
        return Arrays.toString(Arrays.copyOf(a, 12)) + "...(length " + a.length + ")";
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[]{5,7,7,8,8,10}, 8, new int[]{3,4} },
            { new int[]{5,7,7,8,8,10}, 6, new int[]{-1,-1} },
            { new int[]{}, 0, new int[]{-1,-1} },
            { new int[]{1}, 1, new int[]{0,0} },
            { new int[]{1}, 2, new int[]{-1,-1} },
            { new int[]{7,7,7,7,7}, 7, new int[]{0,4} },
            { new int[]{3,3,5,7,9}, 3, new int[]{0,1} },
            { new int[]{2,4,6,6,6}, 6, new int[]{2,4} },
            { new int[]{-9,-5,-5,-5,0,4}, -5, new int[]{1,3} },
            { new int[]{1,1,3,3}, 2, new int[]{-1,-1} },
            { new int[]{4,5,6}, 3, new int[]{-1,-1} },
            { filled(100000, 1000000000), 1000000000, new int[]{0,99999} },
            { repeatEach(100000, 4), 12345, new int[]{49380,49383} },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            int target = (Integer) cases[i][1];
            int[] expected = (int[]) cases[i][2];
            try {
                int[] actual = new Solution().searchRange(nums, target);
                if (Arrays.equals(actual, expected)) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected)
                            + " got " + Arrays.toString(actual)
                            + " (nums=" + brief(nums) + ", target=" + target + ")");
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected)
                        + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
