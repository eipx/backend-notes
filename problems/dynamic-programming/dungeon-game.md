# Dungeon Game
`ref: LC 174` · Difficulty: Hard · Pattern: grid DP filled from the bottom-right corner, where the state is the minimum health needed on entering a cell

## Problem

A knight starts in the top-left room of a rectangular dungeon and has to reach the princess, who is in the bottom-right room. Every room holds an integer that is added to the knight's health when he enters the room. A negative number means demons take that much health, zero means nothing happens, and a positive number means a health orb gives that much. The knight moves one room at a time, either right or down. If his health is ever 0 or below, in any room, he dies. This includes the first room and the last room. Return the smallest health he can start with so that at least one path takes him to the princess alive.

Input: a 2-D integer array `dungeon` with `m` rows and `n` columns.
Output: a single integer, the smallest starting health that lets the knight reach the bottom-right room along some right-and-down path. It is always at least 1.

## Constraints

- `m == dungeon.length` and `n == dungeon[i].length`.
- `1 <= m, n <= 200`.
- `-1000 <= dungeon[i][j] <= 1000`.
- The number of different paths is `C(m + n - 2, m - 1)`, which for a `200 x 200` dungeon is more than `10^100`, so trying every path is impossible. Only `m * n` rooms exist (at most `40000`), so a table with one entry per room is fast.
- A path visits at most `399` rooms and each loses at most 1000 health, so the largest possible answer is `1 + 399 * 1000 = 399001`, which fits in an `int`.

## Worked examples

1. `dungeon = [[-2, -3, 3], [-5, -10, 1], [10, 30, -5]]` -> `7`. Go right, right, down, down. Starting with 7, the health after each room is `5`, `2`, `5`, `6`, `1`. Every value is at least 1 and the last room leaves exactly 1. Starting with 6 fails on every path. On this one the last room would leave 0.
2. `dungeon = [[0]]` -> `1`. The knight needs at least 1 health to be alive at all, and the room does nothing.
3. `dungeon = [[-3]]` -> `4`. With health 4 the room leaves 1. With health 3 it leaves 0, and he dies.
4. `dungeon = [[-1, 10, -6]]` -> `2`. Starting with 2, the health after each room is `1`, `11`, `5`. Starting with 1 would leave 0 after the first room, and the orb in the second room comes too late.

## Edge cases checklist

- A `1 x 1` dungeon with a zero, a positive and a negative room. The answer is never below 1, even when the room is a big orb (`[[5]]` is 1).
- The princess room itself has demons: health must be at least 1 after entering it too (`[[0, 0], [0, -10]]` is 11).
- An orb that comes after the damage cannot save the knight from dying earlier (`[[-1, 10, -6]]` is 2).
- The path with the largest total sum is not always the path that needs the least health, because it can dip lower on the way.
- A single row or a single column (only one path).
- Rooms whose gain is so large that "needed health minus the room's value" is zero or negative. The needed health must still be clamped up to 1.
- All zeros and all positive rooms (the answer is 1).
- The largest damage in every room (`-1000` each), where the answer is `1 + 3000` for a `2 x 2` dungeon.
- A non-square dungeon, such as `3 x 2` (the loops must use `m` for rows and `n` for columns).
- The upper bound `200 x 200` (not among the tests here).

## Approach

### Brute force

Try every right-and-down path. For a path, add up the room values one room at a time to get the running total after each room. The knight must satisfy `start + total >= 1` after every room, so the smallest start for that path is `1 - (smallest running total)`, but never below 1. The answer is the smallest of this over all paths. That is `C(m + n - 2, m - 1)` paths, far too many at the upper bound.

A table that works from the top-left runs into trouble. For a prefix of a path two numbers matter: the health the knight must start with, and the health he has left on arrival. Different paths to the same room trade these two off against each other, and the better one depends on what comes next. A single number per room cannot capture that. Going from the bottom-right makes the future known, and then one number per room is enough.

### Optimal

Let `need[i][j]` be the smallest health the knight must have just before entering room `(i, j)` so that he can still reach the princess alive. The table has `m` rows and `n` columns, and the answer is `need[0][0]`.

Define `next` for room `(i, j)` as the health the knight must carry out of it:

