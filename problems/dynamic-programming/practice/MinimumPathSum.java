// Minimum Path Sum
// ref: LC 64
// You are given a grid of non-negative integers with m rows and n columns. A
// path starts at the top-left cell and ends at the bottom-right cell, and every
// step moves one cell right or one cell down. The cost of a path is the sum of
// the numbers on all the cells it visits, including the first and the last.
// Return the smallest cost of any path.
// 1 <= m, n <= 200, 0 <= grid[i][j] <= 200.
// Required: O(m * n) time.
// Study page: ../minimum-path-sum.md
// Run: javac --release 8 MinimumPathSum.java && java MinimumPathSum

import java.util.*;

class Solution {
    public int minPathSum(int[][] grid) {
        // TODO: implement
        return 0;
    }
}

public class MinimumPathSum {

    private static void check(int caseNum, int[][] grid, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().minPathSum(grid);
            if (got == expected) {
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

        check(1, new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}}, 7, fail, total);
        check(2, new int[][]{{1, 2, 3}, {4, 5, 6}}, 12, fail, total);
        check(3, new int[][]{{5}}, 5, fail, total);
        check(4, new int[][]{{0}}, 0, fail, total);
        check(5, new int[][]{{1, 2, 3, 4}}, 10, fail, total);
        check(6, new int[][]{{1}, {2}, {3}, {4}}, 10, fail, total);
        check(7, new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, 0, fail, total);
        check(8, new int[][]{{1, 2}, {1, 1}}, 3, fail, total);
        check(9, new int[][]{{1, 2, 100}, {3, 90, 100}, {4, 4, 1}}, 13, fail, total);
        check(10, new int[][]{{1, 1, 1}, {9, 9, 1}, {9, 9, 1}}, 5, fail, total);
        check(11, new int[][]{{1, 9}, {1, 9}, {1, 9}, {1, 1}}, 5, fail, total);
        check(12, new int[][]{{200, 200}, {200, 200}}, 600, fail, total);
        check(13, new int[][]{{1, 1, 9}, {9, 1, 9}, {9, 1, 1}}, 5, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
