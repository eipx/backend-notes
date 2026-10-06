# Longest Common Subsequence
`ref: LC 1143` · Difficulty: Medium · Pattern: 2-D DP over the prefixes of two strings, with an extra row and column for the empty prefix

## Problem

Given two strings of lowercase letters, return the length of their longest common subsequence. A common subsequence is a string that can be obtained from each of the two strings by deleting some characters (possibly none) without reordering the rest. The kept characters do not have to be next to each other. If the strings share no character, the answer is `0`.

Input: two strings `text1` and `text2`.
Output: a single integer, the length of the longest string that is a subsequence of both.

## Constraints

- `1 <= text1.length, text2.length <= 1000`
- Both strings contain only lowercase letters `a` to `z`.
- `text1` alone has up to `2^1000` subsequences, so listing them and testing each against `text2` is impossible. A table with one cell per pair of prefixes has at most `1001 * 1001 = 1002001` cells (about 4 MB of ints) and is filled in about a million steps.

## Worked examples

1. `text1 = "abcde"`, `text2 = "ace"` -> `3`. The string `"ace"` is a subsequence of `"abcde"` (drop `b` and `d`), and it is all of `text2`.
2. `text1 = "abc"`, `text2 = "def"` -> `0`. No letter appears in both strings.
3. `text1 = "aaaa"`, `text2 = "aa"` -> `2`. The shorter string is a subsequence of the longer one, and each letter of `text2` can be matched only once.
4. `text1 = "ezupkr"`, `text2 = "ubmrapg"` -> `2`. The subsequences `"ur"` and `"up"` both work. The letters `p` and `r` are shared, but `p` comes before `r` in `text1` and after it in `text2`, so they cannot both be used.

## Edge cases checklist

- No shared letter (`"abc"` and `"def"`, and `"a"` and `"b"`), where the answer is 0.
- Identical strings (the answer is the length).
- Single characters, equal (answer 1) and different (answer 0).
- One string is a subsequence of the other, in both argument orders (`"abcde"` with `"ace"`, and `"ace"` with `"abcde"`). The answer must not depend on the order of the arguments.
- Repeated letters (`"aaaa"` and `"aa"`), where each character of the shorter string can be matched once.
- Shared letters that appear in opposite orders (`"bsbininm"` and `"jmjkbkjkv"` share `b` and `m`, but `m` comes after `b` in one string and before it in the other, so the answer is 1).
- Shared letters that are scattered, not contiguous. A longest-common-substring method would find only 1 for `"abcde"` and `"ace"`.
- A long common part with gaps in the longer string (`"abcba"` and `"abcbcba"`, answer 5).
- A tempting early match that leads to a dead end (`"oxcpqrsvwf"` and `"shmtulqrypy"`: matching `p` first blocks the better pair `q`, `r`).
- The upper bound `1000 x 1000`, about a million cells (not among the tests here). Empty strings are outside the stated bounds, but the table handles them, because its first row and column are already zero.

## Approach

### Brute force

List every subsequence of `text1` (there are `2^m`) and check each against `text2` in `O(n)`, keeping the longest that fits. That is `O(2^m * n)`. A recursion `f(i, j)` on prefixes is shorter to write: if the last characters match it returns `1 + f(i - 1, j - 1)`, otherwise the larger of `f(i - 1, j)` and `f(i, j - 1)`. With no memory it still makes exponentially many calls, but only `(m + 1) * (n + 1)` different pairs `(i, j)` exist, and they are reached again and again. Storing each answer in a table removes the repetition.

### Optimal

Let `dp[i][j]` be the length of the longest common subsequence of the first `i` characters of `text1` and the first `j` characters of `text2`. The table has `m + 1` rows and `n + 1` columns. Row `0` and column `0` stand for the empty prefix, which shares nothing with anything, so they are all `0`. Java starts a new array at `0`, so no loop is needed for them.

For `i >= 1` and `j >= 1`, look at the last characters `text1[i - 1]` and `text2[j - 1]`:

- If they are equal, they can be matched with each other, and the best result is one more than the best for the two shorter prefixes: `dp[i][j] = dp[i - 1][j - 1] + 1`.
- If they differ, at least one of them is not used by the best common subsequence. Either drop the last character of `text1` (`dp[i - 1][j]`) or drop the last character of `text2` (`dp[i][j - 1]`), and keep the better one: `dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])`.

