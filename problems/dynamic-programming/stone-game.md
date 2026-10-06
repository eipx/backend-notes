# Stone Game
`ref: LC 877` · Difficulty: Medium · Pattern: interval DP for a two-player game; `dp[i][j]` = best score difference for the player to move on piles `i..j`

## Problem

Alice and Bob play a game on a row of piles of stones, and `piles[i]` is the number of stones in pile `i`. The players take turns, with Alice going first. On a turn, the player to move takes the whole pile at the left end or the whole pile at the right end of the row. The game ends when no piles are left, and the player holding more stones wins. Both players play perfectly. Return `true` if Alice wins and `false` if she does not (a tie is not a win).

Input: an integer array `piles`.
Output: a boolean, `true` when Alice finishes with strictly more stones than Bob under perfect play, otherwise `false`.

## Constraints

- `2 <= piles.length <= 500`
- `piles.length` is even.
- `1 <= piles[i] <= 500`
- `sum(piles)` is odd, so the two players can never tie.
- The total number of stones is fixed, so a player who wants more stones is the same as a player who wants a larger lead. That is why one table of score differences is enough.
- A plain recursion makes two calls per turn and the game lasts `n` turns, so it makes up to `2^n` calls, which is hopeless at `n = 500`. Only `n * (n + 1) / 2` different ranges of piles `i..j` exist (at most `125250`), so a table with one cell per range is fast.
- Inside these limits the answer is always `true` (the fourth wrong approach below explains why). The table on this page solves the general game, so it also handles the inputs outside the limits that appear in tests 9 to 12 (an empty row, one pile, a tie, an odd number of piles).

## Worked examples

1. `piles = [5, 3, 4, 5]` -> `true`. Alice takes the left `5`, which leaves `[3, 4, 5]`. If Bob takes the right `5`, Alice takes `4` and Bob gets the last `3`, so Alice has `9` and Bob has `8`. If Bob takes the left `3` instead, Alice takes `5` and ends with `10` against `7`. Even Bob's better choice loses by one stone.
2. `piles = [3, 7, 2, 3]` -> `true`. Alice takes the right `3`, which leaves `[3, 7, 2]`. Whichever end Bob takes, the `7` is the next pile Alice reaches, so she ends with `10` and Bob with `5`. Taking the left `3` first would lose: Bob takes the `7`, and Alice ends with `6` against `9`. The first move matters even when both ends are equal.
3. `piles = [1, 2]` -> `true`. Alice takes the `2`.
4. `piles = [1, 100, 1]` -> `false`. This is outside the limits (the count of piles is odd). Alice has to take a `1` from an end, which hands Bob the `100`, so she finishes with `2` against `100`.
5. `piles = [2, 2]` -> `false`. This is outside the limits too (the total is even). Each player gets one pile, the game is tied, and a tie is not a win.

## Edge cases checklist

- Two piles, the smallest input the limits allow (Alice takes the larger one, so the answer is `true`).
- The same two piles in the opposite order (`[1, 2]` and `[2, 1]`). Reversing the row never changes the answer.
- Two equal piles (outside the limits). The score is tied and the answer is `false`, which separates a `> 0` check from a `>= 0` check.
- A single pile (outside the limits). Alice takes it and wins, so the answer is `true`.
- An empty row (outside the limits). Both players score 0, which is a tie, so the answer is `false`. The table must not be indexed at `dp[0][n - 1]` when `n` is 0.
- A huge pile in the middle with an even count (`[4, 100, 3, 2]`). The right first move is the small end `2`, so that Alice, and not Bob, is the one who reaches the `100` later.
- A huge pile in the middle with an odd count (`[1, 100, 1]`, outside the limits). Bob is the one who reaches it.
- The two ends equal (`[3, 7, 2, 3]`). Both first moves look alike, but they lead to different results.
- A long flat middle between two large ends (`[6, 1, 1, 1, 1, 7]`), where the final lead is only one stone.
- The upper bound, 500 piles of up to 500 stones (about 125 thousand table cells; the total is at most `250000`, well inside `int`).

## Approach

### Brute force

Look at the row from the point of view of the player to move. Let `diff(i, j)` be the largest lead (stones of the player to move minus stones of the other player) that the player to move can force on the piles `i..j`. The player takes pile `i` or pile `j`. Then the other player becomes the player to move on the smaller row, and the lead that player forces there is taken away from the first player's gain: `diff(i, j) = max(piles[i] - diff(i + 1, j), piles[j] - diff(i, j - 1))`. With one pile left, the player to move takes it, so `diff(i, i) = piles[i]`. An empty row is worth 0. Alice wins exactly when `diff(0, n - 1) > 0`.

