# Edit Distance
`ref: LC 72` · Difficulty: Medium · Pattern: 2-D DP over the prefixes of two strings, with an extra row and column for the empty prefix

## Problem

You are given two strings of lowercase letters, `word1` and `word2`. You may change `word1` one step at a time. A step is one of three operations: insert a single character at any position, delete a single character from any position, or replace one character with a different one. Return the smallest number of steps that turns `word1` into exactly `word2`.

Input: two strings `word1` and `word2`, either of which may be empty.
Output: a single integer, the minimum number of insert, delete and replace steps needed to turn `word1` into `word2`.

## Constraints

- `0 <= word1.length, word2.length <= 500`
- Both strings contain only lowercase letters `a` to `z`.
- At a mismatch there are three ways to continue, so a plain recursion makes up to `3^(m + n)` calls, which is far too slow at length 500. Only `(m + 1) * (n + 1)` different prefix pairs exist (at most `251001`), so a table with one cell per pair is fast.

## Worked examples

1. `word1 = "horse"`, `word2 = "ros"` -> `3`. Replace `h` with `r` (`"rorse"`), delete the second `r` (`"rose"`), delete `e` (`"ros"`).
2. `word1 = "kitten"`, `word2 = "sitting"` -> `3`. Replace `k` with `s` (`"sitten"`), replace `e` with `i` (`"sittin"`), insert `g` at the end (`"sitting"`).
3. `word1 = ""`, `word2 = "abc"` -> `3`. There is nothing to keep, so the answer is three inserts.
4. `word1 = "abcdef"`, `word2 = "azced"` -> `3`. Replace `b` with `z` (`"azcdef"`), delete `d` (`"azcef"`), replace `f` with `d` (`"azced"`).

## Edge cases checklist

- Both strings empty (the answer is 0, and the table is a single cell).
- `word1` empty (the answer is the length of `word2`, all inserts).
- `word2` empty (the answer is the length of `word1`, all deletes).
- Identical strings (the answer is 0, because every step follows the free diagonal).
- Single characters, equal and different (`"a"` to `"b"` is 1).
- The same letters in swapped order (`"ab"` to `"ba"` is 2). There is no swap operation, so a swap costs two steps.
- Very different lengths with repeated letters (`"aaaa"` to `"a"` is 3, three deletes).
- A mix of all three operations in one answer (`"abcdef"` to `"azced"`).
- Symmetry: swapping the two strings gives the same answer, because an insert in one direction is a delete in the other. `"horse"` to `"ros"` and `"ros"` to `"horse"` are both 3, which is a useful self-check on a finished table.
- The upper bound `500 x 500`, about a quarter of a million cells (not among the tests here).

## Approach

### Brute force

Work from the ends of the strings. Let `f(i, j)` be the cost of turning the first `i` characters of `word1` into the first `j` characters of `word2`. If the last characters of the two prefixes are equal, `f(i, j) = f(i - 1, j - 1)`. Otherwise one step is needed, and there are three choices: replace (`f(i - 1, j - 1)`), delete (`f(i - 1, j)`) or insert (`f(i, j - 1)`), so `f(i, j) = 1 + min` of the three. With no memory this makes up to `3^(m + n)` calls. Only `(m + 1) * (n + 1)` different pairs `(i, j)` exist, and the same pairs are reached over and over, so storing the answers in a table removes the blow-up.

### Optimal

Let `dp[i][j]` be the fewest steps that turn the first `i` characters of `word1` into the first `j` characters of `word2`. The table has `m + 1` rows and `n + 1` columns. The extra row `0` and the extra column `0` stand for the empty prefix:

- `dp[0][j] = j`: turning nothing into `j` characters takes `j` inserts.
- `dp[i][0] = i`: turning `i` characters into nothing takes `i` deletes.

For `i >= 1` and `j >= 1`, compare the last characters of the two prefixes, `word1[i - 1]` and `word2[j - 1]`:

- If they are equal, they need no step: `dp[i][j] = dp[i - 1][j - 1]`.
- If they differ, one step is spent and there are three choices. Replace the last character of the `word1` prefix with the last character of the `word2` prefix, which leaves `dp[i - 1][j - 1]`. Delete the last character of the `word1` prefix, which leaves `dp[i - 1][j]`. Insert the last character of the `word2` prefix after the `word1` prefix, which leaves `dp[i][j - 1]`. So `dp[i][j] = 1 + min(dp[i - 1][j - 1], dp[i - 1][j], dp[i][j - 1])`.

The answer is `dp[m][n]`. The table is filled row by row. Each cell reads its upper, left and upper-left neighbors, which are all final by then.

