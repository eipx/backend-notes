# Minimum Path Sum
`ref: LC 64` · Difficulty: Medium · Pattern: grid DP where each cell is reached from its top or left neighbor, kept in one row of the table (or written in place)

## Problem

You are given a grid of non-negative integers with `m` rows and `n` columns. A path starts at the top-left cell and ends at the bottom-right cell. Every step moves exactly one cell to the right or exactly one cell down, never in any other direction. The cost of a path is the sum of the numbers on every cell it visits, including the first cell and the last cell. Return the smallest cost of any path.

Input: a 2-D integer array `grid` with `m` rows and `n` columns.
Output: a single integer, the smallest sum of the cells along a right-and-down path from the top-left cell to the bottom-right cell.

## Constraints

- `m == grid.length` and `n == grid[i].length`.
- `1 <= m, n <= 200`.
- `0 <= grid[i][j] <= 200`.
- The number of different paths is `C(m + n - 2, m - 1)`, which for a `200 x 200` grid is more than `10^100`, so trying every path is impossible. Only `m * n` cells exist (at most `40000`), and each cell has a single cheapest way to be reached, so a table with one entry per cell is fast.
- A path visits `m + n - 1` cells, at most `399`, so the largest possible answer is `399 * 200 = 79800`, which fits in an `int` with a lot of room.

## Worked examples

1. `grid = [[1, 3, 1], [1, 5, 1], [4, 2, 1]]` -> `7`. Go right, right, down, down: `1 + 3 + 1 + 1 + 1 = 7`.
2. `grid = [[1, 2, 3], [4, 5, 6]]` -> `12`. Go right, right, down: `1 + 2 + 3 + 6 = 12`. The other two paths cost 14 and 16.
3. `grid = [[5]]` -> `5`. The start is also the end, so the path is the single cell and its cost is 5.
4. `grid = [[1, 1, 9], [9, 1, 9], [9, 1, 1]]` -> `5`. Go right, down, down, right through the middle column: `1 + 1 + 1 + 1 + 1 = 5`. Hugging the edges costs `1 + 1 + 9 + 9 + 1 = 21` either way.

## Edge cases checklist

- A `1 x 1` grid (the answer is the cell itself, and neither loop runs).
- A single row or a single column (there is only one path, and each cell has only one possible predecessor).
- All zeros (the answer is 0).
- The first row and the first column. These cells have only one neighbor, and a missing neighbor must not count as a free cell of cost 0.
- The cheap path that is not locally greedy: the smaller of the next two cells can lead into a wall of large numbers (the `[1, 2, 100]` row below).
- The cheapest path through the middle, while both of the "edge" routes (all right then all down, or all down then all right) are expensive.
- A tall grid and a wide grid (the loops must use `m` for rows and `n` for columns, and the answer is in the last column, `n - 1`).
- Two neighbors with an equal cost (either choice is fine).
- Cells with the largest value, 200 (the sum still fits easily).
- The start cell counts. A path that leaves out the first cell reports a total that is too small by `grid[0][0]`.
- The upper bound `200 x 200`, 40000 cells (not among the tests here).

## Approach

### Brute force

Look at the cell `(i, j)` and ask for the cheapest path from there to the corner. At the last cell the cost is the cell itself. Anywhere else it is `grid[i][j]` plus the better of `f(i + 1, j)` (down) and `f(i, j + 1)` (right), skipping any direction that leaves the grid. With no memory this tries every path, `C(m + n - 2, m - 1)` of them. Only `m * n` different cells exist, and the same cell is reached again and again by different routes, so storing the answer for each cell removes the blow-up.

### Optimal

Let `dp[i][j]` be the smallest cost of any path from the top-left cell to cell `(i, j)`, with both ends counted. The table has `m` rows and `n` columns, with no extra border:

- `dp[0][0] = grid[0][0]`: the path is just the start cell.
- First row, `j >= 1`: `dp[0][j] = dp[0][j - 1] + grid[0][j]`. The only way in is from the left.
- First column, `i >= 1`: `dp[i][0] = dp[i - 1][0] + grid[i][0]`. The only way in is from above.

For `i >= 1` and `j >= 1`, the last step into `(i, j)` came either from above, `(i - 1, j)`, or from the left, `(i, j - 1)`. The part of the path before that step must itself be a cheapest path to that neighbor, because swapping in a cheaper way there would give a cheaper path to `(i, j)`. So `dp[i][j] = grid[i][j] + min(dp[i - 1][j], dp[i][j - 1])`.

The answer is `dp[m - 1][n - 1]`. The table is filled row by row, left to right. Each cell reads its upper and left neighbors, which are final by then.

