import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class MaxAreaOfIsland {

    public static int solve(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int maxArea = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    int area = 0;
                    Deque<int[]> queue = new ArrayDeque<int[]>();
                    queue.add(new int[]{r, c});
                    grid[r][c] = 0;
                    while (!queue.isEmpty()) {
                        int[] cur = queue.poll();
                        area++;
                        for (int[] d : dirs) {
                            int nr = cur[0] + d[0], nc = cur[1] + d[1];
                            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                                grid[nr][nc] = 0;
                                queue.add(new int[]{nr, nc});
                            }
                        }
                    }
                    maxArea = Math.max(maxArea, area);
                }
            }
        }
        return maxArea;
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
                int actual = solve(grid);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS");
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
