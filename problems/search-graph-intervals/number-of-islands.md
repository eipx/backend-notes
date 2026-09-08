# Number of Islands
`ref: LC 200` · Difficulty: Medium · Pattern: Grid DFS/BFS (connected components)

## Problem
A 2-D grid of characters represents a map, where each cell is either `'1'` (land) or `'0'` (water). An island is a maximal group of land cells connected orthogonally (up, down, left, right -- not diagonally); land cells that only touch each other diagonally belong to different islands. All four edges of the grid are considered surrounded by water beyond the grid boundary.

The output is the total count of islands in the grid. "Valid" counting means: every land cell belongs to exactly one island, two land cells belong to the same island if and only if there is a path between them moving only through orthogonally-adjacent land cells, and the count is the number of such maximal groups.

## Constraints
- `1 <= rows, cols <= 300`
- Each cell is exactly the character `'1'` or `'0'`
- With up to `300 * 300 = 90000` cells, any algorithm visiting each cell a constant number of times (`O(rows * cols)`) is fast; anything that revisits cells without a visited marker risks exponential blowup or infinite loops on cycles.

## Worked examples
1. `grid = [["1","1","1","1","0"],["1","1","0","1","0"],["1","1","0","0","0"],["0","0","0","0","0"]]` -> `1`. Every `'1'` cell is orthogonally reachable from every other `'1'` cell through the connected block in the top-left, so it is a single island even though its shape is irregular.
2. `grid = [["1","1","0","0","0"],["1","1","0","0","0"],["0","0","1","0","0"],["0","0","0","1","1"]]` -> `3`. There are three separate blocks of land that never touch orthogonally: the top-left 2x2 block, the single cell in the middle, and the bottom-right pair.
3. A single cell `["1"]` -> `1`, while a single cell `["0"]` -> `0`: with only one cell, there is nothing to connect to, so the count is simply whether that one cell is land.

## Edge cases checklist
- A `1x1` grid containing `'0'` or `'1'`.
- An island touching row `0` (the very top edge).
- An island touching column `0` (the very left edge).
- An island touching both row 0 and column 0 simultaneously (the corner) -- this is exactly where the classic `> 0` vs `>= 0` bounds-check bug shows up, since a bug that requires `r > 0` before recursing up would silently refuse to explore row 0 at all.
- A grid entirely land (answer `1`) or entirely water (answer `0`).
- Two land cells that touch only diagonally (they must count as 2 separate islands, not 1).
- A single row grid and a single column grid (only one dimension has more than one cell).

## Approach
### Brute force
For every land cell, do a fresh traversal from it that recounts the whole island it belongs to, without a global visited marker, and divide by the island's size at the end to avoid overcounting. This wastes enormous work re-exploring the same island once per member cell, becoming `O((rows * cols)^2)` in the worst case (e.g. one giant island covering the whole grid) -- with `90000` cells that is on the order of `10^10` operations, too slow, and also fragile because "divide by island size" is easy to get wrong at the boundaries.

### Optimal
Scan every cell once in raster order. Whenever an unvisited `'1'` is found, that must be the first cell discovered of a brand-new island (since raster order guarantees nothing already-visited could have reached it before now), so increment the island count and flood-fill (DFS or BFS) outward from it, marking every connected land cell as visited so the outer scan never starts a second flood-fill inside the same island.

**Key invariant:** each land cell is marked visited exactly once, and a new flood-fill is started only when the outer scan encounters a `'1'` that has not yet been visited -- so the number of times a flood-fill is *started* equals exactly the number of connected components (islands).

Proof sketch: flood-fill from a starting cell visits precisely the set of cells reachable from it via orthogonal land moves, which is by definition the entire island containing that cell (no more, no less, since it explores every unvisited land neighbor and stops at water or grid edges). Because the outer scan only triggers a new flood-fill on an unvisited `'1'`, and each flood-fill exhaustively visits its whole island before the scan resumes, no island can trigger more than one flood-fill and no island can be skipped, so the count of flood-fills started equals the count of islands exactly.

### Step-by-step trace
Trace on `grid = [["1","1","0"],["0","1","0"],["0","0","1"]]` (raster scan order, BFS flood-fill):

| scan position | cell value | visited? | action | island count after |
|---|---|---|---|---|
| (0,0) | '1' | no | new island; BFS visits (0,0),(0,1),(1,1) | 1 |
| (0,1) | '1' | yes (from BFS above) | skip | 1 |
| (0,2) | '0' | -- | skip (water) | 1 |
| (1,0) | '0' | -- | skip (water) | 1 |
| (1,1) | '1' | yes | skip | 1 |
| (1,2) | '0' | -- | skip (water) | 1 |
| (2,0) | '0' | -- | skip (water) | 1 |
| (2,1) | '0' | -- | skip (water) | 1 |
| (2,2) | '1' | no | new island; BFS visits (2,2) alone | 2 |

