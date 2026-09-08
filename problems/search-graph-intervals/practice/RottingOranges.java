// Rotting Oranges
// ref: LC 994
// Given a grid of 0 (empty), 1 (fresh orange), and 2 (rotten orange) cells, where
// every minute each rotten orange rots all orthogonally adjacent fresh oranges
// simultaneously, find the minimum minutes until no fresh orange remains, or -1
// if some fresh orange can never be reached.
// Required complexity: O(rows * cols) time, O(rows * cols) space
// Study page: ../rotting-oranges.md
// Run: javac --release 8 RottingOranges.java && java RottingOranges

import java.util.*;

class Solution {
    public int orangesRotting(int[][] grid) {
        // TODO: implement
        return 0;
    }
}

public class RottingOranges {
    private static int[][] deepCopy(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int r = 0; r < grid.length; r++) {
            copy[r] = grid[r].clone();
        }
        return copy;
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[][]{{2,1,1},{1,1,0},{0,1,1}}, 4 },
            { new int[][]{{2,1,1},{0,1,1},{1,0,1}}, -1 },
            { new int[][]{{0,2}}, 0 },
            { new int[][]{{0}}, 0 },
            { new int[][]{{1}}, -1 },
            { new int[][]{{2}}, 0 },
            { new int[][]{{2,2,2},{1,1,1},{0,1,1}}, 2 },
            { new int[][]{{1,1},{1,1}}, -1 },
            { new int[][]{{2,1},{1,1}}, 2 },
            { new int[][]{{2,0,0},{0,0,0},{0,0,1}}, -1 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[][] grid = (int[][]) cases[i][0];
            int expected = (Integer) cases[i][1];
            try {
                // finished solution mutates the grid in place, so hand the practice
                // implementation a fresh copy each run to keep cases independent.
                int[][] input = deepCopy(grid);
                int actual = new Solution().orangesRotting(input);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (grid=" + Arrays.deepToString(grid) + ")");
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