Cell `(i, j)` needs only row `i - 1` (above) and its own left neighbor in row `i`. So one array `dp` of length `n` is enough. When the loop reaches column `j` of row `i`, `dp[j]` has not been overwritten yet and still holds the cell above, while `dp[j - 1]` was just overwritten and holds the cell to the left. The solution below does this and leaves `grid` untouched. A version that writes the sums back into `grid` itself needs no extra array, at the price of destroying the input.

**Key invariant:** for every cell, the value computed is the exact smallest cost of reaching that cell. In the one-row version, at the moment cell `(i, j)` is computed, `dp[0..j - 1]` hold the finished values of row `i` and `dp[j..n - 1]` still hold the finished values of row `i - 1`. The first row and the first column are correct base cases, and every other cell reads only finished cells, so by induction the last cell is the answer for the whole grid.

### Step-by-step trace

Filled table for `grid = [[1, 3, 1], [1, 5, 1], [4, 2, 1]]`. Each entry is the cheapest cost to reach that cell.

|   | col 0 | col 1 | col 2 |
|---|---|---|---|
| row 0 | 1 | 4 | 5 |
| row 1 | 2 | 7 | 6 |
| row 2 | 6 | 8 | 7 |

The one-row array after each row is `[1, 4, 5]`, then `[2, 7, 6]`, then `[6, 8, 7]`.

A few cells worked out. The first row is a running sum: `1`, `1 + 3 = 4`, `4 + 1 = 5`. The first column is also a running sum: `1`, `1 + 1 = 2`, `2 + 4 = 6`. For `(1, 1)` the cell is 5, the cell above costs 4 and the cell to the left costs 2, so `dp[1][1] = 5 + min(4, 2) = 7`. For `(1, 2)` the cell is 1, the cell above costs 5 and the cell to the left costs 7, so `dp[1][2] = 1 + min(5, 7) = 6`. For `(2, 1)` the cell is 2, above costs 7 and left costs 6, so `dp[2][1] = 2 + min(7, 6) = 8`. For the corner, the cell is 1, above costs 6 and left costs 8, so `dp[2][2] = 1 + min(6, 8) = 7`.

Walking back from the corner shows the path: `(2, 2)` came from above, `(1, 2)`, since 6 is less than 8. `(1, 2)` came from above, `(0, 2)`, since 5 is less than 7. `(0, 2)` came from the left, `(0, 1)`, and `(0, 1)` from the left, `(0, 0)`. That is right, right, down, down, with cost `1 + 3 + 1 + 1 + 1 = 7`, as in worked example 1.

## Java 8 solution
```java
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
```

## Complexity

Time O(m * n): every one of the `m * n` cells is computed once, with constant work per cell (at most 40000 cells here). Space O(n) for the single array. A full `m x n` table uses O(m * n) space and is needed only to recover the path itself (see Variants). Writing the sums back into `grid` brings the extra space down to O(1), but it changes the caller's data.

## Java 8 pitfalls for this problem

- Padding the table with an extra row and column of zeros, as is done for the string-prefix tables. Here a zero border means "a free cell above or to the left", so the first row would take the cheaper of 0 and the real left neighbor, which is always 0. `[[1, 2, 3, 4]]` then returns 4 instead of 10. Fill the first row and the first column explicitly, as the solution does.
- Relying on the zero default of `new int[n]`. The first row must be filled before the loop over the other rows starts. If `dp` is all zeros when row 1 begins, `Math.min(dp[j], dp[j - 1])` picks a zero that is not a real cost.
- Forgetting `dp[0] += grid[i][0]` at the start of every row. The first column then keeps the value from row 0, and everything that reads it is wrong. It must come before the inner loop, because `dp[1]` reads `dp[0]`.
- Off-by-one on the starting indexes. Both loops start at 1, since row 0 and column 0 are handled separately. The answer is `dp[n - 1]`, not `dp[n]`, and the array is `new int[n]` with `n` columns, not `m`.
- Mixing up `m` and `n` on a non-square grid. Rows run to `grid.length` and columns to `grid[0].length`. A tall grid like `4 x 2` and a wide grid like `1 x 4` both appear in the tests.
- Padding with `Integer.MAX_VALUE` as "no neighbor" and then adding the cell to it. `Integer.MAX_VALUE + 5` overflows to a large negative number, which wins the `min` and gives a nonsense answer. Treat the first row and column as special cases instead of adding to a sentinel.
- Writing the sums into `grid` and then calling `solve` again on the same array (or comparing against it later). The second call starts from already-summed numbers. Copy the grid first, or use the one-row array as the solution does.
- Calling `grid[0].length` on an empty grid throws. The constraints say `m, n >= 1`, and the solution still guards against an empty array and returns 0.
- Recursion depth. A top-down version goes `m + n - 1 = 399` frames deep at most, which is safe, but without a memo it is exponential. A memo array filled with the default 0 cannot tell "not computed" from a real zero cost, so use `-1` as the marker.

## Wrong approaches and why they fail

