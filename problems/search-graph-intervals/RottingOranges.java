import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class RottingOranges {

    public static int solve(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Deque<int[]> queue = new ArrayDeque<int[]>(); // ArrayDeque rejects null, so store coordinates as int[2]
        int fresh = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) {
                    queue.add(new int[]{r, c}); // seed ALL rotten oranges at once: multi-source BFS
                } else if (grid[r][c] == 1) {
                    fresh++;
                }
            }
        }
        if (fresh == 0) {
            return 0; // nothing to rot, no minutes needed
        }
        int minutes = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty() && fresh > 0) {
            int levelSize = queue.size(); // snapshot: everything currently queued belongs to THIS minute's level
            for (int i = 0; i < levelSize; i++) {
                int[] cur = queue.poll();
                for (int[] d : dirs) {
                    int nr = cur[0] + d[0], nc = cur[1] + d[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2; // mark rotten immediately to avoid re-queueing the same cell
                        fresh--;
                        queue.add(new int[]{nr, nc});
                    }
                }
            }
            minutes++; // one full level processed == one minute elapsed
        }
        return fresh == 0 ? minutes : -1; // leftover fresh oranges means some were unreachable
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