Taking the free diagonal on a match is safe. Two neighboring cells of this table never differ by more than 1 (one extra character costs at most one insert or one delete), so `dp[i - 1][j - 1]` is never larger than `1 + dp[i - 1][j]` or `1 + dp[i][j - 1]`. The match branch cannot lose to the other two.

**Key invariant:** for every cell, `dp[i][j]` is the exact minimum number of steps for that pair of prefixes. The first row and column are correct base cases, and each later cell only reads cells that are already final, so by induction the last cell `dp[m][n]` is the answer for the whole strings.

### Step-by-step trace

Filled table for `word1 = "horse"` (rows) and `word2 = "ros"` (columns). The row and column labeled `""` are the extra empty-prefix ones.

|   | "" | r | o | s |
|---|---|---|---|---|
| "" | 0 | 1 | 2 | 3 |
| h | 1 | 1 | 2 | 3 |
| o | 2 | 2 | 1 | 2 |
| r | 3 | 2 | 2 | 2 |
| s | 4 | 3 | 3 | 2 |
| e | 5 | 4 | 4 | 3 |

A few cells worked out. `dp[2][2]` compares `o` with `o`, which match, so it copies the diagonal `dp[1][1] = 1`. `dp[3][1]` compares `r` with `r`, so it copies `dp[2][0] = 2`. `dp[5][3]` compares `e` with `s`, which differ, so it is `1 + min(dp[4][2] = 3, dp[4][3] = 2, dp[5][2] = 4) = 3`, and the best neighbor is the one above it (a delete). The answer is `dp[5][3] = 3`.

Walking back from the corner shows the steps: `(5,3)` came from `(4,3)` by deleting `e`; `(4,3)` matches `s` with `s` and came from `(3,2)`; `(3,2)` came from `(2,2)` by deleting `r`; `(2,2)` matches `o` with `o` and came from `(1,1)`; `(1,1)` came from `(0,0)` by replacing `h` with `r`. That is the three steps of worked example 1.

