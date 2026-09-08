// Max Area of Island
// ref: LC 695
// Given a grid of 0 (water) and 1 (land) cells where land cells connect only
// through shared edges (not diagonals), find the area of the single largest
// connected island, or 0 if there is no land at all.
// Required complexity: O(rows * cols) time, O(rows * cols) space
// Study page: ../max-area-of-island.md
// Run: javac --release 8 MaxAreaOfIsland.java && java MaxAreaOfIsland

import java.util.*;

class Solution {
    public int maxAreaOfIsland(int[][] grid) {
        // TODO: implement
        return 0;
    }
}

public class MaxAreaOfIsland {
    private static int[][] deepCopy(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int r = 0; r < grid.length; r++) {
            copy[r] = grid[r].clone();
        }
        return copy;
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[][]{{0,0,0},{0,0,0}}, 0 },
            { new int[][]{{1,1},{1,1}}, 4 },
            { new int[][]{{0,0,0},{0,1,0},{0,0,0}}, 1 },
            { new int[][]{{1,1,0},{0,1,0},{0,0,1}}, 3 },
            { new int[][]{{1,0,0,0,0},{1,1,0,0,0},{0,1,0,0,1},{0,0,0,1,1}}, 4 },
            { new int[][]{{1,1,1,1,1}}, 5 },
            { new int[][]{{1},{1},{1}}, 3 },
            { new int[][]{{1}}, 1 },
            { new int[][]{{0}}, 0 },
            { new int[][]{{1,0},{0,1}}, 1 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[][] grid = (int[][]) cases[i][0];
            int expected = (Integer) cases[i][1];
            try {
                int[][] input = deepCopy(grid);
                int actual = new Solution().maxAreaOfIsland(input);
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