- For the princess room `(m - 1, n - 1)`, `next = 1`. Nothing comes after it, but he must be alive when it is over.
- For another room in the last row, only a step right is possible: `next = need[i][j + 1]`.
- For another room in the last column, only a step down is possible: `next = need[i + 1][j]`.
- Anywhere else he may choose the easier way on: `next = min(need[i + 1][j], need[i][j + 1])`.

Entering with health `h` leaves `h + dungeon[i][j]` after the room. That must be at least `next`, so `h >= next - dungeon[i][j]`. He also needs `h >= 1` on entering, to be alive. Therefore `need[i][j] = max(1, next - dungeon[i][j])`.

The table is filled from the bottom-right corner back to the top-left: rows from the last to the first, and each row from the last column to the first. Each room reads the room below and the room to the right, which are final by then.

The clamp to 1 matters. A big orb can make `next - dungeon[i][j]` zero or negative, which says "any health works", but the knight still needs health 1 to be standing when he walks in.

**Key invariant:** `need[i][j]` is exactly the smallest health that survives some path from `(i, j)` to the princess, counted at the moment before entering `(i, j)`. The requirement for a path only depends on the rooms after the current one, so once the requirements of the two rooms after `(i, j)` are final, the better of them is the best continuation, and the formula gives the exact value for `(i, j)`. By induction from the corner, `need[0][0]` is the answer.

### Step-by-step trace

Filled table for `dungeon = [[-2, -3, 3], [-5, -10, 1], [10, 30, -5]]`. Each entry is the smallest health needed on entering that room.

|   | col 0 | col 1 | col 2 |
|---|---|---|---|
| row 0 | 7 | 5 | 2 |
| row 1 | 6 | 11 | 5 |
| row 2 | 1 | 1 | 6 |

It is filled starting at the bottom-right. A few cells worked out:

- `(2, 2)` is the princess room with value -5, and `next = 1`, so `need = max(1, 1 + 5) = 6`.
- `(2, 1)` is in the last row, so `next = need[2][2] = 6`. Its value is 30, so `max(1, 6 - 30) = max(1, -24) = 1`. The clamp is doing the work: the big orb says any health is enough, but at least 1 is required.
- `(1, 2)` is in the last column, so `next = need[2][2] = 6`. Its value is 1, so `need = max(1, 6 - 1) = 5`.
- `(1, 1)` has value -10 and `next = min(need[2][1] = 1, need[1][2] = 5) = 1`, so `need = 1 + 10 = 11`. The cheaper way on is down, but this room hurts a lot.
- `(0, 2)` has value 3 and `next = need[1][2] = 5`, so `need = 5 - 3 = 2`.
- `(0, 1)` has value -3 and `next = min(need[1][1] = 11, need[0][2] = 2) = 2`, so `need = 2 + 3 = 5`.
- `(1, 0)` has value -5 and `next = min(need[2][0] = 1, need[1][1] = 11) = 1`, so `need = 1 + 5 = 6`.
- `(0, 0)` has value -2 and `next = min(need[1][0] = 6, need[0][1] = 5) = 5`, so `need = 5 + 2 = 7`.

The answer is `need[0][0] = 7`. Following the smaller neighbor from the start gives the path: `(0, 0)` to `(0, 1)` (5 is less than 6), to `(0, 2)` (2 is less than 11), to `(1, 2)`, to `(2, 2)`. Starting with 7 the health is `5, 2, 5, 6, 1`, as in worked example 1.

