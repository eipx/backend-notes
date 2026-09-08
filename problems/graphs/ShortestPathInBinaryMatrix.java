import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class ShortestPathInBinaryMatrix {

    public static int solve(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) {
            return -1;
        }
        if (n == 1) {
            return 1;
        }
        int[][] dirs = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };
        Deque<int[]> queue = new ArrayDeque<int[]>();
        queue.add(new int[]{0, 0});
        grid[0][0] = 1;
        int length = 1;
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                int[] cur = queue.poll();
                if (cur[0] == n - 1 && cur[1] == n - 1) {
                    return length;
                }
                for (int[] d : dirs) {
                    int nr = cur[0] + d[0], nc = cur[1] + d[1];
                    if (nr >= 0 && nr < n && nc >= 0 && nc < n && grid[nr][nc] == 0) {
                        grid[nr][nc] = 1;
                        queue.add(new int[]{nr, nc});
                    }
                }
            }
            length++;
        }
        return -1;
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
