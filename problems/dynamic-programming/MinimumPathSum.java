public class MinimumPathSum {

    // Smallest sum of the cells on a path from the top-left cell to the
    // bottom-right cell, moving only right or down. Both end cells count.
    //
    // dp[j] holds the cheapest cost of reaching column j of the row being
    // processed. Before cell (i, j) is overwritten, dp[j] still holds the cost
    // for the cell above it (row i - 1), and dp[j - 1] has already been
    // overwritten with the cost for the cell to its left (row i). The input
    // grid is not modified.
    public static int solve(int[][] grid) {
        if (grid.length == 0 || grid[0].length == 0) {
            return 0;
        }
        int m = grid.length;
        int n = grid[0].length;
        int[] dp = new int[n];

        // First row: each cell can only be reached from its left neighbor.
        dp[0] = grid[0][0];
        for (int j = 1; j < n; j++) {
            dp[j] = dp[j - 1] + grid[0][j];
        }

        for (int i = 1; i < m; i++) {
            // First column: each cell can only be reached from above, and
            // dp[0] still holds the cost of the cell above.
            dp[0] += grid[i][0];
            for (int j = 1; j < n; j++) {
                dp[j] = grid[i][j] + Math.min(dp[j], dp[j - 1]);
            }
        }
        return dp[n - 1];
    }

    private static void check(int caseNum, int[][] grid, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(grid);
        if (got == expected) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
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
