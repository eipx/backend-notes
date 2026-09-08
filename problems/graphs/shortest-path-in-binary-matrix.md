# Shortest Path in Binary Matrix
`ref: LC 1091` · Difficulty: Medium · Pattern: 8-direction BFS with level counting

## Problem
An `n x n` grid holds only `0`s (open, walkable) and `1`s (blocked). Starting at the top-left cell `(0,0)` and ending at the bottom-right cell `(n-1,n-1)`, find the length of the shortest "clear path" between them, where a path is a sequence of open cells, each one reachable from the previous by moving to any of its 8 neighbors (up, down, left, right, and all four diagonals), and the path's length is the total number of cells visited (including both the start and end cells). If the start or end cell is itself blocked, or no such path exists at all, the answer is `-1`.

## Constraints
- `1 <= n <= 100` (grid is always square).
- Each cell is exactly `0` or `1`.
- Movement is 8-directional (queen-move adjacency), not just the 4 orthogonal directions used by most other grid problems in this set -- diagonal moves are allowed and count as a single step just like orthogonal ones.
- The start and end cell can coincide when `n == 1`.

## Worked examples
1. `grid = [[0,1],[1,0]]` -> `2`. The only two open cells are the start `(0,0)` and the end `(1,1)`, and they are diagonally adjacent, so one diagonal step connects them; path length counts both cells, giving `2`.
2. `grid = [[0,0,0],[1,1,0],[1,1,0]]` -> `4`. The left column is blocked below the top row, so the path must go right along the top, then down the open right column: `(0,0) -> (0,1) -> (0,2)/(1,2) -> (2,2)`, four cells total.
3. `grid = [[1,0,0],[1,1,0],[1,1,0]]` -> `-1`. The very first cell `(0,0)` is blocked, so no path can even begin.
4. `grid = [[0]]` -> `1`. A single-cell grid where the start and end are the same cell: the path is just that one cell, length `1`.

