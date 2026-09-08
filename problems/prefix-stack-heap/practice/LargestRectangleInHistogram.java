// Largest Rectangle in Histogram
// ref: LC 84
// Given non-negative bar heights forming a histogram (each bar width 1, no
// gaps), find the area of the largest axis-aligned rectangle that fits
// entirely inside the outline formed by the bars.
// Required: O(n) time, O(n) space.
// Study page: ../largest-rectangle-in-histogram.md
// Run: javac --release 8 LargestRectangleInHistogram.java && java LargestRectangleInHistogram

import java.util.*;

class Solution {
    public int largestRectangleArea(int[] heights) {
        // TODO: implement
        return -1;
    }
}

public class LargestRectangleInHistogram {

    private static void check(int caseNum, int[] heights, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().largestRectangleArea(heights);
            if (Integer.compare(got, expected) == 0) {
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

        check(1, new int[]{4, 2, 6, 7, 3, 4}, 12, fail, total);
        check(2, new int[]{}, 0, fail, total);
        check(3, new int[]{5}, 5, fail, total);
        check(4, new int[]{1, 1, 1, 1}, 4, fail, total);
        check(5, new int[]{5, 4, 3, 2, 1}, 9, fail, total);
        check(6, new int[]{1, 2, 3, 4, 5}, 9, fail, total);
        check(7, new int[]{0, 0, 0}, 0, fail, total);
        check(8, new int[]{2, 2, 2}, 6, fail, total);
        check(9, new int[]{3, 6, 5, 7, 4, 8, 1, 0}, 20, fail, total);
        check(10, new int[]{5, 5, 0, 5, 5}, 10, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
