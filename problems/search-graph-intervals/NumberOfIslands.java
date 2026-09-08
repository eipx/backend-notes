import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class NumberOfIslands {

    public static int solve(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }
        int rows = grid.length, cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int islands = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '1' && !visited[r][c]) {
                    islands++; // first time seeing this island in raster order
                    floodFill(grid, visited, r, c, rows, cols);
                }
            }
        }
        return islands;
    }

    private static void floodFill(char[][] grid, boolean[][] visited, int startR, int startC, int rows, int cols) {
        Deque<int[]> queue = new ArrayDeque<int[]>(); // ArrayDeque rejects null, fine here since we only add int[]
        queue.add(new int[]{startR, startC});
        visited[startR][startC] = true;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            for (int[] d : dirs) {
                int nr = cur[0] + d[0], nc = cur[1] + d[1];
                // bounds check MUST use >= 0, not > 0, or row 0 / column 0 land gets skipped
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc] && grid[nr][nc] == '1') {
                    visited[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
        }
    }

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
            int actual = solve(grid);
            if (Integer.compare(actual, expected) == 0) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                        + " (grid=" + Arrays.deepToString(grid) + ")");
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
