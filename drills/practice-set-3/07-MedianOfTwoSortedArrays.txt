// Median of Two Sorted Arrays
// ref: LC 4
// Given two integer arrays nums1 and nums2, each already sorted in ascending order,
// find the median of all of their values taken together, as if the two arrays were
// merged into one sorted sequence. For an odd total count the median is the middle
// value; for an even total count it is the average of the two middle values.
// Input: int[] nums1 (length m), int[] nums2 (length n). Output: a double.
// Constraints: 0 <= m, n <= 1000; m + n >= 1; every value is in -1000000..1000000.
// Example: nums1 = [1,3], nums2 = [2] returns 2.0 (merged: 1,2,3).
// Example: nums1 = [1,2], nums2 = [3,4] returns 2.5 (merged: 1,2,3,4; the middle
// two values are 2 and 3).
// Required complexity: O(log(m + n)) time, O(1) extra space
// Run: javac --release 8 MedianOfTwoSortedArrays.java && java MedianOfTwoSortedArrays

import java.util.*;

class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        // TODO: implement
        return 0.0;
    }
}

public class MedianOfTwoSortedArrays {

    // Builds the values start, start + step, start + 2 * step, ... (count values).
    private static int[] range(int start, int count, int step) {
        int[] a = new int[count];
        for (int i = 0; i < count; i++) {
            a[i] = start + i * step;
        }
        return a;
    }

    private static boolean closeEnough(double actual, double expected) {
        return Math.abs(actual - expected) <= 1e-9;
    }

    private static String brief(int[] a) {
        if (a.length <= 12) {
            return Arrays.toString(a);
        }
        return Arrays.toString(Arrays.copyOf(a, 12)) + "...(length " + a.length + ")";
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[]{1,3}, new int[]{2}, 2.0 },
            { new int[]{1,2}, new int[]{3,4}, 2.5 },
            { new int[]{}, new int[]{1}, 1.0 },
            { new int[]{2}, new int[]{}, 2.0 },
            { new int[]{1,2,3}, new int[]{10,20,30}, 6.5 },
            { new int[]{10,20,30}, new int[]{1,2}, 10.0 },
            { new int[]{1,2,2}, new int[]{2,2,3}, 2.0 },
            { new int[]{-3,-1}, new int[]{-2,0}, -1.5 },
            { new int[]{999999}, new int[]{1000000}, 999999.5 },
            { new int[]{-1000000,-1000000}, new int[]{-999999}, -1000000.0 },
            { new int[]{1,2,3}, new int[]{4,5,6,7,8,9,10}, 5.5 },
            { new int[]{1,3,8,9,15}, new int[]{7,11,18,19,21,25}, 11.0 },
            { range(0, 1000, 2), range(1, 1000, 2), 999.5 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums1 = (int[]) cases[i][0];
            int[] nums2 = (int[]) cases[i][1];
            double expected = (Double) cases[i][2];
            try {
                double actual = new Solution().findMedianSortedArrays(nums1, nums2);
                if (closeEnough(actual, expected)) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (nums1=" + brief(nums1) + ", nums2=" + brief(nums2) + ")");
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception "
                        + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
