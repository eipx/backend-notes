# Freedom Trail
`ref: LC 514` · Difficulty: Hard · Pattern: DP over (position in key, position on the ring) with the circular distance; precomputed indices per character

## Problem

A circular ring has a lowercase letter at each of its `n` positions, numbered `0` to `n - 1` in clockwise order. A marker sits at the top of the ring. At the start, ring position `0` is at the marker. You must spell the word `key` one letter at a time, in order. To spell a letter, first turn the ring clockwise or anticlockwise until a position that holds that letter is at the marker. Each single-position turn is one step. Then press the button, which is one more step, and move on to the next letter. Return the fewest total steps (turns plus presses) needed to spell the whole `key`.

Input: two strings `ring` and `key`.
Output: a single integer, the minimum number of steps (turns plus presses) needed to spell `key`.

## Constraints

- `1 <= ring.length, key.length <= 100`
- Both strings contain only lowercase letters `a` to `z`.
- Every letter of `key` appears somewhere in `ring`, so an answer always exists.
- A letter can appear many times in `ring`, and each key letter may use any of its copies. A plain recursion that tries every copy for every key letter makes up to `c^m` calls for `c` copies and a key of `m` letters (for a ring of 100 `a` characters and a key of 100 `a` characters that is `100^100`). Only `m * n` different pairs (letters spelled, ring position) exist (at most `10000`), so a table with one cell per pair is fast.

## Worked examples

1. `ring = "godding"`, `key = "gd"` -> `4`. The `g` is already at the marker, so press (1 step). The `d` at index 2 is two turns from index 0, and the `d` at index 3 is three turns away (the other way round it is four), so use index 2: 2 turns and 1 press. Total `1 + 2 + 1 = 4`.
2. `ring = "godding"`, `key = "godding"` -> `13`. Walk forward one position at a time: indices `0, 1, 2, 3, 4, 5, 6` hold `g, o, d, d, i, n, g`. That is 6 turns and 7 presses.
3. `ring = "abcde"`, `key = "ea"` -> `4`. The `e` at index 4 is one turn away from index 0, because the ring wraps around: one turn toward lower indexes goes from index 0 to index 4. Turn once and press (2 steps). Then `a` is one turn back, then press (2 steps).
4. `ring = "abcde"`, `key = "aaa"` -> `3`. The `a` is already at the marker and stays there: no turns, three presses.

## Edge cases checklist

- The smallest input, a one-letter ring and a one-letter key (`"a"` and `"a"` is 1): one press and no turns.
- The first key letter already at the top, so the first letter costs only a press.
- The same letter several times in a row in `key`: no turns, one press each (`"aaa"` is 3).
- A letter that appears several times in `ring`. Every copy has to be considered, because the nearest copy is not always the best one (`"ccabaaa"` with `"ab"`).
- Wraparound. The shorter way round may cross the seam between index `n - 1` and index `0` (`"abcde"` with `"ea"` is 4). The turns between positions `a` and `b` are `min(|a - b|, n - |a - b|)`.
- Two positions exactly opposite each other on an even ring: both directions cost the same (`"abcd"` with `"c"` is 3).
- Every key letter costs one press, so the answer is never smaller than `key.length`.
- Taking the first copy of a letter (`indexOf`) is not always best (`"abcdd"` with `"d"` is 2, using the last `d`).
- A ring where every letter is the same: all positions match, no turns are needed, and the work per row is at its largest (test 13).
- The key starts from ring index `0`, not from the first copy of the first letter.
- The upper bound, a ring and a key of 100 letters each (tests 12 and 13).

## Approach

### Brute force

Let `f(i, j)` be the fewest steps to spell the letters of `key` from index `i` to the end, when ring position `j` is at the marker. With nothing left to spell, `f(m, j) = 0`. Otherwise try every ring position `p` with `ring[p] == key[i]`. Turning from `j` to `p` costs `dist(j, p)`, the press costs 1, and then the rest costs `f(i + 1, p)`. So `f(i, j) = min over p of (dist(j, p) + 1 + f(i + 1, p))`, and the answer is `f(0, 0)`. With no memory this makes up to `c^m` calls, where `c` is the number of copies of a letter. Only `m * n` different pairs `(i, j)` exist, and the same pairs are reached over and over, so storing the answers in a table removes the blow-up. Another way to check small cases is a breadth-first search over single steps (turn one way, turn the other way, press), which follows the statement literally.

### Optimal

Let `dp[i][j]` be the fewest steps after the first `i` letters of `key` have been spelled, with ring position `j` at the marker. The table has `m + 1` rows and `n` columns. Row `0` stands for nothing spelled yet. A cell that cannot happen stays at "infinity": for `i >= 1`, the position `j` must hold the letter `key[i - 1]`, because that is the letter just pressed.

