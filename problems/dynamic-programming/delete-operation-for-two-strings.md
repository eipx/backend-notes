# Delete Operation for Two Strings
`ref: LC 583` · Difficulty: Medium · Pattern: longest common subsequence (2-D DP over the prefixes of two strings); the answer is `m + n - 2 * LCS`

## Problem

You are given two strings of lowercase letters, `word1` and `word2`. In one step you delete exactly one character from either string. Return the smallest number of steps that makes the two strings equal. Deleting every character of both strings is allowed (two empty strings are equal), so an answer always exists.

Input: two strings `word1` and `word2`.
Output: a single integer, the minimum number of deletes (from either string) after which the two strings are identical.

## Constraints

- `1 <= word1.length, word2.length <= 500`
- Both strings contain only lowercase letters `a` to `z`.
- `word1` alone has up to `2^500` subsequences, so listing the ways to shrink it and testing each against `word2` is impossible. A plain recursion on the last characters makes up to `2^(m + n)` calls. Only `(m + 1) * (n + 1)` different prefix pairs exist (at most `251001`), so a table with one cell per pair is fast.

## Worked examples

1. `word1 = "sea"`, `word2 = "eat"` -> `2`. Delete `s` from `"sea"` and delete `t` from `"eat"`. Both become `"ea"`.
2. `word1 = "leetcode"`, `word2 = "etco"` -> `4`. The string `"etco"` is already a subsequence of `"leetcode"`, so only `word1` loses characters: delete `l`, one `e`, `d` and the last `e`.
3. `word1 = "abc"`, `word2 = "def"` -> `6`. No letter is shared, so nothing can be kept. All three characters of each string are deleted and both end up empty.
4. `word1 = "ab"`, `word2 = "ba"` -> `2`. Only one letter can stay. Delete `a` from `"ab"` and delete `a` from `"ba"`, and both become `"b"`.

## Edge cases checklist

- No shared letter (`"abc"` and `"def"` give 6, and `"a"` and `"b"` give 2). Every character goes.
- Identical strings (the answer is 0).
- Single characters, equal (answer 0) and different (answer 2). A replace would fix the different pair in one step in `edit-distance.md`, but replace is not allowed here.
- One string is a subsequence of the other, in both argument orders (`"abcde"` with `"ace"`, and `"ace"` with `"abcde"`). The answer is the difference in length, and it must not depend on the order of the arguments.
- Repeated letters (`"aaaa"` and `"aa"`), where each kept letter of the shorter string is matched once.
- The same letters in swapped order (`"ab"` and `"ba"` give 2, and `"abcd"` and `"dcba"` give 6). All the letters are shared, but only one of them can be kept.
- A tempting early match that spoils a longer one (`"abcd"` and `"bcda"`: matching the `a` first leaves nothing for `b`, `c`, `d`, and the answer is 2, not 6).
- Scattered matches that are not next to each other (`"leetcode"` and `"etco"`).
- Two cheap self-checks on a finished answer. It is never smaller than the difference in length, and it has the same parity (odd or even) as `m + n`, because it equals `m + n` minus an even number.
- Empty strings are outside the stated bounds, but the table handles them: its first row and column are already zero, so the answer is the length of the other string.
- The upper bound `500 x 500`, about a quarter of a million cells (not among the tests here). The answer is at most `1000`.

## Approach

### Brute force

Let `f(i, j)` be the fewest deletes that make the first `i` characters of `word1` equal to the first `j` characters of `word2`. If the last characters of the two prefixes are equal, they can both stay: `f(i, j) = f(i - 1, j - 1)`. Otherwise one of the two last characters has to go, and there are two choices: `f(i, j) = 1 + min(f(i - 1, j), f(i, j - 1))`. The base cases are `f(i, 0) = i` and `f(0, j) = j`. With no memory, each mismatch makes two calls and the depth can reach `m + n`, so up to `2^(m + n)` calls are made. Another brute force lists the `2^m` subsequences of `word1`, checks each against `word2` in `O(n)`, and keeps the longest one that fits, for `O(2^m * n)`. Only `(m + 1) * (n + 1)` different pairs `(i, j)` exist, and the same pairs are reached over and over, so storing the answers in a table removes the blow-up.