## Java 8 solution
```java
public class DungeonGame {

    // Smallest starting health that lets a knight walk from the top-left room
    // to the bottom-right room, moving only right or down, without his health
    // ever dropping to 0 or below. Every room's number is added to his health
    // when he enters it (negative means demons, positive means a health orb).
    //
    // need[i][j] is the smallest health the knight must have just before
    // entering room (i, j) so that he can still finish the walk alive. The
    // table is filled from the bottom-right corner back to the top-left,
    // because the requirement depends on what lies ahead, not behind.
    public static int solve(int[][] dungeon) {
        if (dungeon.length == 0 || dungeon[0].length == 0) {
            return 1;
        }
        int m = dungeon.length;
        int n = dungeon[0].length;
        int[][] need = new int[m][n];

        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                // The health the knight must carry out of this room: the
                // smaller requirement of the rooms he can walk into next.
                int next;
                if (i == m - 1 && j == n - 1) {
                    // Nothing comes after the princess room, but he must be
                    // alive (health at least 1) when he leaves it.
                    next = 1;
                } else if (i == m - 1) {
                    // Last row: only a move to the right is possible.
                    next = need[i][j + 1];
                } else if (j == n - 1) {
                    // Last column: only a move down is possible.
                    next = need[i + 1][j];
                } else {
                    next = Math.min(need[i + 1][j], need[i][j + 1]);
                }
                // Entering with health h leaves h + dungeon[i][j], which must be
                // at least next. A big orb can push this below 1, but the knight
                // still needs at least 1 to be alive on entering.
                need[i][j] = Math.max(1, next - dungeon[i][j]);
            }
        }
        return need[0][0];
    }

    private static void check(int caseNum, int[][] dungeon, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(dungeon);
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

        check(1, new int[][]{{-2, -3, 3}, {-5, -10, 1}, {10, 30, -5}}, 7, fail, total);
        check(2, new int[][]{{0}}, 1, fail, total);
        check(3, new int[][]{{-3}}, 4, fail, total);
        check(4, new int[][]{{5}}, 1, fail, total);
        check(5, new int[][]{{-1, 10, -6}}, 2, fail, total);
        check(6, new int[][]{{-1}, {-2}, {3}, {-4}}, 5, fail, total);
        check(7, new int[][]{{0, 0}, {0, 0}}, 1, fail, total);
        check(8, new int[][]{{1, 2}, {3, 4}}, 1, fail, total);
        check(9, new int[][]{{1, -3, 3}, {0, -2, 0}, {-3, -3, -3}}, 3, fail, total);
        check(10, new int[][]{{0, 0}, {0, -10}}, 11, fail, total);
        check(11, new int[][]{{-1000, -1000}, {-1000, -1000}}, 3001, fail, total);
        check(12, new int[][]{{-3, 5}, {-2, -1}, {4, -7}}, 7, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(m * n): every one of the `m * n` rooms is computed once, with constant work per room (at most 40000 rooms here). Space O(m * n) for the full table. Row `i` reads only row `i + 1` and the cell to its own right, so a single array of length `n` is enough and brings the space down to O(n) (see Variants).

## Java 8 pitfalls for this problem

- Filling the table from the top-left, the way most grid problems go. The requirement for a room depends on the rooms after it, so the recurrence has no simple forward form (see Wrong approaches). Loop `i` from `m - 1` down to 0, and `j` from `n - 1` down to 0.
- Leaving out the clamp, `Math.max(1, ...)`. A big orb produces a zero or negative requirement, and it then flows backward into the rooms before it. `[[-1, 10, -6]]` would return `-2` instead of 2, and a single `[[5]]` would return `-4` instead of 1.
- Clamping to 0 instead of 1. The knight dies at health 0, so the smallest legal health is 1. A clamp at 0 returns `0` for `[[5]]`, and returns 1 for `[[-1, 10, -6]]`, where 1 dies in the first room.
- Padding the table with `Integer.MAX_VALUE` as "no room" and then subtracting the room value from the minimum. At the princess room both neighbors are padding, so the formula would work on `Integer.MAX_VALUE` itself. Subtracting a negative room value, as in `Integer.MAX_VALUE - (-5)`, overflows to a large negative number, and the clamp turns that into 1, so the princess room claims to need almost nothing and every room before it is wrong. The solution avoids this by setting `next = 1` explicitly at the corner and by handling the last row and the last column separately. A padded version must put `1` into the two cells next to the corner.
- Off-by-one on the loop bounds. Both loops must reach index 0, so the condition is `i >= 0` and `j >= 0`. The neighbors `need[i + 1][j]` and `need[i][j + 1]` exist only when `i < m - 1` and `j < n - 1`.
- Relying on the zero default of `new int[m][n]`. A zero is never a legal answer here (the smallest is 1), so every cell must be written before it is read. The backward fill order guarantees that, and a wrong order silently reads zeros.
- Mixing up `m` and `n` on a non-square dungeon. Rows run to `dungeon.length` and columns to `dungeon[0].length`. The `3 x 2` test catches a swap.
- Overflow is not an issue in this problem. The largest value is `399001`, well inside an `int`. A `long` would only be needed if the values were around `10^9`.
- Recursion depth. A top-down memoized version goes at most `399` frames deep, which is safe. The zero default of a new memo array can serve as the "not computed" mark here, because no room ever needs 0 health. In the other grid pages in this group, 0 can be a real answer and does not work as a mark.

## Wrong approaches and why they fail

1. **Take the path with the largest total sum and compute what that path needs.** The total sum ignores when the damage happens. Counterexample: `dungeon = [[-2, -3, 3], [-5, -10, 1], [10, 30, -5]]`. The path down, down, right, right has the largest total, `-2 - 5 + 10 + 30 - 5 = 28`, but its running total drops to `-7` after the second room, so it needs health 8. The path right, right, down, down has a total of only `-6` and needs just `7`, which is the correct answer.
2. **Fill the table from the top-left with the same formula, `need[i][j] = max(1, min(need[i - 1][j], need[i][j - 1]) - dungeon[i][j])`.** This subtracts a room's value from a requirement that was measured at an earlier room, as if a gain in this room lowered the requirement for the rooms before it. It does not: an orb helps only after it is picked up. Counterexample: the first worked example gives `6`, which is too low, because the correct answer is `7`. For `[[1, -3, 3], [0, -2, 0], [-3, -3, -3]]` it gives `4`, and the correct answer is `3`.
3. **Treat health 0 as alive, or clamp the requirement at 0.** The knight dies when his health drops to 0. Counterexample: `dungeon = [[5]]`. A clamp at 0 returns `0`, and the correct answer is `1`. For `[[-1, 10, -6]]` it returns `1`, because the requirement of the middle room is clamped to 0 and the first room then asks for only 1. The correct answer is `2`.
4. **Leave out the clamp altogether.** A negative requirement after a big orb then reduces the requirement of every room before it. Counterexample: `dungeon = [[-1, 10, -6]]`. The last room needs 7, the middle room needs `7 - 10 = -3`, and the first room needs `-3 + 1 = -2`, so it returns `-2`. The correct answer is `2`. On the first worked example it returns `-27`.

## Variants

1. **One-row version.** Room `(i, j)` reads only the room below (the old value in the same column) and the room to the right (just written in the same row), so one array of length `n` is enough and the space drops to O(n). Loop the columns from right to left, as the table does.
2. **Recover the path.** From `(0, 0)`, repeatedly step to the neighbor (right or down) with the smaller `need`. The health along that path stays at least 1 when the knight starts with `need[0][0]`.
3. **Binary search on the starting health.** For one fixed start `h`, run a forward table of the most health the knight can have on leaving each room (marking rooms where it would be 0 or below as unreachable). Whether `h` works is monotone, so a binary search over `1 ... 1 + 1000 * (m + n - 1)` finds the answer in O(m * n * log(range)). The backward table is both simpler and faster.
4. **The sum version.** Minimum Path Sum (LC 64, see `minimum-path-sum.md`) has the same grid and moves, but costs add up and nothing has to stay above a floor, so a top-left table is enough there.
5. **Moves in all four directions.** The fill order no longer works, because a room can depend on rooms that depend on it. This becomes a shortest-path style search where the cost of a path is its required starting health.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `dungeon=[[-2,-3,3],[-5,-10,1],[10,30,-5]]` | `7` | the trace example; the largest-total path needs 8, and a top-left table gives 6 |
| 2 | `dungeon=[[0]]` | `1` | a single empty room, the answer is 1 and not 0 |
| 3 | `dungeon=[[-3]]` | `4` | a single room with demons |
| 4 | `dungeon=[[5]]` | `1` | a single orb, the answer is clamped to 1; clamping at 0 gives 0 |
| 5 | `dungeon=[[-1,10,-6]]` | `2` | a late orb cannot save an early death; without the clamp the result is -2, a top-left table gives 7 |
| 6 | `dungeon=[[-1],[-2],[3],[-4]]` | `5` | a single column with an orb in the middle |
| 7 | `dungeon=[[0,0],[0,0]]` | `1` | all zeros |
| 8 | `dungeon=[[1,2],[3,4]]` | `1` | all orbs; clamping at 0 gives 0 |
| 9 | `dungeon=[[1,-3,3],[0,-2,0],[-3,-3,-3]]` | `3` | the best path goes right, right, down, down; a top-left table gives 4 |
| 10 | `dungeon=[[0,0],[0,-10]]` | `11` | demons in the princess room itself |
| 11 | `dungeon=[[-1000,-1000],[-1000,-1000]]` | `3001` | largest damage in every room |
| 12 | `dungeon=[[-3,5],[-2,-1],[4,-7]]` | `7` | non-square `3 x 2` dungeon; a top-left table gives 9 |