The answer is `dp[m][n]`. The table is filled row by row, and each cell reads its upper, left and upper-left neighbors, which are final by then.

Why a match takes only the diagonal: if the last characters are equal, matching them together never costs anything. If a best common subsequence ended earlier in one of the strings, its last character could be swapped for the final matching pair without making it shorter.

**Key invariant:** `dp[i][j]` is the exact length of the longest common subsequence of the two prefixes. Along every row and every column the values never decrease, and they never rise by more than 1 from one cell to the next. Because each cell only reads cells that are already final, the last cell `dp[m][n]` is the answer for the whole strings.

### Step-by-step trace

Filled table for `text1 = "abcde"` (rows) and `text2 = "ace"` (columns). The row and column labeled `""` are the extra empty-prefix ones.

|   | "" | a | c | e |
|---|---|---|---|---|
| "" | 0 | 0 | 0 | 0 |
| a | 0 | 1 | 1 | 1 |
| b | 0 | 1 | 1 | 1 |
| c | 0 | 1 | 2 | 2 |
| d | 0 | 1 | 2 | 2 |
| e | 0 | 1 | 2 | 3 |

Three cells are matches. `dp[1][1]` compares `a` with `a`, so it is `dp[0][0] + 1 = 1`. `dp[3][2]` compares `c` with `c`, so it is `dp[2][1] + 1 = 2`. `dp[5][3]` compares `e` with `e`, so it is `dp[4][2] + 1 = 3`. Every other cell is a mismatch and copies the larger of its upper and left neighbors; for example `dp[4][3]` compares `d` with `e` and takes `max(dp[3][3] = 2, dp[4][2] = 2) = 2`. The answer is `dp[5][3] = 3`.