### Optimal

Look at what is left once all the deletes are done. The two remaining strings are equal, so that string is a subsequence of `word1` (it is `word1` with some characters deleted) and also a subsequence of `word2`. It is a common subsequence. Going the other way, any common subsequence of length `L` can be reached by deleting everything else, which costs `m - L` deletes in `word1` and `n - L` deletes in `word2`. A kept string of length `L` therefore costs exactly `m + n - 2L` deletes, and fewer deletes means a larger `L`. So the answer is `m + n - 2 * LCS`, where `LCS` is the length of the longest common subsequence of the two strings.

Let `dp[i][j]` be the length of the longest common subsequence of the first `i` characters of `word1` and the first `j` characters of `word2`. The table has `m + 1` rows and `n + 1` columns. Row `0` and column `0` stand for the empty prefix, which shares nothing with anything, so they are all `0`. Java starts a new array at `0`, so no loop is needed for them.

For `i >= 1` and `j >= 1`, look at the last characters `word1[i - 1]` and `word2[j - 1]`. There are three choices:

- Keep both. This is only possible when they are equal, and then `dp[i][j] = dp[i - 1][j - 1] + 1`.
- Delete the last character of the `word1` prefix: `dp[i - 1][j]`.
- Delete the last character of the `word2` prefix: `dp[i][j - 1]`.

When the characters are equal, keeping both is taken. When they differ, the best of the two deletes is taken: `dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])`.

The answer is `m + n - 2 * dp[m][n]`. The table is filled row by row. Each cell reads its upper, left and upper-left neighbors, which are all final by then.

Why a match takes only the diagonal: if the last characters are equal, keeping them as a pair never costs anything. If a best common subsequence ended earlier in one of the strings, its last character could be swapped for the final matching pair without making it shorter.

The two views agree on every cell. For any pair of prefixes the fewest deletes is `i + j - 2 * dp[i][j]`, which is exactly what the recursion `f(i, j)` above computes. The table only needs the `LCS` form because its border is all zeros.

**Key invariant:** for every cell, `dp[i][j]` is the exact length of the longest common subsequence of that pair of prefixes, and `i + j - 2 * dp[i][j]` is the exact fewest deletes for that pair. The first row and column are correct base cases, and each later cell only reads cells that are already final, so by induction the last cell `dp[m][n]` gives the answer for the whole strings.

### Step-by-step trace

Filled table for `word1 = "sea"` (rows) and `word2 = "eat"` (columns). The row and column labeled `""` are the extra empty-prefix ones.

|   | "" | e | a | t |
|---|---|---|---|---|
| "" | 0 | 0 | 0 | 0 |
| s | 0 | 0 | 0 | 0 |
| e | 0 | 1 | 1 | 1 |
| a | 0 | 1 | 2 | 2 |

A few cells worked out. `dp[1][1]` compares `s` with `e`, which differ, so it is `max(dp[0][1], dp[1][0]) = 0`. `dp[2][1]` compares `e` with `e`, which match, so it is `dp[1][0] + 1 = 1`. `dp[3][2]` compares `a` with `a`, which match, so it is `dp[2][1] + 1 = 2`. `dp[3][3]` compares `a` with `t`, which differ, so it is `max(dp[2][3] = 1, dp[3][2] = 2) = 2`, and the better neighbor is the one to its left. The longest common subsequence has length `dp[3][3] = 2`, so the answer is `3 + 3 - 2 * 2 = 2`.

Walking back from the corner shows what is kept. `(3,3)` came from `(3,2)` by deleting `t`; `(3,2)` matches `a` with `a` and came from `(2,1)`; `(2,1)` matches `e` with `e` and came from `(1,0)`. The kept string is `"ea"`. The characters off that path are the deletes: the `s` of `word1` and the `t` of `word2`. That is the two steps of worked example 1.

