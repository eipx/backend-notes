# Max Area of Island
`ref: LC 695` · Difficulty: Medium · Pattern: Grid flood fill with in-place marking

## Problem
A grid of `0`s and `1`s represents water and land. A "land mass" (island) is a group of `1` cells connected edge-to-edge (up, down, left, right -- not diagonally); water cells and the grid border both stop a land mass from extending further. The area of an island is simply the number of `1` cells it contains. Scan the whole grid and report the area of the single largest island. If the grid contains no land at all, the answer is `0`.

## Constraints
- `1 <= rows, cols <= 50` (small enough that an `O(rows * cols)` flood fill comfortably fits any reasonable time budget)
- Each cell is exactly `0` or `1`.
- A cell only counts as connected to another through a shared edge, never a shared corner -- two diagonally-touching `1` cells belong to different islands unless some other edge-connected path links them.

## Worked examples
1. `grid = [[0,0,0],[0,1,0],[0,0,0]]` -> `1`. A single isolated land cell surrounded by water on all four sides.
2. `grid = [[1,1,0],[0,1,0],[0,0,1]]` -> `3`. The top-left three `1`s form one connected island of area 3 (they chain together through shared edges), while the bottom-right `1` is a second, separate island of area 1 -- the largest of the two is 3.
3. `grid = [[1,0,0,0,0],[1,1,0,0,0],[0,1,0,0,1],[0,0,0,1,1]]` -> `4`. There are two islands here: an L-shaped one of area 4 in the top-left, and an L-shaped one of area 3 in the bottom-right (they are not connected to each other because no shared-edge path links them) -- the answer takes the larger of the two.
4. `grid = [[1,0],[0,1]]` -> `1`. These two land cells only touch diagonally, so despite looking adjacent, they form two separate size-1 islands, not one size-2 island.