- `dp[0][0] = 0`: nothing is spelled and position `0` is at the marker. Every other cell of row `0` is infinity.

For `i >= 1`, look at each ring position `to` with `ring[to] == key[i - 1]`. These positions come from a list built once for every letter, so a key letter visits only its own copies:

- `dp[i][to] = min over from of (dp[i - 1][from] + dist(from, to) + 1)`, over every `from` whose cell in row `i - 1` is not infinity.
- `dist(a, b) = min(|a - b|, n - |a - b|)`: the shorter way round the circle.

The answer is the smallest value in row `m`. The table is filled row by row. Row `i` reads only row `i - 1`, which is final by then.

Why two coordinates are enough: what the rest of the work costs depends only on how many letters are already spelled and where the ring is now, not on how the ring got there. And between two positions the ring has only two ways to go, so the shorter arc is the cheapest turn.

**Key invariant:** after row `i` is filled, `dp[i][j]` is the exact fewest steps to spell the first `i` letters and end with position `j` at the marker, or infinity if no such way exists. Row `0` is correct by definition. Every way to spell `i` letters ends with one turn of the ring and one press after some way to spell `i - 1` letters, and the formula tries every earlier position, so by induction row `m` holds the exact best value for each final position.

### Step-by-step trace

`ring = "ccabaaa"` (indices `0` to `6` hold `c, c, a, b, a, a, a`) and `key = "ab"`. Here `n = 7`. A dash means infinity.

| i | letter pressed | j=0 | j=1 | j=2 | j=3 | j=4 | j=5 | j=6 |
|---|---|---|---|---|---|---|---|---|
| 0 | (none) | 0 | - | - | - | - | - | - |
| 1 | a | - | - | 3 | - | 4 | 3 | 2 |
| 2 | b | - | - | - | 5 | - | - | - |

Row 1: the `a` copies sit at indices 2, 4, 5 and 6. From position 0 the turns are `dist(0, 2) = 2`, `dist(0, 4) = min(4, 3) = 3`, `dist(0, 5) = min(5, 2) = 2` and `dist(0, 6) = min(6, 1) = 1`. Adding the press gives `3, 4, 3, 2`.

Row 2: the only `b` is at index 3. Each cell of row 1 gives a candidate:

- from position 2: `3 + dist(2, 3) + 1 = 3 + 1 + 1 = 5`
- from position 4: `4 + dist(4, 3) + 1 = 4 + 1 + 1 = 6`
- from position 5: `3 + dist(5, 3) + 1 = 3 + 2 + 1 = 6`
- from position 6: `2 + dist(6, 3) + 1 = 2 + 3 + 1 = 6`

The smallest is 5, so `dp[2][3] = 5` and the answer is 5.

Notice that the cheapest cell in row 1 is position 6 with value 2, and it leads to 6. The best final answer comes from position 2, whose value 3 is not the smallest in its row. Choosing the nearest copy at each key letter would give 6, and the table keeps every copy alive until the next letter decides.

Walking back from `dp[2][3]`: it came from `dp[1][2]` (a turn of 1), and `dp[1][2]` came from `dp[0][0]` (a turn of 2). So the steps are: turn 2, press `a`, turn 1, press `b`, a total of 5.

