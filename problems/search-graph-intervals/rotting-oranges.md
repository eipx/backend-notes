# Rotting Oranges
`ref: LC 994` · Difficulty: Medium · Pattern: Multi-source BFS with level snapshot

## Problem
A grid of cells represents a crate of oranges. Each cell holds one of three states: empty (no orange), a fresh orange, or a rotten orange. Every minute, any rotten orange spreads rot to every fresh orange that is orthogonally adjacent to it (up, down, left, right -- not diagonally); all such spreads happen simultaneously across the whole grid for that minute, and the newly rotten oranges can spread further starting the next minute.

The output is the minimum number of minutes that must pass until no fresh orange remains anywhere in the grid. If some fresh orange can never be reached by any chain of rot (because it is isolated by empty cells or grid edges from every rotten orange), the output is `-1` instead. "Valid" spreading means: a fresh cell turns rotten in minute `t` only if it is adjacent to a cell that was already rotten at the start of minute `t` (either originally rotten, or turned rotten in a strictly earlier minute).

## Constraints
- `1 <= rows, cols <= 10` (a small grid, but the shape of the algorithm should still generalize to larger grids in `O(rows * cols)`)
- Each cell is `0` (empty), `1` (fresh), or `2` (rotten)
- With at most 100 cells, any polynomial approach is fast enough, but the *correct* minute count requires a level-by-level (BFS) traversal, not a plain visited-set BFS/DFS that ignores distance.

## Worked examples
1. `grid = [[2,1,1],[1,1,0],[0,1,1]]` -> `4`. The rot spreads outward one ring per minute from the single rotten corner; the farthest fresh orange (bottom-right) is 4 steps away along the only path around the `0` blocking the middle-right cell.
2. `grid = [[2,1,1],[0,1,1],[1,0,1]]` -> `-1`. The fresh orange at the bottom-left corner is walled off by `0` cells on both of its orthogonal neighbors that lead back toward the rotten source, so it can never rot.
3. `grid = [[0,2]]` -> `0`. There are no fresh oranges to begin with, so zero minutes are needed regardless of how many rotten oranges exist.

## Edge cases checklist
- No fresh oranges anywhere (`fresh == 0` from the start) -> answer is `0` immediately, no BFS needed.
- No rotten oranges but at least one fresh orange -> every fresh orange is unreachable -> answer is `-1` immediately.
- A fresh orange isolated by `0` cells from all rotten sources (unreachable orange, but not all fresh oranges are unreachable).
- Multiple rotten oranges present at minute 0 (multi-source BFS, not single-source).
- A `1x1` grid containing just `0`, just `1`, or just `2`.
- The grid has rotten oranges touching each other (no effect, they are already rotten).
- Fresh oranges that all rot in the very first minute (answer `1`).
- Grid entirely fresh with zero rotten sources (already covered above, but worth checking as its own scenario since it looks similar to case 2 but larger).

## Approach
### Brute force
Repeatedly scan the entire grid, and on each pass rot any fresh orange adjacent to a currently-rotten one, stopping when a full pass makes no changes; count passes. This is essentially BFS done manually with an `O(rows*cols)` scan per minute instead of a queue, giving `O((rows*cols)^2)` in the worst case since the number of minutes can itself be `O(rows*cols)`. At the given bounds (`rows,cols <= 10`, so at most 100 cells) this is actually fast enough to pass, but it does not scale and it obscures the real level-by-level structure that BFS with a queue makes explicit and easy to reason about.

### Optimal
Multi-source BFS: seed a queue with the coordinates of every rotten orange at minute 0 simultaneously (not just one), and count fresh oranges. Then repeatedly process the queue **one full level at a time** (i.e. capture `queue.size()` before the inner loop, and only process that many nodes before incrementing the minute counter) -- popping a node, rotting its fresh neighbors, decrementing the fresh counter, and pushing the newly rotten neighbors onto the queue for the next level.

**Key invariant:** at the start of processing BFS level `t`, the queue holds exactly the set of cells that turned rotten at minute `t-1` (or the original rotten cells for `t=0`), so processing a full level and then incrementing `minutes` correctly counts "one minute equals one BFS level," and the number of completed levels when the queue drains equals the minimum number of minutes for the rot to reach every reachable fresh orange.

Proof sketch: BFS from multiple sources simultaneously computes shortest distance (in graph-edge terms) from the nearest source to every reachable node, processing nodes in non-decreasing order of distance. Since rot legitimately spreads to *all* adjacent fresh oranges from *all* currently-rotten oranges every minute, the "distance from nearest rotten source" computed by multi-source BFS is exactly the minute at which a given orange rots. The maximum such distance among all fresh oranges is the answer, and the BFS level-count naturally tracks this maximum as it processes level by level. If any fresh orange remains unreached when the queue empties, it is unreachable, so the answer is `-1`.

### Step-by-step trace
Trace on `grid = [[2,1,1],[1,1,0],[0,1,1]]` (rows are `r=0..2`, cols are `c=0..2`, fresh count starts at 6):

