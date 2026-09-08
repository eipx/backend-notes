// Number of Provinces
// ref: LC 547
// Given an n x n adjacency matrix of direct city connections, count how many
// maximal groups of transitively connected cities (provinces) exist.
// Required complexity: O(n^2 * alpha(n)) time, O(n) space
// Study page: ../number-of-provinces.md
// Run: javac --release 8 NumberOfProvinces.java && java NumberOfProvinces

import java.util.*;

class Solution {
    public int findCircleNum(int[][] isConnected) {
        // TODO: implement
        return 0;
    }
}

public class NumberOfProvinces {
    private static int[][] deepCopy(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int r = 0; r < grid.length; r++) {
            copy[r] = grid[r].clone();
        }
        return copy;
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[][]{{1,1,0},{1,1,0},{0,0,1}}, 2 },
            { new int[][]{{1,0,0},{0,1,0},{0,0,1}}, 3 },
            { new int[][]{{1,1,1},{1,1,1},{1,1,1}}, 1 },
            { new int[][]{{1}}, 1 },
            { new int[][]{{1,0},{0,1}}, 2 },
            { new int[][]{{1,1,0,0},{1,1,0,0},{0,0,1,1},{0,0,1,1}}, 2 },
            { new int[][]{{1,0,0,1},{0,1,1,0},{0,1,1,0},{1,0,0,1}}, 2 },
            { new int[][]{{1,1,0,0,0},{1,1,1,0,0},{0,1,1,0,0},{0,0,0,1,1},{0,0,0,1,1}}, 2 },
            { new int[][]{{1,0,0,0,0},{0,1,0,0,0},{0,0,1,0,0},{0,0,0,1,0},{0,0,0,0,1}}, 5 },
            { new int[][]{{1,1,1,1,1,1},{1,1,1,1,1,1},{1,1,1,1,1,1},{1,1,1,1,1,1},{1,1,1,1,1,1},{1,1,1,1,1,1}}, 1 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[][] isConnected = (int[][]) cases[i][0];
            int expected = (Integer) cases[i][1];
            try {
                int[][] input = deepCopy(isConnected);
                int actual = new Solution().findCircleNum(input);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual);
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