1. **Greedy: at every cell step to the cheaper of the right and the down neighbor.** The cheap step can lead into expensive cells that cannot be avoided. Counterexample: `grid = [[1, 2, 100], [3, 90, 100], [4, 4, 1]]`. From the start the right neighbor (2) is cheaper than the down neighbor (3), so it goes right. There the right neighbor costs 100 and the down neighbor costs 90, so it goes down, then down again to 4 and right to 1, for `1 + 2 + 90 + 4 + 1 = 98`. The correct answer is `13`, from going down, down, right, right (`1 + 3 + 4 + 4 + 1`). On the first worked example the greedy also fails: it gives `9`, and the correct answer is `7`.
2. **Compare only the two edge routes, all right then all down, and all down then all right.** The best path may cut through the middle. Counterexample: `grid = [[1, 1, 9], [9, 1, 9], [9, 1, 1]]`. Both edge routes cost `1 + 1 + 9 + 9 + 1 = 21`. The correct answer is `5`, through the middle column.
3. **Use a zero border so that the first row and column need no special code.** A missing neighbor then looks like a free cell of cost 0 and is always the minimum. Counterexample: `grid = [[1, 2, 3, 4]]`. Every cell takes the "free" cell above it, so the table ends with `4`. The correct answer is `10`. On `grid = [[1, 2, 3], [4, 5, 6]]` it gives `9`, and the correct answer is `12`.
4. **Leave the start cell out of the total.** Counting only the cells that are entered gives a result that is too small by `grid[0][0]`. Counterexample: `grid = [[5]]` returns `0`, and the correct answer is `5`. On the first worked example it returns `6` instead of `7`.

## Variants

1. **Write in place.** Add the best neighbor cost into `grid[i][j]` itself. No extra array is needed, but the input is destroyed.
2. **Recover the path.** Keep the full `m x n` table. Walk back from the corner and at each cell step to the smaller of the upper and left neighbors, until the start is reached. Reverse the steps to read the path forward.
3. **Count the cheapest paths.** Keep a second table of counts. A cell's count is the sum of the counts of the neighbors that give the minimum, or of both if they tie.
4. **Count all paths.** Unique Paths (LC 62) counts the right-and-down paths instead of minimizing a cost, and Unique Paths II (LC 63) adds blocked cells. Both fill the grid with the same row-by-row order.
5. **Other shapes.** Triangle (LC 120) and Minimum Falling Path Sum (LC 931) use the same idea with different sets of allowed predecessors.
6. **Diagonal moves allowed.** Add the upper-left neighbor to the `min`: `dp[i][j] = grid[i][j] + min(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])`. With one array, save the old `dp[j - 1]` before it is overwritten.
7. **Largest sum instead of smallest.** Replace `min` by `max`. This also works with negative numbers, because a path can never return to a cell, so there are no cycles.
8. **A different recurrence from the other end.** Dungeon Game (LC 174, see `dungeon-game.md`) looks like this problem but must be filled from the bottom-right corner.
9. **Moves in all four directions.** The table order no longer works, since a cell can depend on cells that depend on it. This is a shortest-path problem on a grid, solved with Dijkstra's algorithm.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `grid=[[1,3,1],[1,5,1],[4,2,1]]` | `7` | the trace example; the step-to-the-cheaper-neighbor greedy gives 9 |
| 2 | `grid=[[1,2,3],[4,5,6]]` | `12` | wide `2 x 3` grid; a zero border gives 9 |
| 3 | `grid=[[5]]` | `5` | a single cell, the start is the end; leaving out the start gives 0 |
| 4 | `grid=[[0]]` | `0` | a single cell of zero |
| 5 | `grid=[[1,2,3,4]]` | `10` | a single row, only one path; a zero border gives 4 |
| 6 | `grid=[[1],[2],[3],[4]]` | `10` | a single column, only one path |
| 7 | `grid=[[0,0,0],[0,0,0],[0,0,0]]` | `0` | all zeros |
| 8 | `grid=[[1,2],[1,1]]` | `3` | smallest grid with a real choice, down then right beats right then down |
| 9 | `grid=[[1,2,100],[3,90,100],[4,4,1]]` | `13` | the locally cheaper step leads into a wall of large cells; the greedy gives 98 |
| 10 | `grid=[[1,1,1],[9,9,1],[9,9,1]]` | `5` | the best path runs along the top edge and down the right edge |
| 11 | `grid=[[1,9],[1,9],[1,9],[1,1]]` | `5` | tall `4 x 2` grid, down the left column then right; mixing up rows and columns fails |
| 12 | `grid=[[200,200],[200,200]]` | `600` | largest cell values, all three cells on the path counted |
| 13 | `grid=[[1,1,9],[9,1,9],[9,1,1]]` | `5` | the best path cuts through the middle; both edge routes cost 21 |