## Edge cases checklist
- Grid entirely water (`0`s only) -- answer must be `0`, no island to measure.
- Grid entirely land (`1`s only) -- the whole grid is one island; answer equals `rows * cols`.
- A `1x1` grid holding either a `0` or a `1`.
- A single row or a single column grid (degenerate shape, still needs correct 4-directional adjacency).
- Two islands of exactly equal area (confirms the code takes the max correctly and doesn't just overwrite with whichever is found last if that happens to be smaller).
- Land cells that touch only diagonally (must NOT be merged into the same island).
- An island that snakes through most of the grid in a single connected chain (stresses the flood fill's queue/stack size, not just its correctness).

## Approach
### Brute force
For every land cell, run a completely separate flood fill without marking cells as visited, to compute the area of "the island touching this cell." This recomputes the same island's area once per cell that belongs to it, giving `O((rows*cols)^2)` in the worst case (e.g. an entire grid of `1`s: every one of `rows*cols` cells triggers an `O(rows*cols)` flood fill). It gets the right numeric answer but re-does an enormous amount of duplicate work, because it never remembers that a cell has already been counted as part of some island.

### Optimal
Scan every cell once. Whenever an unvisited `1` cell is found, flood fill outward (BFS with a queue, or DFS) to find every cell connected to it, counting them as one island's area, and mark each visited cell so it is never explored as an island seed again -- the cheapest way to do this without a separate `boolean[][] visited` array is to overwrite each visited land cell to `0` right when it is first enqueued/visited. Track the maximum area seen across all flood fills.

**Key invariant:** by the time the flood fill starting at some cell finishes, every cell reachable from it through 4-directional `1`-to-`1` edges has been marked `0` and counted exactly once, so the outer double loop can never re-enter the same island through a different starting cell -- each island is discovered and measured exactly once, at whichever of its cells the row-major scan reaches first.

Proof sketch: marking a cell `0` as soon as it is discovered (not just when it is fully processed) prevents the flood fill from ever re-queueing that cell, so each of the `rows*cols` cells is enqueued at most once across the entire run of the outer loop -- this bounds total work by the number of cells, not by cells-times-islands. Since connectivity is defined purely by shared edges and the flood fill explores exactly that relation, the set of cells marked during one flood fill call is precisely one full island, so the area counted is exactly correct, and the running maximum across all flood fills is the answer.

### Step-by-step trace
Trace on `grid = [[1,1,0],[0,1,0],[0,0,1]]` (rows `r=0..2`, cols `c=0..2`), scanning in row-major order:

| cell scanned | action | queue during flood fill | area found | running max |
|---|---|---|---|---|
| (0,0)=1 | start flood fill, mark (0,0)=0 | [(0,0)] -> pop, enqueue (0,1) | -- | -- |
| -- | continue flood fill | [(0,1)] -> pop, mark 0, enqueue (1,1) | -- | -- |
| -- | continue flood fill | [(1,1)] -> pop, mark 0, no new unvisited 1-neighbors | -- | -- |
| -- | queue empty, island done | -- | 3 | 3 |
| (0,1) | already 0 (visited), skip | -- | -- | 3 |
| (1,1) | already 0 (visited), skip | -- | -- | 3 |
| (2,2)=1 | start new flood fill, mark (2,2)=0 | [(2,2)] -> pop, no unvisited 1-neighbors | -- | -- |
| -- | queue empty, island done | -- | 1 | 3 (unchanged, 1 < 3) |

Final answer: `3`.

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.Deque;

public static int solve(int[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    int maxArea = 0;
    int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == 1) {
                int area = 0;
                Deque<int[]> queue = new ArrayDeque<int[]>(); // ArrayDeque rejects null, use int[] coords
                queue.add(new int[]{r, c});
                grid[r][c] = 0; // mark visited immediately, doubles as the visited set
                while (!queue.isEmpty()) {
                    int[] cur = queue.poll();
                    area++;
                    for (int[] d : dirs) {
                        int nr = cur[0] + d[0], nc = cur[1] + d[1];
                        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                            grid[nr][nc] = 0; // mark on enqueue, not on dequeue, to avoid double-queueing
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
```

## Complexity
- Time: `O(rows * cols)` -- every cell is examined by the outer loop once, and every land cell is enqueued and dequeued by some flood fill exactly once.
- Space: `O(rows * cols)` -- the queue can hold up to every cell in the worst case (a grid that is one giant island).

## Java 8 pitfalls for this problem
- The bounds check must read `nr >= 0 && nr < rows && nc >= 0 && nc < cols` -- swapping `>=` for `>` on the lower bound silently drops row 0 or column 0 from consideration, undercounting islands that touch the grid's top or left edge.
- `ArrayDeque` rejects `null` elements; storing coordinates as `int[]{r, c}` sidesteps that entirely and is also faster than boxing each coordinate pair into an `Integer[]` or a custom object.
- Marking `grid[nr][nc] = 0` at the moment a neighbor is discovered (on enqueue) rather than when it is popped (on dequeue) is what keeps each cell enqueued exactly once; marking only on dequeue lets the same cell be pushed onto the queue multiple times by different neighbors before it's ever processed, wasting work (though it does not break correctness by itself, since a re-check `grid[nr][nc]==1` before enqueuing would still block true duplicates -- but skipping that recheck without dequeue-time marking would double count).
- Mutating the input grid in place (turning visited land into water) means calling `solve` twice on the same array reference gives a wrong answer of `0` the second time -- fine for a single pass over test data, but a real API contract should either document this side effect or work on a defensive copy.
- Recursion depth: a recursive DFS version of this flood fill is fine at `50 * 50 = 2500` cells, but the same recursive shape applied to a `10^4`-node graph (as in a graph adjacency-list version of this pattern, not a bounded grid) risks `StackOverflowError` -- the iterative BFS/queue version shown here avoids that limit entirely and is the version worth defaulting to.
- Declare the queue as `Deque<int[]>` (the interface type), not `ArrayDeque<int[]>`, for the local variable -- matches idiomatic Java 8 style and keeps the choice of concrete queue implementation swappable.

## Wrong approaches and why they fail
- **Counting land cells globally without tracking which island they belong to:** simply summing all `1`s answers "how much total land is there," not "what is the size of the *largest single* island" -- for `grid=[[1,1,0],[0,1,0],[0,0,1]]` this wrongly returns `4` (all land cells) instead of the correct `3` (the largest connected group).
- **Treating diagonal neighbors as connected:** adding the four diagonal directions to the neighbor check merges islands that should stay separate -- for `grid=[[1,0],[0,1]]` this incorrectly reports `2` instead of the correct `1`.
- **Brute-force flood fill from every land cell without marking visited:** as described above, this still gets the right numeric answer, but wastes enormous repeated work and does not scale -- it's a correctness-vs-performance trap rather than an outright bug, but the pattern's whole point is the in-place-marking optimization.

## Variants
- **LC 200 Number of Islands** uses the exact same 4-directional grid flood fill and in-place-marking technique, but only needs to *count* how many separate islands exist, not measure and compare their areas -- the traversal skeleton (bounds check, mark-on-enqueue, `dirs` array) is identical between the two problems; only the bookkeeping inside the loop differs (increment a counter once per flood fill call vs. accumulate and compare an area).
- **Return the island's actual cell coordinates, not just its area:** collect each popped `int[]` into a list instead of (or in addition to) incrementing a counter.
- **Weighted land cells (each cell contributes a different value instead of a flat 1):** replace `area++` with `area += grid[cur[0]][cur[1]]`, but note this requires capturing the value *before* overwriting the cell to a sentinel, or using a separate `boolean[][] visited` array instead of in-place `0`-marking.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | grid=[[0,0,0],[0,0,0]] | 0 | all water, no islands |
| 2 | grid=[[1,1],[1,1]] | 4 | all land, one big island |
| 3 | grid=[[0,0,0],[0,1,0],[0,0,0]] | 1 | single isolated land cell |
| 4 | grid=[[1,1,0],[0,1,0],[0,0,1]] | 3 | two islands, take the larger |
| 5 | grid=[[1,0,0,0,0],[1,1,0,0,0],[0,1,0,0,1],[0,0,0,1,1]] | 4 | two comparable-sized islands, neither touching |
| 6 | grid=[[1,1,1,1,1]] | 5 | single row, one connected island |
| 7 | grid=[[1],[1],[1]] | 3 | single column, one connected island |
| 8 | grid=[[1]] | 1 | 1x1 grid, land |
| 9 | grid=[[0]] | 0 | 1x1 grid, water |
| 10 | grid=[[1,0],[0,1]] | 1 | diagonal-only touching land, must NOT merge |