## Java 8 solution
```java
public class LongestCommonSubsequence {

    // Length of the longest subsequence that appears in both strings.
    //
    // dp[i][j] is the length of the longest common subsequence of the first i
    // characters of text1 and the first j characters of text2. The table has
    // one extra row and one extra column for the empty prefix; Java fills them
    // with 0, which is the correct value (an empty prefix shares nothing).
    public static int solve(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // Table index i covers i characters, so the last one of the
                // prefix is charAt(i - 1).
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    private static void check(int caseNum, String text1, String text2, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(text1, text2);
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

        check(1, "abcde", "ace", 3, fail, total);
        check(2, "abc", "abc", 3, fail, total);
        check(3, "abc", "def", 0, fail, total);
        check(4, "a", "a", 1, fail, total);
        check(5, "a", "b", 0, fail, total);
        check(6, "aaaa", "aa", 2, fail, total);
        check(7, "abcba", "abcbcba", 5, fail, total);
        check(8, "abcdgh", "aedfhr", 3, fail, total);
        check(9, "ezupkr", "ubmrapg", 2, fail, total);
        check(10, "bsbininm", "jmjkbkjkv", 1, fail, total);
        check(11, "oxcpqrsvwf", "shmtulqrypy", 2, fail, total);
        check(12, "ace", "abcde", 3, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(m * n): every one of the `(m + 1) * (n + 1)` cells is filled once, with constant work per cell. Space O(m * n) for the full table, about 4 MB at the bounds. A version that keeps only two rows uses O(n) space, but it cannot rebuild the subsequence itself (see Variants).

## Java 8 pitfalls for this problem

- Reading `charAt(i)` instead of `charAt(i - 1)`. Table index `i` stands for the first `i` characters, so the last one is at position `i - 1`. Using `charAt(i)` compares the wrong characters and throws a `StringIndexOutOfBoundsException` when `i == m`.
- Allocating `new int[m][n]` instead of `new int[m + 1][n + 1]`. Cell `dp[m][n]` must exist, and the failure is an `ArrayIndexOutOfBoundsException`.
- Returning `dp[m - 1][n - 1]`. That ignores the last character of each string. The answer is in the corner `dp[m][n]`.
- Setting the border by hand and getting it wrong. In this table the empty-prefix row and column must be `0`, which is what Java already puts there. Do not copy the border loops from the edit-distance table, where the border is `i` and `j`.
- Comparing characters through `substring` or `String` objects with `==`. That compares references. `charAt` returns a primitive `char`, so `==` on two `charAt` results is correct.
- Building long test strings with `"a".repeat(1000)`. `String.repeat` was added in Java 11 and does not compile with `--release 8`. Use a `StringBuilder` and a loop.
- A top-down memoized version recurses up to `m + n` frames deep (2000 at the bounds). That usually fits in the default stack, but the bottom-up loops avoid the question.

## Wrong approaches and why they fail

1. **Find the longest common substring (contiguous) by resetting the cell to 0 on a mismatch.** A subsequence may skip characters, so the best common part is often not contiguous. Counterexample: `text1 = "abcde"`, `text2 = "ace"`. The longest common substring has length `1`. The correct answer is `3` (`"ace"`).
2. **Count the distinct letters that appear in both strings.** This ignores order. Counterexample: `text1 = "ezupkr"`, `text2 = "ubmrapg"`. The letters `u`, `p` and `r` are shared, so this approach says `3`, but `p` and `r` come in opposite orders in the two strings, and the correct answer is `2`. For `"bsbininm"` and `"jmjkbkjkv"` it says `2` (`b` and `m`), and the correct answer is `1`.
3. **On a mismatch, carry only the diagonal cell `dp[i - 1][j - 1]` (drop both last characters).** That throws away matches that are still available on one side. Counterexample: `text1 = "abcde"`, `text2 = "ace"`. The approach ends with `1`, and the correct answer is `3`. Swapping the arguments (`"ace"`, `"abcde"`) also gives `1`.
4. **Greedy: for each letter of `text1` in order, match its first occurrence in `text2` after the previous match.** An early match can use up the part of `text2` that a better match needs. Counterexample: `text1 = "oxcpqrsvwf"`, `text2 = "shmtulqrypy"`. The letter `p` matches at position 9 of `text2`, and then no later letter of `text1` appears after it, so the greedy answer is `1`. The correct answer is `2` (`"qr"`).

## Variants

1. **Two-row version.** Cell `(i, j)` only reads row `i - 1` and row `i`, so two arrays of length `n + 1` are enough and the space drops to O(n). Keep the full table if you need to rebuild the subsequence.
2. **Print one longest common subsequence.** Walk back from `dp[m][n]`. If the two characters are equal, take the character and move diagonally. Otherwise move to the neighbor (above or left) with the larger value. The characters are collected in reverse order.
3. **Longest common substring.** Set `dp[i][j] = 0` on a mismatch, and take the largest cell anywhere in the table as the answer.
4. **Delete Operation for Two Strings (LC 583).** Only deletes are allowed, so the answer is `m + n - 2 * LCS`. It is also the insert-and-delete-only special case of `edit-distance.md`.
5. **Shortest Common Supersequence (LC 1092).** Its length is `m + n - LCS`. The string itself is built by walking back through the table.
6. **Longest Palindromic Subsequence (LC 516).** It equals the LCS of the string and its reverse.
7. **Three strings.** Add a third index, `dp[i][j][k]`, with the same match-or-drop rule.
8. **Two permutations of the same numbers.** The LCS reduces to a longest increasing subsequence, which the `O(n log n)` form in `longest-increasing-subsequence.md` solves.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `text1="abcde", text2="ace"` | `3` | second string is a subsequence of the first; a longest-common-substring method gives 1 |
| 2 | `text1="abc", text2="abc"` | `3` | identical strings |
| 3 | `text1="abc", text2="def"` | `0` | no shared letter |
| 4 | `text1="a", text2="a"` | `1` | single equal characters |
| 5 | `text1="a", text2="b"` | `0` | single different characters |
| 6 | `text1="aaaa", text2="aa"` | `2` | repeated letters; the shorter string is a subsequence |
| 7 | `text1="abcba", text2="abcbcba"` | `5` | shorter string is a subsequence with gaps in the longer one |
| 8 | `text1="abcdgh", text2="aedfhr"` | `3` | scattered match `a`, `d`, `h` |
| 9 | `text1="ezupkr", text2="ubmrapg"` | `2` | shared `p` and `r` in opposite orders; counting shared letters gives 3 |
| 10 | `text1="bsbininm", text2="jmjkbkjkv"` | `1` | shared `b` and `m` in opposite orders; counting shared letters gives 2 |
| 11 | `text1="oxcpqrsvwf", text2="shmtulqrypy"` | `2` | the earliest match (`p`) is a dead end; greedy matching gives 1 |
| 12 | `text1="ace", text2="abcde"` | `3` | case 1 with the arguments swapped; the answer is symmetric |
