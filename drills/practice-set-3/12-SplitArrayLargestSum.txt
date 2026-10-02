// Split Array Largest Sum
// ref: LC 410
// Given an array nums of non-negative integers and an integer k, cut nums into
// exactly k non-empty contiguous parts (each part is a run of adjacent elements and
// the order of the elements is unchanged). The cost of a cut is the largest sum
// among its k parts. Return the smallest cost over all ways to cut.
// Input: int[] nums, int k. Output: an int.
// Constraints: 1 <= nums.length <= 1000; 0 <= nums[i] <= 1000000;
// 1 <= k <= min(50, nums.length).
// Example: nums = [7,2,5,10,8], k = 2 returns 18 (parts [7,2,5] and [10,8] have
// sums 14 and 18, and no other cut has a smaller largest sum).
// Example: nums = [1,2,3], k = 3 returns 3 (every element is its own part).
// Required complexity: O(n log(sum(nums))) time, O(1) extra space
// Run: javac --release 8 SplitArrayLargestSum.java && java SplitArrayLargestSum

import java.util.*;

class Solution {
    public int splitArray(int[] nums, int k) {
        // TODO: implement
        return 0;
    }
}

public class SplitArrayLargestSum {

    // Builds an array of count copies of value.
    private static int[] filled(int count, int value) {
        int[] a = new int[count];
        Arrays.fill(a, value);
        return a;
    }

    // Builds a repeatable pseudo-random array with values in 0..maxValue.
    private static int[] pseudoRandom(int count, int maxValue, long seed) {
        int[] a = new int[count];
        long x = seed;
        for (int i = 0; i < count; i++) {
            x = (x * 1103515245L + 12345L) & 0x7fffffffL;
            a[i] = (int) ((x >> 8) % (maxValue + 1L));
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
            { new int[]{7,2,5,10,8}, 2, 18 },
            { new int[]{1,2,3,4,5}, 2, 9 },
            { new int[]{5}, 1, 5 },
            { new int[]{3,1,4,1,5}, 5, 5 },
            { new int[]{2,3,1,2,4,3}, 1, 15 },
            { new int[]{0,0,0,0}, 2, 0 },
            { new int[]{0,5,0,0,7,0}, 3, 7 },
            { new int[]{1,1,1000000,1,1}, 3, 1000000 },
            { new int[]{1,4,1,1,1}, 2, 5 },
            { new int[]{1,2,3,4,5,6,7,8,9}, 3, 17 },
            { filled(1000, 1000000), 1, 1000000000 },
            { filled(1000, 1000000), 50, 20000000 },
            { pseudoRandom(1000, 1000000, 12345L), 50, 9790029 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            int k = (Integer) cases[i][1];
            int expected = (Integer) cases[i][2];
            try {
                int actual = new Solution().splitArray(nums, k);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (nums=" + brief(nums) + ", k=" + k + ")");
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