## Java 8 solution
```java
public class EditDistance {

    // Fewest single-character edits (insert, delete, replace) that turn word1
    // into word2.
    //
    // dp[i][j] is the fewest edits that turn the first i characters of word1
    // into the first j characters of word2. The table has one extra row and
    // one extra column so that the empty prefix (i == 0 or j == 0) has a cell.
    public static int solve(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Turning i characters into nothing takes i deletes.
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        // Turning nothing into j characters takes j inserts.
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // Table index i covers i characters, so the last one of the
                // prefix is charAt(i - 1).
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int replace = dp[i - 1][j - 1];
                    int delete = dp[i - 1][j];
                    int insert = dp[i][j - 1];
                    dp[i][j] = 1 + Math.min(replace, Math.min(delete, insert));
                }
            }
        }
        return dp[m][n];
    }

    private static void check(int caseNum, String word1, String word2, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(word1, word2);
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

        check(1, "horse", "ros", 3, fail, total);
        check(2, "intention", "execution", 5, fail, total);
        check(3, "", "", 0, fail, total);
        check(4, "", "abc", 3, fail, total);
        check(5, "abc", "", 3, fail, total);
        check(6, "abc", "abc", 0, fail, total);
        check(7, "a", "b", 1, fail, total);
        check(8, "ab", "ba", 2, fail, total);
        check(9, "kitten", "sitting", 3, fail, total);
        check(10, "sunday", "saturday", 3, fail, total);
        check(11, "abcdef", "azced", 3, fail, total);
        check(12, "aaaa", "a", 3, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(m * n): every one of the `(m + 1) * (n + 1)` cells is filled once, with constant work per cell. Space O(m * n) for the full table (at most `251001` ints here). A version that keeps only two rows uses O(n) space, but it cannot rebuild the list of steps (see Variants).

## Java 8 pitfalls for this problem

- Reading `charAt(i)` instead of `charAt(i - 1)`. Table index `i` stands for the first `i` characters, so the last one is at position `i - 1`. Using `charAt(i)` compares the wrong characters and throws a `StringIndexOutOfBoundsException` when `i == m`.
- Not filling the first row and column. A new `int[][]` is all zeros, so `"" -> "abc"` would report 0 instead of 3. Unlike the longest-common-subsequence table, the border here is not zero.
- Allocating `new int[m][n]` instead of `new int[m + 1][n + 1]`. Cell `dp[m][n]` must exist, and the failure is an `ArrayIndexOutOfBoundsException`.
- `Math.min` takes exactly two arguments in Java 8. Three choices need a nested call, `Math.min(a, Math.min(b, c))`.
- Comparing characters through `substring` or `String` objects with `==`. That compares references. `charAt` returns a primitive `char`, so `==` on two `charAt` results is correct.
- Building long test strings with `"a".repeat(500)`. `String.repeat` was added in Java 11 and does not compile with `--release 8`. Use a `StringBuilder` and a loop.
- Assuming which string is longer. The table is `(m + 1)` by `(n + 1)` for any lengths, and the loops must use `m` for rows and `n` for columns.

## Wrong approaches and why they fail

1. **Compare the strings position by position, count the mismatches, and add the difference in length.** This assumes characters can only be lined up by their index, but one insert or delete shifts every character after it. Counterexample: `word1 = "horse"`, `word2 = "ros"`. Position by position there are 2 mismatches (`h` against `r`, and `r` against `s`) plus a length gap of 2, giving `4`. The correct answer is `3`. For `"sunday"` and `"saturday"` the same method gives `7`, and the correct answer is `3`.
2. **Allow only inserts and deletes (no replace), computed as `m + n - 2 * LCS`.** A replace does the work of a delete and an insert in one step. Counterexample: `word1 = "a"`, `word2 = "b"`. The insert-and-delete formula gives `1 + 1 - 0 = 2`. The correct answer is `1`. For `"kitten"` and `"sitting"` (LCS length 4) it gives `6 + 7 - 8 = 5`, and the correct answer is `3`.
3. **Leave the first row and column at zero.** The empty-prefix cells are then wrong, and the error spreads into every cell that reads them. Counterexample: `word1 = ""`, `word2 = "abc"`. The loops never run, and the table returns `0`. The correct answer is `3`. The input `"aaaa"` and `"a"` returns `0` too, and the correct answer is `3`.
4. **Leave one of the three moves out of the recurrence.** With the insert move missing (only replace and delete are tried), `word1 = "kitten"`, `word2 = "sitting"` gives `6`, because the final `g` can never be added, and the correct answer is `3`. With the delete move missing, `word1 = "horse"`, `word2 = "ros"` gives `4`, and the correct answer is `3`.

## Variants

1. **Two-row version.** Cell `(i, j)` only reads row `i - 1` and row `i`, so two arrays of length `n + 1` are enough and the space drops to O(n). Keep the full table if you need to recover the steps.
2. **Recover the list of steps.** Walk back from `dp[m][n]`. At each cell, check which neighbor produced the value: the diagonal means keep (characters equal) or replace, the cell above means delete, and the cell to the left means insert.
3. **Different prices for each operation.** Replace the `1 +` on each of the three branches by that operation's own cost, and set the first row to `j * insertCost` and the first column to `i * deleteCost`.
4. **Delete Operation for Two Strings (LC 583).** Only deletes are allowed, so the answer is `m + n - 2 * LCS`. See `longest-common-subsequence.md`.
5. **Adjacent swap as a fourth operation.** Add a branch `dp[i - 2][j - 2] + 1`, allowed when `word1[i - 1] == word2[j - 2]` and `word1[i - 2] == word2[j - 1]`.
6. **Is the distance at most `k`?** Only cells within `k` of the main diagonal can matter, so the work drops to about `k * min(m, n)`.
7. **Exactly one edit apart (LC 161).** Two pointers decide it in O(n) without any table.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `word1="horse", word2="ros"` | `3` | replace plus two deletes; position-by-position counting gives 4 |
| 2 | `word1="intention", word2="execution"` | `5` | longer mixed case |
| 3 | `word1="", word2=""` | `0` | both strings empty |
| 4 | `word1="", word2="abc"` | `3` | word1 empty, all inserts (fails without the base row) |
| 5 | `word1="abc", word2=""` | `3` | word2 empty, all deletes (fails without the base column) |
| 6 | `word1="abc", word2="abc"` | `0` | identical strings |
| 7 | `word1="a", word2="b"` | `1` | single replace; an insert-and-delete-only formula gives 2 |
| 8 | `word1="ab", word2="ba"` | `2` | a swap costs two steps |
| 9 | `word1="kitten", word2="sitting"` | `3` | two replaces and an insert; the insert-and-delete-only formula gives 5, a table without the insert move gives 6 |
| 10 | `word1="sunday", word2="saturday"` | `3` | two inserts and a replace; position-by-position counting gives 7 |
| 11 | `word1="abcdef", word2="azced"` | `3` | replace, delete and replace in the middle of the strings |
| 12 | `word1="aaaa", word2="a"` | `3` | repeated letters, only deletes needed |
