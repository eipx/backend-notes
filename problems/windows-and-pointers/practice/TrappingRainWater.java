// Trapping Rain Water
// ref: LC 42
// Given an array height describing an elevation map, compute how much water
// is trapped above each position after it rains, bounded by the shorter of
// the tallest walls to its left and right, and sum the total volume.
// Required complexity: O(n) time, O(1) space.
// Study page: ../trapping-rain-water.md
// Run: javac --release 8 TrappingRainWater.java && java TrappingRainWater

import java.util.*;

class Solution {
    public int trap(int[] height) {
        // TODO: implement
        return 0;
    }
}

public class TrappingRainWater {
    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{0,1,0,2,1,0,1,3,2,1,2,1}, 6},
            {new int[]{4,2,0,3,2,5}, 9},
            {new int[]{}, 0},
            {new int[]{5}, 0},
            {new int[]{5,5}, 0},
            {new int[]{1,2,3,4,5}, 0},
            {new int[]{5,4,3,2,1}, 0},
            {new int[]{2,0,2}, 2},
            {new int[]{3,0,3,0,3}, 6},
            {new int[]{4,4,4,4}, 0}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] height = (int[]) cases[i][0];
            int expected = (Integer) cases[i][1];
            try {
                int got = new Solution().trap(height);
                if (Integer.compare(expected, got) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + got);
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
