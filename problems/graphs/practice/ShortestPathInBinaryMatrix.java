// Shortest Path in Binary Matrix
// ref: LC 1091
// Given an n x n grid of 0 (open) and 1 (blocked) cells, find the length (in
// cells) of the shortest 8-directionally connected path from the top-left to
// the bottom-right cell, or -1 if the start, the end, or every path is blocked.
// Required complexity: O(n^2) time, O(n^2) space
// Study page: ../shortest-path-in-binary-matrix.md
// Run: javac --release 8 ShortestPathInBinaryMatrix.java && java ShortestPathInBinaryMatrix

import java.util.*;

class Solution {
    public int shortestPathBinaryMatrix(int[][] grid) {
        // TODO: implement
        return 0;
    }
}

public class ShortestPathInBinaryMatrix {
    private static int[][] deepCopy(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int r = 0; r < grid.length; r++) {
            copy[r] = grid[r].clone();
        }
        return copy;
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[][]{{0,1},{1,0}}, 2 },
            { new int[][]{{0,0,0},{1,1,0},{1,1,0}}, 4 },
            { new int[][]{{1,0,0},{1,1,0},{1,1,0}}, -1 },
            { new int[][]{{0,0,0},{1,1,0},{1,1,1}}, -1 },
            { new int[][]{{0}}, 1 },
            { new int[][]{{1}}, -1 },
            { new int[][]{{0,0},{0,0}}, 2 },
            { new int[][]{{0,1,1},{1,1,1},{1,1,0}}, -1 },
            { new int[][]{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}}, 4 },
            { new int[][]{{0,0,1},{1,0,0},{1,0,0}}, 3 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[][] grid = (int[][]) cases[i][0];
            int expected = (Integer) cases[i][1];
            try {
                int[][] input = deepCopy(grid);
                int actual = new Solution().shortestPathBinaryMatrix(input);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (grid=" + Arrays.deepToString(grid) + ")");
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
