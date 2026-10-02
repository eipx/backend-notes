// Search Insert Position
// ref: LC 35
// Given an array nums sorted in ascending order with all values distinct, and an
// integer target, return the index of target if it is present. If it is not
// present, return the index at which it would have to be inserted to keep the
// array sorted.
// Input: int[] nums, int target. Output: an int in the range 0..nums.length.
// Constraints: 1 <= nums.length <= 10000; -10000 <= nums[i], target <= 10000;
// nums is strictly increasing.
// Example: nums = [1,3,5,6], target = 5 returns 2.
// Example: nums = [1,3,5,6], target = 2 returns 1 (it would go between 1 and 3).
// Required complexity: O(log n) time, O(1) extra space
// Run: javac --release 8 SearchInsertPosition.java && java SearchInsertPosition

import java.util.*;

class Solution {
    public int searchInsert(int[] nums, int target) {
        // TODO: implement
        return 0;
    }
}

public class SearchInsertPosition {

    // Builds the values start, start + step, start + 2 * step, ... (count values).
    private static int[] range(int start, int count, int step) {
        int[] a = new int[count];
        for (int i = 0; i < count; i++) {
            a[i] = start + i * step;
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
            { new int[]{1,3,5,6}, 5, 2 },
            { new int[]{1,3,5,6}, 2, 1 },
            { new int[]{1,3,5,6}, 7, 4 },
            { new int[]{1,3,5,6}, 0, 0 },
            { new int[]{1}, 1, 0 },
            { new int[]{1}, 0, 0 },
            { new int[]{1}, 2, 1 },
            { new int[]{1,3}, 2, 1 },
            { new int[]{-10,-5,0,5,10}, -7, 1 },
            { range(-10000, 10000, 2), 9999, 10000 },
            { range(-10000, 10000, 2), 1, 5001 },
            { range(-10000, 10000, 2), 4000, 7000 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            int target = (Integer) cases[i][1];
            int expected = (Integer) cases[i][2];
            try {
                int actual = new Solution().searchInsert(nums, target);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (nums=" + brief(nums) + ", target=" + target + ")");
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