## Java 8 solution
```java
public class DeleteOperationForTwoStrings {

    // Fewest single-character deletes (from either string) that make word1 and
    // word2 equal.
    //
    // Whatever is left after the deletes is a common subsequence of the two
    // strings, so keeping the longest common subsequence (LCS) means deleting
    // the fewest characters: (m - lcs) from word1 and (n - lcs) from word2.
    //
    // dp[i][j] is the LCS length of the first i characters of word1 and the
    // first j characters of word2. The table has one extra row and one extra
    // column for the empty prefix; Java fills them with 0, which is the
    // correct value (an empty prefix shares nothing).
    public static int solve(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // Table index i covers i characters, so the last one of the
                // prefix is charAt(i - 1).
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        int lcs = dp[m][n];
        return m + n - 2 * lcs;
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

        check(1, "sea", "eat", 2, fail, total);
        check(2, "leetcode", "etco", 4, fail, total);
        check(3, "a", "a", 0, fail, total);
        check(4, "a", "b", 2, fail, total);
        check(5, "abc", "abc", 0, fail, total);
        check(6, "abc", "def", 6, fail, total);
        check(7, "abcde", "ace", 2, fail, total);
        check(8, "ace", "abcde", 2, fail, total);
        check(9, "ab", "ba", 2, fail, total);
        check(10, "aaaa", "aa", 2, fail, total);
        check(11, "abcd", "dcba", 6, fail, total);
        check(12, "abcd", "bcda", 2, fail, total);
        check(13, "intention", "execution", 8, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(m * n): every one of the `(m + 1) * (n + 1)` cells is filled once, with constant work per cell. Space O(m * n) for the full table (at most `251001` ints here, about 1 MB). A version that keeps only two rows uses O(n) space, but it cannot rebuild the list of deleted characters (see Variants).

## Java 8 pitfalls for this problem

- Returning `dp[m][n]` directly. The table holds the length of the longest common subsequence, not the number of deletes. The answer is `m + n - 2 * dp[m][n]`, and returning the table value gives the number of characters kept.
- Writing `m + n - dp[m][n]`, which subtracts the common part once. The kept characters disappear from both strings, so they are subtracted twice. `"abc"` against `"abc"` would give 3 instead of 0.
- Reading `charAt(i)` instead of `charAt(i - 1)`. Table index `i` stands for the first `i` characters, so the last one is at position `i - 1`. Using `charAt(i)` compares the wrong characters and throws a `StringIndexOutOfBoundsException` when `i == m`.
- Allocating `new int[m][n]` instead of `new int[m + 1][n + 1]`. Cell `dp[m][n]` must exist, and the failure is an `ArrayIndexOutOfBoundsException`.
- Mutable default values. A new `int[][]` starts at all zeros, which is the correct border for this table. It is the wrong border for the direct delete-count table (`dp[i][0] = i` and `dp[0][j] = j`, as in `edit-distance.md`). In a memoized recursion, a zero cannot tell "not computed yet" from "the common subsequence is empty", so fill the memo with `-1`. Allocate the table inside the method; a static table reused between calls keeps the values of the previous strings.
- Integer overflow is not a risk here. The largest table value is 500 and the largest answer is 1000, so `int` is plenty. The overflow trap is in the direct delete-count table when it uses `Integer.MAX_VALUE` as a "too big" start: `1 + Integer.MAX_VALUE` wraps to a negative number and wins every `min`.
- A top-down memoized version recurses up to `m + n` frames deep (1000 at the bounds). That usually fits in the default stack, but the bottom-up loops avoid the question.
- Building long test strings with `"a".repeat(500)`. `String.repeat` was added in Java 11 and does not compile with `--release 8`. Use a `StringBuilder` and a loop.
- Comparing characters through `substring` or `String` objects with `==`. That compares references. `charAt` returns a primitive `char`, so `==` on two `charAt` results is correct.

## Wrong approaches and why they fail

1. **Run the edit-distance table and use its answer.** Edit distance also allows replace, and a replace does in one step what two deletes do here. Counterexample: `word1 = "a"`, `word2 = "b"`. Edit distance gives `1`. The correct answer is `2`, because replacing is not allowed and both letters must be deleted. For `"abc"` and `"abd"` it gives `1`, and the correct answer is `2`.
2. **Count the letters the two strings share and delete the rest.** This ignores order. Counterexample: `word1 = "abcd"`, `word2 = "dcba"`. Every letter is shared, so this approach deletes nothing and answers `0`, but the two strings are not equal. The correct answer is `6`. For `"ab"` and `"ba"` it answers `0`, and the correct answer is `2`.
3. **Greedy: for each character of `word1` in order, keep it if it occurs in `word2` after the previous kept character, and delete it otherwise.** An early match can use up the part of `word2` that a better match needs. Counterexample: `word1 = "abcd"`, `word2 = "bcda"`. The `a` of `word1` is kept at the last position of `word2`, and then `b`, `c`, `d` have nothing after it, so only 1 character is kept and the answer comes out as `4 + 4 - 2 = 6`. The correct answer is `2` (keep `"bcd"`, delete the first `a` of `word1` and the last `a` of `word2`).
4. **Subtract the longest common subsequence once, `m + n - LCS`.** The kept characters are missing from both strings, so the saving is counted twice. Counterexample: `word1 = "sea"`, `word2 = "eat"`. The longest common subsequence has length 2, and the formula gives `6 - 2 = 4`. The correct answer is `2`. For two identical strings of length 3 it gives `3`, and the correct answer is `0`.

## Variants

1. **Two-row version.** Cell `(i, j)` only reads row `i - 1` and row `i`, so two arrays of length `n + 1` are enough and the space drops to O(n). Keep the full table if you need to list the deletes.
2. **Direct delete-count table.** Skip the `LCS` step: `dp[i][0] = i`, `dp[0][j] = j`, a match copies the diagonal, and a mismatch takes `1 + min(dp[i - 1][j], dp[i][j - 1])`. It gives the same answer, and its border is not zero.
3. **Recover which characters are deleted.** Walk back from `dp[m][n]`. A match moves diagonally and keeps the character. Otherwise move to the neighbor (above or left) with the larger value. The characters of `word1` and `word2` that were not on the path are the deletes.
4. **Edit Distance (LC 72).** Adds replace as a third operation, so the three-way minimum appears. See `edit-distance.md`. This problem is its insert-and-delete-only special case, which is why `"a"` to `"b"` costs 2 here and 1 there.
5. **Minimum ASCII Delete Sum for Two Strings (LC 712).** Each deleted character costs its character code. The answer is the total of all codes minus twice the largest code sum of a common subsequence, so the same table works if a match adds the code of the character instead of 1.
6. **Shortest Common Supersequence (LC 1092).** Its length is `m + n - LCS`. The string itself is built by walking back through the table.
7. **Longest Common Subsequence (LC 1143).** The table of this problem is that problem's table. See `longest-common-subsequence.md`.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `word1="sea", word2="eat"` | `2` | first LeetCode example; delete `s` and `t`, keep `"ea"`; subtracting the LCS only once gives 4 |
| 2 | `word1="leetcode", word2="etco"` | `4` | second LeetCode example; `word2` is a subsequence of `word1`, so only `word1` loses characters |
| 3 | `word1="a", word2="a"` | `0` | smallest input, equal characters |
| 4 | `word1="a", word2="b"` | `2` | smallest input, different characters; an edit-distance table (replace allowed) gives 1 |
| 5 | `word1="abc", word2="abc"` | `0` | identical strings |
| 6 | `word1="abc", word2="def"` | `6` | no shared letter; every character is deleted from both strings |
| 7 | `word1="abcde", word2="ace"` | `2` | `word2` is a subsequence of `word1`; the answer is the difference in length |
| 8 | `word1="ace", word2="abcde"` | `2` | case 7 with the arguments swapped; the answer is symmetric |
| 9 | `word1="ab", word2="ba"` | `2` | same letters in swapped order; only one of them can be kept |
| 10 | `word1="aaaa", word2="aa"` | `2` | repeated letters; the shorter string is a subsequence |
| 11 | `word1="abcd", word2="dcba"` | `6` | all letters shared but in opposite order; counting shared letters gives 0 |
| 12 | `word1="abcd", word2="bcda"` | `2` | the earliest match (`a`) is a dead end; greedy matching gives 6 |
| 13 | `word1="intention", word2="execution"` | `8` | longer mixed case; the longest common subsequence is `"etion"`, length 5 |