With no memory this makes two calls per call and goes `n` levels deep, so up to `2^n` calls. Only `n * (n + 1) / 2` different ranges `(i, j)` exist, and the same ranges are reached over and over, so storing the answers in a table removes the blow-up.

### Optimal

Let `dp[i][j]`, for `0 <= i <= j < n`, be the best lead the player to move can force on the piles `i..j`. The table is square, but only the cells with `i <= j` are used.

- `dp[i][i] = piles[i]`: with one pile left, the player to move takes it, and the opponent gets nothing.
- For `i < j`: `dp[i][j] = max(piles[i] - dp[i + 1][j], piles[j] - dp[i][j - 1])`.

The second line is the whole idea. Taking pile `i` gains `piles[i]` stones now. Then the opponent is the player to move on the range `i + 1..j`, and the best lead the opponent can force there is `dp[i + 1][j]`. That lead is the opponent's gain, so it is subtracted. The subtraction swaps the point of view, so the table never needs to know whether Alice or Bob is moving. It also replaces "the opponent plays to hurt me" by "the opponent plays to help himself", which is the same thing in a game with a fixed total.

The answer is `dp[0][n - 1] > 0`. The table is filled by the length of the range, from 2 up to `n`. A cell of length `len` reads two cells of length `len - 1`, which are final by then.

**Key invariant:** for every range `i..j`, `dp[i][j]` is the exact largest lead that the player to move can force on that range, no matter which player that is. The state of the game is fully described by the range (the piles that are left), the base cells are correct, and each later cell only reads cells of the shorter length that are already final. By induction on the length, the corner cell `dp[0][n - 1]` is the lead Alice can force on the whole row.

### Step-by-step trace

Filled table for `piles = [5, 3, 4, 5]`. Row `i` is the left end and column `j` is the right end of the range; cells with `j < i` are not used.

| i \ j | 0 | 1 | 2 | 3 |
|---|---|---|---|---|
| 0 | 5 | 2 | 4 | 1 |
| 1 | - | 3 | 1 | 4 |
| 2 | - | - | 4 | 1 |
| 3 | - | - | - | 5 |

A few cells worked out. `dp[0][1]` is the range `[5, 3]`. Taking the `5` gives `5 - dp[1][1] = 5 - 3 = 2`, taking the `3` gives `3 - dp[0][0] = 3 - 5 = -2`, so the cell is `2`. `dp[1][2]` is the range `[3, 4]`: `3 - 4 = -1` against `4 - 3 = 1`, so `1`. `dp[0][2]` is `[5, 3, 4]`: taking the left end gives `5 - dp[1][2] = 5 - 1 = 4`, taking the right end gives `4 - dp[0][1] = 4 - 2 = 2`, so `4`. `dp[1][3]` is `[3, 4, 5]`: `3 - dp[2][3] = 3 - 1 = 2` against `5 - dp[1][2] = 5 - 1 = 4`, so `4`. Finally `dp[0][3]` is the whole row: `5 - dp[1][3] = 5 - 4 = 1` for the left end and `5 - dp[0][2] = 5 - 4 = 1` for the right end, so the cell is `1`.

The answer is `dp[0][3] = 1 > 0`, so Alice wins. The total is `17` and the lead is `1`, so the final score is `9` to `8`, as in worked example 1. Following the table: Alice takes the left `5`, then Bob is the player to move on `[3, 4, 5]`, where his best lead is `dp[1][3] = 4`, reached by taking the right `5`. Alice moves next on `[3, 4]` and takes the `4`, and Bob gets the `3`.

