// Number of Islands
// ref: LC 200
// Given a 2-D grid of '1' (land) and '0' (water) cells, count the number of
// islands, where an island is a maximal group of land cells connected
// orthogonally (not diagonally).
// Required complexity: O(rows * cols) time, O(rows * cols) space
// Study page: ../number-of-islands.md
// Run: javac --release 8 NumberOfIslands.java && java NumberOfIslands

import java.util.*;

class Solution {
    public int numIslands(char[][] grid) {
        // TODO: implement
        return 0;
    }
}

public class NumberOfIslands {
    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new char[][]{{'1','1','1','1','0'},{'1','1','0','1','0'},{'1','1','0','0','0'},{'0','0','0','0','0'}}, 1 },
            { new char[][]{{'1','1','0','0','0'},{'1','1','0','0','0'},{'0','0','1','0','0'},{'0','0','0','1','1'}}, 3 },
            { new char[][]{{'1'}}, 1 },
            { new char[][]{{'0'}}, 0 },
            { new char[][]{{'1','0'},{'0','0'}}, 1 },
            { new char[][]{{'0','0'},{'0','1'}}, 1 },
            { new char[][]{{'1','1','1'},{'1','1','1'},{'1','1','1'}}, 1 },
            { new char[][]{{'0','0','0'},{'0','0','0'},{'0','0','0'}}, 0 },
            { new char[][]{{'1','0'},{'0','1'}}, 2 },
            { new char[][]{{'1','1','1'}}, 1 },
            { new char[][]{{'1'},{'1'},{'1'}}, 1 },
            { new char[][]{{'1','1'},{'1','0'}}, 1 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            char[][] grid = (char[][]) cases[i][0];
            int expected = (Integer) cases[i][1];
            try {
                int actual = new Solution().numIslands(grid);
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
