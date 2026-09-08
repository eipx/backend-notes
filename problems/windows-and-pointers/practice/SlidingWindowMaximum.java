// Sliding Window Maximum
// ref: LC 239
// Given an integer array nums and a window size k, slide a window of exactly
// k consecutive elements across the array and report the maximum value inside
// the window at every position it stops at.
// Required complexity: O(n) time, O(k) space for the deque plus O(n-k+1) for output.
// Study page: ../sliding-window-maximum.md
// Run: javac --release 8 SlidingWindowMaximum.java && java SlidingWindowMaximum

import java.util.*;

class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        // TODO: implement
        return new int[0];
    }
}

public class SlidingWindowMaximum {
    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{1,3,-1,-3,5,3,6,7}, 3, new int[]{3,3,5,5,6,7}},
            {new int[]{1}, 1, new int[]{1}},
            {new int[]{1,-1}, 1, new int[]{1,-1}},
            {new int[]{9,11}, 2, new int[]{11}},
            {new int[]{4,-2}, 2, new int[]{4}},
            {new int[]{5,5,5,5}, 2, new int[]{5,5,5}},
            {new int[]{5,4,3,2,1}, 2, new int[]{5,4,3,2}},
            {new int[]{1,2,3,4,5}, 2, new int[]{2,3,4,5}},
            {new int[]{3,1,2}, 3, new int[]{3}},
            {new int[]{-7,-8,-3,-9,-1}, 3, new int[]{-3,-3,-1}}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            int k = (Integer) cases[i][1];
            int[] expected = (int[]) cases[i][2];
            try {
                int[] got = new Solution().maxSlidingWindow(nums, k);
                boolean ok = Arrays.equals(expected, got);
                if (ok) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected) + " got " + Arrays.toString(got));
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected) + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