## Java 8 solution
```java
public class StoneGame {

    // Does the first player (Alice) win a game on a row of piles, when both
    // players play perfectly? On each turn the player to move takes the whole
    // pile at the left end or at the right end. The player with more stones at
    // the end wins, so a tie is not a win.
    //
    // dp[i][j] is the best score difference (stones of the player to move minus
    // stones of the other player) that the player to move can force on the
    // piles i..j. After taking a pile, the roles swap, so what the opponent
    // forces on the rest is subtracted.
    public static boolean solve(int[] piles) {
        int n = piles.length;
        if (n == 0) {
            return false;
        }
        int[][] dp = new int[n][n];

        // One pile left: the player to move takes it.
        for (int i = 0; i < n; i++) {
            dp[i][i] = piles[i];
        }

        // Fill by the length of the range, so that both smaller ranges that a
        // cell reads are already final.
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                int takeLeft = piles[i] - dp[i + 1][j];
                int takeRight = piles[j] - dp[i][j - 1];
                dp[i][j] = Math.max(takeLeft, takeRight);
            }
        }
        return dp[0][n - 1] > 0;
    }

    private static void check(int caseNum, int[] piles, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        boolean got = solve(piles);
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

        check(1, new int[]{5, 3, 4, 5}, true, fail, total);
        check(2, new int[]{3, 7, 2, 3}, true, fail, total);
        check(3, new int[]{1, 2}, true, fail, total);
        check(4, new int[]{2, 1}, true, fail, total);
        check(5, new int[]{1, 1, 3, 2}, true, fail, total);
        check(6, new int[]{4, 100, 3, 2}, true, fail, total);
        check(7, new int[]{6, 1, 1, 1, 1, 7}, true, fail, total);

        // 500 piles: 499 piles of 500 stones and a last pile of 499 (the
        // largest input the limits allow, with an odd total).
        int[] big = new int[500];
        for (int i = 0; i < big.length; i++) {
            big[i] = 500;
        }
        big[499] = 499;
        check(8, big, true, fail, total);

        check(9, new int[]{}, false, fail, total);
        check(10, new int[]{7}, true, fail, total);
        check(11, new int[]{2, 2}, false, fail, total);
        check(12, new int[]{1, 100, 1}, false, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n^2): every one of the `n * (n + 1) / 2` cells with `i <= j` is filled once, with constant work per cell. Space O(n^2) for the full table (at most `250000` ints here, about 1 MB). The values for one length depend only on the values for the length before it, so a single array of length `n` is enough and the space drops to O(n), at the price of not being able to look at the whole table afterwards (see Variants).

## Java 8 pitfalls for this problem

- Adding instead of subtracting. `piles[i] + dp[i + 1][j]` treats the opponent as a helper and turns every cell into the sum of the range. The subtraction is what makes the opponent play against you.
- Filling the table in an order where a cell reads a cell that is not final yet. Looping `i` upward with `j` inside reads `dp[i + 1][j]` before row `i + 1` exists, and it is still 0 from the allocation. Fill by the length of the range, or loop `i` downward from `n - 1` with `j` upward inside.
- Using `>= 0` instead of `> 0` at the end. A tie is not a win. The LeetCode limits make a tie impossible, but `[2, 2]` shows the difference.
- An empty array. `dp[0][n - 1]` is `dp[0][-1]`, and `new int[0][0]` has no row 0 at all, so the read throws an `ArrayIndexOutOfBoundsException`. Check `n == 0` first.
- Array sizing. The table is `new int[n][n]`. There is no extra row or column here, because no prefix sums are used. A "score" version that sums a range as `prefix[j + 1] - prefix[i]` needs an array of size `n + 1`, and writing `prefix[j] - prefix[i]` silently leaves out pile `j`.
- Integer overflow. The total is at most `500 * 500 = 250000`, so `int` is safe. With piles up to `10^9` and 500 of them, a lead can reach about `5 * 10^11` and the table must be `long`.
- Recursion depth. A top-down version goes at most `n = 500` calls deep, because each call shortens the range by one, which is safe. With 100000 piles it would overflow the stack, and the `n * n` table would not fit in memory anyway.
- Zero as the default value. A new `int[n][n]` is full of zeros, and `0` is a legal value (a balanced range), so a top-down memo cannot use `0` to mean "not computed yet". Use `Integer.MIN_VALUE` or a separate `boolean[][]`.

## Wrong approaches and why they fail

1. **Both players take the larger end.** This is greedy, and it ignores what the move uncovers for the opponent. Counterexample: `piles = [1, 1, 3, 2]`. Alice takes `2`, Bob takes `3` from `[1, 1, 3]`, and the last two piles split `1` and `1`, so Alice gets `3` and Bob gets `4`, and the method answers `false`. The correct answer is `true`: Alice takes the left `1` first, and whatever Bob takes from `[1, 3, 2]`, she reaches the `3` next, ending `4` to `3`. For `[4, 100, 3, 2]` the method gives Alice `7` and Bob `102` and answers `false`, while the correct answer is `true`: Alice takes the small `2` first and ends with `102` against `7`.
2. **Let the two players cooperate (add instead of subtract), then compare with half the total.** The recurrence `dp[i][j] = max(piles[i] + dp[i + 1][j], piles[j] + dp[i][j - 1])` has one player collect every pile, so each cell is the sum of its range and the test "more than half" always passes. Counterexample (outside the limits): `piles = [2, 2]` gives `true`, and the correct answer is `false` (a tie). `[1, 100, 1]` gives `true`, and the correct answer is `false`.
3. **Fill the table with `i` going upward, so that `dp[i + 1][j]` is still 0.** A cell that reads an unfilled neighbor works from a 0 that does not belong there. Counterexample (outside the limits): `piles = [1, 100, 1]`. The cell `dp[0][2]` becomes `max(1 - 0, 1 - 99) = 1` and the method answers `true`, while the correct answer is `false`.
4. **Return `true` without computing anything.** This is correct for every input that meets the LeetCode limits, and here is why. Number the piles `0, 1, 2, ...` from the left. On each of her turns, Alice faces a row of even length, whose two ends have different parities of position. She can always take the end of the parity she picked in advance. That leaves a row of odd length whose two ends both have the other parity, so Bob has to take a pile of the other parity, and she faces an even row again. So she collects either all the even-position piles or all the odd-position piles. The total is odd, so the two sums differ, and she picks the larger. The shortcut breaks as soon as the limits do: `[2, 2]` (tie), `[1, 100, 1]` (odd count, Bob wins) and `[]` (nothing to win) all have the answer `false`.

## Variants

1. **Predict the Winner (LC 486).** The same table for any number of piles, with player one winning on a tie, so the final test is `dp[0][n - 1] >= 0`.
2. **Rolling array.** Keep one array `d` of length `n`. Before the pass for length `len`, `d[i]` holds the value for the range of length `len - 1` that starts at `i`. With `j = i + len - 1`, the update `d[i] = max(piles[i] - d[i + 1], piles[j] - d[i])`, going with `i` upward, turns the array into the values for length `len`. Space drops to O(n).
3. **Report the scores, not only the winner.** If `lead = dp[0][n - 1]` and `total` is the sum of all piles, Alice finishes with `(total + lead) / 2` and Bob with `(total - lead) / 2`.
4. **Stone Game III (LC 1406).** A player takes one, two or three piles from the front of the row, and the answer is Alice, Bob or a tie. The same lead-based table, over suffixes of the row.
5. **Stone Game VII (LC 1690).** A player removes either end and scores the sum of the piles that remain. The same interval table of leads, with prefix sums to get the score of a range in constant time.
6. **Top-down version.** The same recurrence written as a recursion with a memo table. It reads naturally and goes at most `n` levels deep, but it needs a "not computed" marker other than 0.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `piles=[5,3,4,5]` | `true` | the first example; Bob's best reply still loses by one stone |
| 2 | `piles=[3,7,2,3]` | `true` | the second example; equal ends, but only the right end is a winning first move |
| 3 | `piles=[1,2]` | `true` | the smallest valid input; Alice takes the larger pile |
| 4 | `piles=[2,1]` | `true` | the mirror image of the smallest input |
| 5 | `piles=[1,1,3,2]` | `true` | taking the larger end first answers `false` whichever end is taken on a tie; no row inside the limits with fewer stones does that |
| 6 | `piles=[4,100,3,2]` | `true` | a huge pile in the middle; the right first move is the small end |
| 7 | `piles=[6,1,1,1,1,7]` | `true` | a flat middle between large ends; the lead is only one stone |
| 8 | `piles=` 500 piles, the first 499 are `500` and the last is `499` | `true` | the largest input the limits allow; the total is odd |
| 9 | `piles=[]` | `false` | outside the limits; an empty row is a 0 to 0 tie, and the table must not be indexed |
| 10 | `piles=[7]` | `true` | outside the limits; a single pile is taken by Alice |
| 11 | `piles=[2,2]` | `false` | outside the limits; a tie is not a win (fails with `>= 0`) |
| 12 | `piles=[1,100,1]` | `false` | outside the limits; an odd count hands the `100` to Bob (fails when the two players cooperate, or when the table is filled in the wrong order) |