| minute | queue at start of level | cells rotted this level | fresh remaining after |
|---|---|---|---|
| 0 (seed) | [(0,0)] | -- (seeding only) | 6 |
| 1 | [(0,0)] | (0,1), (1,0) | 4 |
| 2 | [(0,1), (1,0)] | (0,2), (1,1) | 2 |
| 3 | [(0,2), (1,1)] | (2,1) | 1 |
| 4 | [(2,1)] | (2,2) | 0 |
| -- | queue empty, fresh == 0 | | return minutes = 4 |

(Cell `(1,2)` is `0`/empty and `(2,0)` is `0`/empty in this grid, so they are never queued.)

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.Deque;

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
```

## Complexity
- Time: `O(rows * cols)` -- every cell is enqueued and dequeued at most once, and each dequeue does `O(1)` work per of its 4 neighbors.
- Space: `O(rows * cols)` -- the queue can hold up to every cell in the worst case (e.g. a grid that is entirely rotten already).

## Java 8 pitfalls for this problem
- `ArrayDeque` rejects `null` elements, which is actually useful here: storing coordinates as `int[]{r, c}` and never `null` avoids `NullPointerException` surprises; do not use a `LinkedList` just out of habit when `ArrayDeque` is the faster, more idiomatic choice for a BFS queue.
- The bounds check must be `nr >= 0 && nr < rows && nc >= 0 && nc < cols` -- writing `nr > 0` instead of `nr >= 0` silently drops row 0 and column 0 from consideration, which would miscount islands of rot near the grid's top/left edge (the same classic bug documented in Number of Islands in this folder).
- Capturing `levelSize = queue.size()` **before** the inner `for` loop is the entire trick to turning a plain BFS into a "BFS with level snapshot." Forgetting this and instead writing `for (int[] cur : queue)` while also mutating the queue inside the loop causes a `ConcurrentModificationException` or, worse, silently processes newly-added same-minute cells as if they were part of the current minute, undercounting the answer.
- Mutating `grid[nr][nc] = 2` in place doubles as the "visited" marker, avoiding a separate `boolean[][] visited` array; this is fine for a single call, but if `solve` were called twice on the same grid reference the second call would see a fully-rotten grid and immediately return `0`, since Java arrays are passed by reference.
- Return type pitfall: comparing `fresh == 0` at the very end (not just checking `queue.isEmpty()`) is required, because the queue naturally empties whether or not every fresh orange was actually reached.

## Wrong approaches and why they fail
- **DFS from each rotten orange independently, tracking the minimum minute each fresh orange is reached:** without careful global bookkeeping (e.g. a shared distance array updated only when a shorter distance is found, revisiting nodes as needed), a naive DFS marks a fresh orange as "reached" the first time any path touches it, which is not necessarily the shortest (fastest) path, and can produce an inflated minute count.
- **Plain single-queue BFS without snapshotting `queue.size()`:** processing the queue element-by-element while blindly incrementing `minutes` after every single pop (instead of every full level) counts a "minute" per orange rather than per wave of oranges, wildly overcounting the answer -- for `grid=[[2,1,1],[1,1,0],[0,1,1]]` this could report `6` instead of the correct `4`.
- **Assuming the answer is always `max(rows, cols) - 1` or some other fixed formula based on grid shape:** this ignores blocked/empty cells entirely; `grid=[[2,1,1],[0,1,1],[1,0,1]]` has the same shape as the working example but returns `-1` because a fresh orange is walled off, which no grid-dimension formula can predict.

## Variants
- **Return the actual final grid state (all reachable oranges rotted) instead of just the minute count:** the BFS body is unchanged; just return `grid` (or a copy of it) once the loop ends instead of computing `minutes`.
- **LC 200 Number of Islands** shares the exact same 4-directional bounds-check and BFS/DFS traversal skeleton, but counts connected components instead of propagating a wave -- see the companion note in this folder.
- **Weighted or diagonal spread (8-directional rot, or rot that takes longer through certain cell types):** the level-snapshot BFS shape breaks down once edges have different costs; that variant needs Dijkstra's algorithm with a priority queue keyed on rot-arrival time instead of a plain FIFO queue.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | grid=[[2,1,1],[1,1,0],[0,1,1]] | 4 | standard case from the walkthrough |
| 2 | grid=[[2,1,1],[0,1,1],[1,0,1]] | -1 | one fresh orange walled off and unreachable |
| 3 | grid=[[0,2]] | 0 | no fresh oranges at all |
| 4 | grid=[[0]] | 0 | 1x1 grid, empty cell only |
| 5 | grid=[[1]] | -1 | 1x1 grid, single fresh orange, no rotten source anywhere |
| 6 | grid=[[2]] | 0 | 1x1 grid, single rotten orange, no fresh oranges |
| 7 | grid=[[2,2,2],[1,1,1],[0,1,1]] | 2 | multi-source BFS, two-level spread |
| 8 | grid=[[1,1],[1,1]] | -1 | all fresh, zero rotten sources |
| 9 | grid=[[2,1],[1,1]] | 2 | small grid, everything reachable within 2 minutes |
| 10 | grid=[[2,0,0],[0,0,0],[0,0,1]] | -1 | fresh orange isolated by a wall of empty cells |