## Java 8 solution
```java
import java.util.*;

public class FreedomTrail {

    // Fewest steps to spell key on the ring. One step is either turning the
    // ring one position (clockwise or anticlockwise) or pressing the button.
    // At the start ring[0] is at the top, and every letter of key needs one
    // press while that letter is at the top.
    //
    // dp[i][j] is the fewest steps after the first i characters of key have
    // been spelled, with ring index j at the top. Only an index whose letter
    // equals key[i - 1] can have a real value for i >= 1; every other cell
    // stays at INF (unreachable).
    public static int solve(String ring, String key) {
        int n = ring.length();
        int m = key.length();

        // positions.get(c) lists every index of ring that holds the letter
        // 'a' + c, so a key character only tries the indices that can match.
        List<List<Integer>> positions = new ArrayList<List<Integer>>();
        for (int c = 0; c < 26; c++) {
            positions.add(new ArrayList<Integer>());
        }
        for (int p = 0; p < n; p++) {
            positions.get(ring.charAt(p) - 'a').add(p);
        }

        // Half of the int range, so that adding a small cost to a cell that
        // is still INF could never overflow.
        final int INF = Integer.MAX_VALUE / 2;
        int[][] dp = new int[m + 1][n];
        for (int[] row : dp) {
            Arrays.fill(row, INF);
        }
        // Nothing spelled yet and ring[0] is at the top.
        dp[0][0] = 0;

        for (int i = 1; i <= m; i++) {
            // key[i - 1] is the i-th character, so table row i covers i
            // characters.
            List<Integer> targets = positions.get(key.charAt(i - 1) - 'a');
            for (int to : targets) {
                int best = INF;
                for (int from = 0; from < n; from++) {
                    if (dp[i - 1][from] == INF) {
                        continue;
                    }
                    // Turn from -> to by the shorter way round, then press.
                    int cost = dp[i - 1][from] + ringDistance(from, to, n) + 1;
                    best = Math.min(best, cost);
                }
                dp[i][to] = best;
            }
        }

        int answer = INF;
        for (int j = 0; j < n; j++) {
            answer = Math.min(answer, dp[m][j]);
        }
        return answer;
    }

    // Turns needed to bring index b to the top when index a is at the top.
    // The ring is a circle, so going the other way round costs n - |a - b|.
    private static int ringDistance(int a, int b, int n) {
        int d = Math.abs(a - b);
        return Math.min(d, n - d);
    }

    // Java 8 has no String.repeat, so build repeated text with a loop.
    private static String rep(String unit, int times) {
        StringBuilder sb = new StringBuilder();
        for (int t = 0; t < times; t++) {
            sb.append(unit);
        }
        return sb.toString();
    }

    private static void check(int caseNum, String ring, String key, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(ring, key);
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

        check(1, "godding", "gd", 4, fail, total);
        check(2, "godding", "godding", 13, fail, total);
        check(3, "a", "a", 1, fail, total);
        check(4, "ab", "b", 2, fail, total);
        check(5, "abcde", "ea", 4, fail, total);
        check(6, "abcde", "aaa", 3, fail, total);
        check(7, "abcde", "edcba", 10, fail, total);
        check(8, "abcd", "c", 3, fail, total);
        check(9, "ccabaaa", "ab", 5, fail, total);
        check(10, "abcdd", "d", 2, fail, total);
        check(11, "aaaaa", "aaaaa", 5, fail, total);
        check(12, rep("ab", 50), rep("ab", 50), 199, fail, total);
        check(13, rep("a", 100), rep("a", 100), 100, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(m * c * n), where `m = key.length`, `n = ring.length` and `c` is the largest number of copies of one letter in the ring: row `i` has at most `c` target cells, and each one scans the `n` cells of the previous row. The worst case is a ring of one repeated letter, where `c = n`, so the bound is `m * n^2 = 10^6` operations here (test 13). Building the position lists costs O(n). Space O(m * n) for the table (at most `101 * 100 = 10100` ints) plus O(n) for the lists. Keeping only the previous row cuts the table to O(n) (see Variants).

## Java 8 pitfalls for this problem

- Forgetting the wraparound. Using `Math.abs(a - b)` alone treats the ring as a straight line, and `"abcde"` with `"ea"` gives 10 instead of 4. Going the other way round costs `n - |a - b|`, and the distance is the smaller of the two.
- Leaving out the presses. Every key letter needs one press after its turns. Without them `"godding"` with `"gd"` gives 2 instead of 4, and `"godding"` with `"godding"` gives 6 instead of 13.
- Integer overflow on the "infinity" value. With `Integer.MAX_VALUE`, adding a turn cost to it wraps around to a large negative number, which then wins every `Math.min`. Use `Integer.MAX_VALUE / 2`, as here, and also skip the cells that are still infinity before adding. Real answers are at most about `100 * 51 = 5100`, so no real value overflows.
- Mutable default values. A new `int[m + 1][n]` is all zeros, and a zero reads as "reachable for free". Fill every cell with infinity before use. A memo or table kept in a `static` field would also carry stale values from one test case into the next, so allocate it inside `solve`.
- Off-by-one on the key index. Row `i` stands for `i` spelled letters, so the letter just pressed is `key.charAt(i - 1)`. Using `key.charAt(i)` compares the wrong letter and throws a `StringIndexOutOfBoundsException` when `i == m`.
- Letters as list indexes. `ring.charAt(p) - 'a'` is an `int` from `0` to `25`. Forgetting `- 'a'` uses the character code (97 to 122) and throws an `IndexOutOfBoundsException` from `positions.get`.
- Creating the lists. `new List[26]` makes an array of `null` references and a generic array warning, and calling `.add` on one throws a `NullPointerException`. A `List<List<Integer>>` with 26 inner lists created up front avoids both.
- Starting from the wrong position. The ring starts with index `0` at the marker, not with the first copy of the first key letter. Setting the first row to zero at that copy skips the turns needed to reach it.
- Mixing up the table sizes. The table is `(m + 1)` rows by `n` columns: rows follow the key, columns follow the ring.
- Recursion depth. A top-down version goes `m = 100` calls deep, which is safe, but a much longer key would risk a `StackOverflowError`. The bottom-up loops have no depth problem.
- Building long test strings with `"ab".repeat(50)`. `String.repeat` was added in Java 11 and does not compile with `--release 8`. Use a `StringBuilder` and a loop.

## Wrong approaches and why they fail

1. **Greedy: for each key letter, turn to the nearest copy.** The nearest copy can leave the ring far from the next letter. Counterexample: `ring = "ccabaaa"`, `key = "ab"`. The nearest `a` is at index 6, one turn away (2 steps with the press), and the `b` at index 3 is then 3 turns away (4 steps), so greedy answers `6`. Using the `a` at index 2 costs 3 steps, and then the `b` is one turn away (2 steps), a total of `5`. The correct answer is `5`.
2. **Always use the first copy of each letter (`indexOf`).** The first copy can be on the long side of the ring. Counterexample: `ring = "abcdd"`, `key = "d"`. The first `d` is at index 3, which is two turns away the short way round, so the method answers `2 + 1 = 3`. The `d` at index 4 is one turn away, and the correct answer is `2`.
3. **Measure the turn between two positions as `|a - b|`, with no wraparound.** The seam between index `n - 1` and index `0` is ignored. Counterexample: `ring = "abcde"`, `key = "ea"`. The method pays 4 turns to reach the `e` and 4 more to come back, and answers `10`. With wraparound each leg is one turn, and the correct answer is `4`. For `key = "e"` alone it answers `5`, and the correct answer is `2`.
4. **Count only the turns and forget the presses.** Each letter needs one press. Counterexample: `ring = "godding"`, `key = "gd"` gives `2`, and the correct answer is `4`. For `key = "godding"` it gives `6`, and the correct answer is `13`.
5. **Turn in one direction only (always toward higher indexes).** The ring can turn both ways, and the shorter way is often the other one. Counterexample: `ring = "abcde"`, `key = "ea"`. Turning only toward higher indexes costs 4 turns to reach the `e` and 1 turn to come round to the `a`, so the method answers `4 + 1 + 1 + 1 = 7`. The correct answer is `4`.

## Variants

1. **Two rows only.** Row `i` reads only row `i - 1`, so two arrays of length `n` are enough and the space drops to O(n). Keep the full table if you need to recover the turns.
2. **Scan only the previous letter's copies.** Row `i - 1` has real values only at the positions of `key[i - 2]`, so loop over that list instead of all `n` columns. The work drops to the sum, over the key, of (copies of one letter) times (copies of the next).
3. **Top-down with a memo.** Write `f(i, j)` as in the brute force and store results in an `int[m][n]` filled with `-1`. The same `m * n` states are visited, but only the reachable ones.
4. **Recover the sequence of moves.** Store, for every real cell, the `from` position that gave the best value. Walk back from the best cell of row `m`, and turn each hop into "turn clockwise or anticlockwise by this many, then press".
5. **Different costs.** If a turn toward higher indexes and a turn toward lower indexes have different prices, replace `dist(from, to)` by `min(upCost * up, downCost * down)`, where `up = (to - from + n) % n` and `down = (from - to + n) % n`. If the press is free, drop the `+ 1`.
6. **Shortest path in layers.** Every state `(i, j)` is a node, every state in row `i - 1` has edges to the states in row `i`, and the cost of an edge is the turn plus the press. The table is the shortest-path computation on this layered graph, done one layer at a time.
7. **A start other than position 0.** Put the 0 in `dp[0][start]` instead of `dp[0][0]`, and leave every other cell of row `0` at infinity.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `ring="godding", key="gd"` | `4` | first letter already at the top, then a choice between two copies of `d` |
| 2 | `ring="godding", key="godding"` | `13` | spelling the whole ring forward, 6 turns and 7 presses |
| 3 | `ring="a", key="a"` | `1` | smallest input, one press |
| 4 | `ring="ab", key="b"` | `2` | one turn and one press |
| 5 | `ring="abcde", key="ea"` | `4` | wraparound in both directions; a no-wraparound version gives 10 |
| 6 | `ring="abcde", key="aaa"` | `3` | repeated letter, no turns at all |
| 7 | `ring="abcde", key="edcba"` | `10` | walking the whole ring backwards, one turn per letter |
| 8 | `ring="abcd", key="c"` | `3` | exactly opposite position, both directions tie |
| 9 | `ring="ccabaaa", key="ab"` | `5` | the nearest copy of `a` is the wrong one; nearest-copy greedy gives 6 |
| 10 | `ring="abcdd", key="d"` | `2` | the first copy of `d` is not the closest; a first-copy version gives 3 |
| 11 | `ring="aaaaa", key="aaaaa"` | `5` | every position matches, answer is only the presses |
| 12 | `ring=` the pair `ab` fifty times, `key=` the pair `ab` fifty times | `199` | upper size, 99 single turns and 100 presses, 50 copies of each letter |
| 13 | `ring=` 100 times `a`, `key=` 100 times `a` | `100` | upper size, every position matches in every row (most work) |