## Edge cases checklist
- Start cell blocked (`grid[0][0] == 1`) -- return `-1` immediately without doing any search.
- End cell blocked (`grid[n-1][n-1] == 1`) -- return `-1` immediately (this is easy to miss if only the start cell is checked).
- `n == 1`: start and end are the same cell; if that cell is open the answer is `1`, if blocked the answer is `-1` (covered by the start/end check above, since start==end here).
- No path exists even though both endpoints are open (they're on separate "islands" of open cells with no connecting chain of 8-directional moves).
- A grid that is entirely open (all `0`s) -- the shortest path should use diagonal moves to go directly toward the corner, giving a much shorter path than a 4-directional BFS would.
- Start and end are 8-directionally adjacent (shortest possible non-trivial path, length `2`).
- A grid where the only path available is a "staircase" of diagonal moves, exercising that all 8 directions (not just 4) are checked.

## Approach
### Brute force
Explore every possible path from the start with DFS, backtracking whenever a dead end is hit, and track the minimum path length seen across all complete paths that reach the end. This is exponential in the worst case, because DFS can wander down long, ultimately-unproductive branches before backtracking, and revisiting cells without a strong pruning rule can blow up combinatorially on anything but the smallest grids. It also doesn't naturally stop at the *first* time the end is reached with the fewest steps -- it needs to explore essentially everything to be sure no shorter path exists elsewhere.

### Optimal
Because every move costs exactly one step regardless of direction (unweighted edges), plain BFS from the single source `(0,0)` finds the shortest path to every other reachable cell, processing cells in non-decreasing order of distance. Use the same "level snapshot" trick as Rotting Oranges: seed a queue with the start cell, then repeatedly drain the queue one full level at a time, incrementing a path-length counter once per level, and mark each cell visited the moment it's enqueued (reusing the grid itself, or a separate `visited` array) so no cell is processed twice. Stop as soon as the end cell is dequeued.

**Key invariant:** when a cell is dequeued during BFS level `t` (counting the start cell's own level as `t=1`), the shortest path from the start to that cell has exactly `t` cells, because BFS explores all cells at graph-distance `t-1` completely before touching any cell at distance `t`. Marking a cell visited on first discovery (not first processing) guarantees each cell is assigned the minimum possible level exactly once.

Proof sketch: BFS visits nodes in order of non-decreasing distance from the source specifically because it processes the queue in FIFO order and only enqueues a node the very first time it's reached; since every edge (8-directional adjacency) has the same "cost" of one step, the level at which a node is first dequeued equals its true shortest-path distance from the source measured in number of cells. The moment the end cell is dequeued, the level counter at that point is therefore its true shortest path length, and returning immediately avoids any need to keep searching once the answer is known.

### Step-by-step trace
Trace on `grid = [[0,0,0],[1,1,0],[1,1,0]]` (n=3, rows `r=0..2`, cols `c=0..2`), start `(0,0)`, end `(2,2)`:

| length (level) | queue at start of level | cells discovered this level | notes |
|---|---|---|---|
| 1 (seed) | [(0,0)] | -- | mark (0,0) visited, length starts at 1 |
| 2 | [(0,0)] | (0,1) | (0,0)'s open 8-neighbors: only (0,1) is open and in bounds; (1,0) and (1,1) are blocked |
| 3 | [(0,1)] | (0,2), (1,2) | from (0,1): (0,2) open, (1,2) open (diagonal), (1,0)/(1,1) blocked |
| 4 | [(0,2), (1,2)] | (2,2) | from (0,2): (1,2) already visited; from (1,2): (2,2) open (straight down) -- this is the end cell |
| -- | (2,2) dequeued at length 4 | | return 4 |

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.Deque;

public static int solve(int[][] grid) {
    int n = grid.length;
    if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) {
        return -1; // start or end blocked, no path can exist
    }
    if (n == 1) {
        return 1; // single open cell is both start and end
    }
    int[][] dirs = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    }; // all 8 queen-move directions
    Deque<int[]> queue = new ArrayDeque<int[]>();
    queue.add(new int[]{0, 0});
    grid[0][0] = 1; // reuse the grid as the visited marker
    int length = 1;
    while (!queue.isEmpty()) {
        int levelSize = queue.size(); // snapshot: this minute's/level's worth of cells
        for (int i = 0; i < levelSize; i++) {
            int[] cur = queue.poll();
            if (cur[0] == n - 1 && cur[1] == n - 1) {
                return length; // reached the end at the current level's distance
            }
            for (int[] d : dirs) {
                int nr = cur[0] + d[0], nc = cur[1] + d[1];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && grid[nr][nc] == 0) {
                    grid[nr][nc] = 1; // mark visited on discovery
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        length++;
    }
    return -1; // queue drained without ever reaching the end
}
```

## Complexity
- Time: `O(n^2)` -- every cell is enqueued and dequeued at most once, and each dequeue checks a constant 8 neighbors.
- Space: `O(n^2)` -- the queue can hold up to every cell in the worst case.

## Java 8 pitfalls for this problem
- Forgetting the **end-cell-blocked** check is a very common miss: only checking `grid[0][0] == 1` and skipping the symmetric check on `grid[n-1][n-1]` lets BFS run to completion and correctly return `-1` anyway (since the end is never dequeued), so this particular omission doesn't break correctness here -- but it does waste a full BFS pass when an instant `-1` was available, and the habit of skipping symmetric edge checks causes real bugs in other similar problems.
- All 8 directions must be listed explicitly (`dirs` has 8 entries here, not 4) -- accidentally reusing the 4-directional `dirs` array from a problem like Rotting Oranges or Max Area of Island silently understates true reachability and can turn a valid short diagonal-heavy path into a wrongly-reported `-1` or an inflated length.
- The `n == 1` special case must be checked *after* the blocked-cell check, not instead of it -- a 1x1 grid holding a `1` must still return `-1`, not `1`.
- `ArrayDeque` rejects `null`; storing coordinates as `int[]{r, c}` avoids ever needing to queue a `null` sentinel, unlike some level-counting schemes that push a `null` marker to signal "end of level" (a pattern to avoid here in favor of the `queue.size()` snapshot).
- Path length here counts *cells*, not *edges* -- the level counter starts at `1` for the seeded start cell (not `0`), which is the opposite convention from graph-distance BFS that typically starts a `distance` array at `0` for the source; mixing up these two conventions is an easy off-by-one to introduce when adapting BFS code between problems.
- Mutating the input `grid` in place as the visited marker means, exactly as in Max Area of Island, that calling `solve` twice on the same array reference gives a stale/wrong result the second time.

## Wrong approaches and why they fail
- **4-directional BFS (reusing the Rotting Oranges/Max Area of Island `dirs` array):** this ignores diagonal moves entirely, which can make a real shortest path look nonexistent, or force a much longer detour. On `grid=[[0,1],[1,0]]`, a 4-directional BFS from `(0,0)` cannot reach `(1,1)` at all (both of its orthogonal neighbors are blocked) and would incorrectly report `-1`, when the true 8-directional answer is `2`.
- **Dijkstra's algorithm with a priority queue:** technically correct (Dijkstra generalizes BFS), but pointless overhead here -- every edge has the same weight of `1`, so plain BFS already finds the shortest path in `O(n^2)` without the `O(n^2 log n)` overhead of a priority queue; reaching for Dijkstra signals a misunderstanding of when it's actually needed (unequal edge weights).
- **Marking a cell visited only when it is dequeued, not when it is enqueued:** the same cell can be discovered by multiple different cells at the same BFS level before any of them are processed, so without marking on discovery it gets queued multiple times, wasting work and, in pathological cases, contributing to inflated queue sizes that break the `levelSize` snapshot's assumption that each queued cell is genuinely new.

## Variants
- **Return the actual sequence of cells forming the shortest path, not just its length:** track a `parent` map (`Map<cell, cell>` or a `parentR`/`parentC` grid) alongside the BFS and walk it backwards from the end cell once found.
- **Weighted movement cost (diagonal moves cost more than orthogonal ones):** plain BFS's level-snapshot trick breaks down once edges have different costs; that variant needs Dijkstra's algorithm with a priority queue keyed on accumulated cost, similar to the note in Rotting Oranges' Variants section.
- **Compare against 4-directional-only shortest path in the same grid:** simply swap the `dirs` array to the 4-entry version used elsewhere in this folder -- useful for building intuition about how much diagonal movement can shorten a path.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | grid=[[0,1],[1,0]] | 2 | minimal diagonal-adjacent path |
| 2 | grid=[[0,0,0],[1,1,0],[1,1,0]] | 4 | path forced around a blocked region, using a diagonal shortcut |
| 3 | grid=[[1,0,0],[1,1,0],[1,1,0]] | -1 | start cell blocked |
| 4 | grid=[[0,0,0],[1,1,0],[1,1,1]] | -1 | end cell blocked |
| 5 | grid=[[0]] | 1 | 1x1 grid, open |
| 6 | grid=[[1]] | -1 | 1x1 grid, blocked |
| 7 | grid=[[0,0],[0,0]] | 2 | smallest 2x2 fully-open grid, one diagonal step |
| 8 | grid=[[0,1,1],[1,1,1],[1,1,0]] | -1 | start cell fully walled in by blocked neighbors |
| 9 | grid=[[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0]] | 4 | fully open grid, diagonal shortcut straight to the corner |
| 10 | grid=[[0,0,1],[1,0,0],[1,0,0]] | 3 | pure diagonal shortcut through the open middle despite a blocked row and column |
