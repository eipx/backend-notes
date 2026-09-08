// Container With Most Water
// ref: LC 11
// Given an array `height` where height[i] is the height of a vertical line at
// position i, pick two lines that together with the x-axis form a container.
// Return the maximum amount of water that container can hold.
// Required complexity: O(n) time, O(1) space.
// Study page: ../container-with-most-water.md
// Run: javac --release 8 ContainerWithMostWater.java && java ContainerWithMostWater

import java.util.*;

class Solution {
    public int maxArea(int[] height) {
        // TODO: implement
        return 0;
    }
}

public class ContainerWithMostWater {
    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{1,8,6,2,5,4,8,3,7}, 49},
            {new int[]{1,1}, 1},
            {new int[]{4,3,2,1,4}, 16},
            {new int[]{1,2,1}, 2},
            {new int[]{2,2,2,2}, 6},
            {new int[]{1,2,4,3}, 4},
            {new int[]{0,2}, 0},
            {new int[]{5,4,3,2,1}, 6},
            {new int[]{1,2,3,4,5}, 6},
            {new int[]{10000,10000}, 10000}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] height = (int[]) cases[i][0];
            int expected = (Integer) cases[i][1];
            try {
                int got = new Solution().maxArea(height);
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
