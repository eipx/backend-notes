// Merge Intervals
// ref: LC 56
// Given an array of [start, end] intervals not guaranteed to be sorted or
// non-overlapping, merge every pair of overlapping or touching intervals and
// return the resulting non-overlapping intervals sorted by start.
// Required complexity: O(n log n) time, O(n) space
// Study page: ../merge-intervals.md
// Run: javac --release 8 MergeIntervals.java && java MergeIntervals

import java.util.*;

class Solution {
    public int[][] merge(int[][] intervals) {
        // TODO: implement
        return new int[0][];
    }
}

public class MergeIntervals {
    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[][]{{1,3},{2,6},{8,10},{15,18}}, new int[][]{{1,6},{8,10},{15,18}} },
            { new int[][]{{1,4},{4,5}}, new int[][]{{1,5}} },
            { new int[][]{}, new int[][]{} },
            { new int[][]{{1,4}}, new int[][]{{1,4}} },
            { new int[][]{{1,4},{0,4}}, new int[][]{{0,4}} },
            { new int[][]{{1,4},{2,3}}, new int[][]{{1,4}} },
            { new int[][]{{1,4},{5,6}}, new int[][]{{1,4},{5,6}} },
            { new int[][]{{1,10},{2,3},{4,5},{6,7}}, new int[][]{{1,10}} },
            { new int[][]{{1,4},{1,4}}, new int[][]{{1,4}} },
            { new int[][]{{1,4},{0,2},{3,5}}, new int[][]{{0,5}} },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[][] input = (int[][]) cases[i][0];
            int[][] expected = (int[][]) cases[i][1];
            try {
                int[][] actual = new Solution().merge(input);
                if (Arrays.deepEquals(actual, expected)) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.deepToString(expected)
                            + " got " + Arrays.deepToString(actual));
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.deepToString(expected)
                        + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