Final answer: `2` islands.

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.Deque;

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
```

## Complexity
- Time: `O(rows * cols)` -- every cell is enqueued and visited at most once across all flood-fills combined.
- Space: `O(rows * cols)` -- the `visited` array, plus up to `O(rows * cols)` queue entries in the worst case (one giant island).

## Java 8 pitfalls for this problem
- The bounds check must read `nr >= 0 && nr < rows && nc >= 0 && nc < cols`. Writing `nr > 0` instead of `nr >= 0` (a very easy typo, since `>` and `>=` differ by one character) silently excludes row 0 from ever being explored as a *neighbor*, which undercounts or miscounts islands that touch the top edge -- and the same applies to `nc > 0` for the left edge/column 0.
- Mutating `grid[r][c]` in place (e.g. setting visited land to `'0'`) instead of using a separate `boolean[][] visited` array is a common shortcut that saves memory, but it destroys the input; if the test harness or a later step needs the original grid, use the explicit `visited` array shown above instead.
- `ArrayDeque<int[]>` is preferred over `LinkedList<int[]>` for the BFS queue: both work, but `ArrayDeque` is generally faster and, notably, rejects `null` elements outright, which catches an accidental `queue.add(null)` immediately instead of causing a confusing `NullPointerException` later during `poll()`.
- Comparing characters with `grid[r][c] == '1'` is correct and cheap; do not accidentally compare with the string `"1"` (`==` on a boxed/interned `String` literal happens to often "work" by coincidence in Java due to string interning, but it is the wrong type and a `.equals` habit from string comparisons is unnecessary overhead here).
- Recursive DFS is a valid alternative to the BFS shown above, but with grids up to `300x300 = 90000` cells, a fully-connected single island can recurse 90000 levels deep, which risks a `StackOverflowError` in Java's default stack size; the iterative BFS/queue version shown here avoids that risk entirely.

## Wrong approaches and why they fail
- **Counting land cells and dividing by an assumed average island size:** there is no such thing as an "average" island size known in advance; this approach has no mathematical basis and gives wrong answers for almost any non-trivial input.
- **Treating diagonal neighbors as connected:** for `grid=[["1","0"],["0","1"]]`, treating the two diagonal `'1'` cells as one island gives `1`, but the correct orthogonal-adjacency answer is `2` -- diagonal connectivity is a different (and here, wrong) problem definition.
- **Bounds check using `nr > 0 && nr < rows` (off-by-one on the lower bound):** for `grid=[["1","0"],["0","0"]]`, the single land cell sits at `(0,0)`; a flood-fill that never even starts its bounds-checked neighbor exploration incorrectly for cells at row 0 might still count the seed cell correctly by luck (since the outer scan itself starts at `(0,0)` regardless of the neighbor-check bug), but for a two-cell island spanning `(0,0)` and `(1,0)`, a buggy neighbor check that excludes moving *into* row 0 from row 1 would fail to connect them, splitting one island into two.

## Variants
- **LC 994 Rotting Oranges** shares the identical 4-directional bounds-check and BFS traversal skeleton in this same folder, but propagates a timed wave outward from multiple sources instead of counting static connected components.
- **Return the size of the largest island (LC 695 Max Area of Island):** keep the same flood-fill skeleton, but have each flood-fill return the count of cells it visited, and track the maximum across all flood-fills instead of just incrementing a counter.
- **Count islands in a grid that can be modified by flipping one water cell to land (LC 305 Number of Islands II, or an online/dynamic variant):** this changes the shape of the problem entirely -- repeated flood-fills after each update would be too slow, and the standard approach switches to a Union-Find (disjoint set) data structure that merges components incrementally as land cells are added.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | [["1","1","1","1","0"],["1","1","0","1","0"],["1","1","0","0","0"],["0","0","0","0","0"]] | 1 | one large irregular island |
| 2 | [["1","1","0","0","0"],["1","1","0","0","0"],["0","0","1","0","0"],["0","0","0","1","1"]] | 3 | three disconnected islands |
| 3 | [["1"]] | 1 | 1x1 grid, single land cell |
| 4 | [["0"]] | 0 | 1x1 grid, single water cell |
| 5 | [["1","0"],["0","0"]] | 1 | island touching row 0 and column 0 (top-left corner) |
| 6 | [["0","0"],["0","1"]] | 1 | island touching the bottom-right corner only |
| 7 | [["1","1","1"],["1","1","1"],["1","1","1"]] | 1 | entirely land |
| 8 | [["0","0","0"],["0","0","0"],["0","0","0"]] | 0 | entirely water |
| 9 | [["1","0"],["0","1"]] | 2 | diagonal-only adjacency must NOT count as connected |
| 10 | [["1","1","1"]] | 1 | single row grid, land spans the whole top edge |
| 11 | [["1"],["1"],["1"]] | 1 | single column grid, land spans the whole left edge |
| 12 | [["1","1"],["1","0"]] | 1 | L-shaped island touching both row 0 and column 0 |
