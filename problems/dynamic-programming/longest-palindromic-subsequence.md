# Longest Palindromic Subsequence
`ref: LC 516` · Difficulty: Medium · Pattern: interval DP on one string, where `dp[i][j]` covers the characters from position `i` to position `j`; equal ends add 2 to the inside, otherwise take the better of dropping one end

## Problem

You are given a string `s` of lowercase letters. A palindrome reads the same forward and backward. A subsequence is what remains after deleting some characters (possibly none) without reordering the rest, so the kept characters do not have to be next to each other. Return the length of the longest subsequence of `s` that is a palindrome.

Input: a string `s`.
Output: a single integer, the length of the longest palindromic subsequence of `s`.

## Constraints

- `1 <= s.length <= 1000`
- `s` contains only lowercase letters `a` to `z`.
- A string of length `n` has `2^n` subsequences, so testing each one for the palindrome property is impossible at length 1000. A plain recursion on the two ends makes up to `2^n` calls. Only `n * (n + 1) / 2` different intervals exist (at most `500500`), so a table with one cell per interval is fast.

## Worked examples

1. `s = "bbbab"` -> `4`. The subsequence `"bbbb"` skips the `a`. A palindrome of length 5 would have to be the whole string, which is not one.
2. `s = "cbbd"` -> `2`. The subsequence `"bb"`. The two ends `c` and `d` are not used at all.
3. `s = "agbdba"` -> `5`. The subsequence `"abdba"` skips the `g`. The longest palindromic substring (contiguous) is only `"bdb"`, length 3.
4. `s = "abc"` -> `1`. No letter repeats, so the best palindrome is a single character.

## Edge cases checklist

- A single character (answer 1), the smallest input.
- Two equal characters (`"aa"` gives 2). The inside of the interval is empty, and the answer relies on the empty interval counting as 0.
- Two different characters (`"ab"` gives 1).
- All characters different (`"abc"` gives 1).
- The whole string is a palindrome, with even and odd length (`"aaaa"` gives 4 and `"racecar"` gives 7). The answer is the length of the string.
- A palindrome that needs characters skipped in the middle (`"agbdba"` gives 5, and `"abacdfgdcaba"` gives 11, where one of `f` and `g` is picked as the middle letter).
- The best palindrome is not contiguous (`"bbbab"` gives 4, while the longest palindromic substring is 3).
- The two ends differ but one of them belongs to the answer (`"abcb"` gives 3 because the right end `b` is used), and the case where neither end is used (`"cbbd"` gives 2).
- Letters that repeat but cannot nest (`"aabb"` gives 2, although each letter appears twice). Counting letter pairs ignores the order and says 4.
- An odd-length answer has a single middle character, and an even-length answer has none. Both are produced by the same recurrence.
- A cheap self-check: reversing the string does not change the answer, because a subsequence is a palindrome exactly when its reverse is.
- The empty string is outside the stated bounds. The code returns 0 for it before allocating the table.
- The upper bound `n = 1000`, a table of a million cells of which about half are used (not among the tests here).

## Approach

### Brute force

List every subsequence of `s` (there are `2^n`) and test each for the palindrome property in `O(n)`, keeping the longest. That is `O(n * 2^n)`. A recursion on intervals is shorter to write. Let `f(i, j)` be the answer for the characters from position `i` to position `j`. If `i > j` the interval is empty and `f = 0`. If `i == j` it is one character and `f = 1`. If `s[i] == s[j]` then `f(i, j) = 2 + f(i + 1, j - 1)`. Otherwise `f(i, j) = max(f(i + 1, j), f(i, j - 1))`. With no memory, each mismatch makes two calls, so up to `2^n` calls are made. Only `n * (n + 1) / 2` different intervals `(i, j)` exist, and the same ones are reached over and over, so storing the answers in a table removes the blow-up.

### Optimal

Let `dp[i][j]` be the length of the longest palindromic subsequence of the part of `s` from position `i` to position `j`, both ends included, for `i <= j`. Here `i` and `j` are positions of characters, not prefix lengths, so the table is `n` by `n`. A cell below the diagonal (`i > j`) stands for the empty interval and holds `0`. Java starts a new array at `0`, so those cells need no loop. The diagonal is the base case: `dp[i][i] = 1`, because one character is a palindrome.

For `i < j`, look at the two ends `s[i]` and `s[j]`:

- If they are equal, put them around the best palindrome of the inside: `dp[i][j] = dp[i + 1][j - 1] + 2`. When `j == i + 1` the inside is empty, and `dp[i + 1][i]` is a below-the-diagonal cell, so the formula gives `0 + 2 = 2`.
- If they differ, the two ends cannot be the outer pair of one palindrome, so at least one of them is not used. Drop the left end (`dp[i + 1][j]`) or drop the right end (`dp[i][j - 1]`), and keep the better one: `dp[i][j] = max(dp[i + 1][j], dp[i][j - 1])`.

The answer is `dp[0][n - 1]`. Every cell reads cells of a strictly shorter interval: the row below (`i + 1`) and its own left neighbor (`j - 1`). So the rows are filled from the last one up to the first (`i` from `n - 1` down to `0`), and each row from left to right (`j` from `i + 1` to `n - 1`). Filling the rows from the top would read cells that are still zero.

Why equal ends can both be used: suppose `s[i] == s[j]` and take any palindromic subsequence `Q` of the interval. If `Q` has at most one character, the two ends alone are a longer palindrome. If `Q` uses neither end, wrap it with the two ends and it gets 2 longer. If `Q` has two or more characters and uses only one end, say `s[i]` as its first character, then its last character is the same letter as `s[i]`, and that last character can be moved to position `j` (the same letter, further right) without breaking the order. So some best palindrome always uses both ends, and the rest of it is a palindrome of the inside.

**Key invariant:** for every cell with `i <= j`, `dp[i][j]` is the exact length of the longest palindromic subsequence of the interval from `i` to `j`, and the empty interval holds 0. The diagonal is a correct base case, and each later cell only reads shorter intervals that are already final, so by induction `dp[0][n - 1]` is the answer for the whole string.

### Step-by-step trace

Filled table for `s = "bbbab"`. Rows are the left end `i`, columns are the right end `j`, and only the cells with `i <= j` are used. The blank cells below the diagonal are the empty intervals, which hold 0.

|   | j=0 (b) | j=1 (b) | j=2 (b) | j=3 (a) | j=4 (b) |
|---|---|---|---|---|---|
| i=0 (b) | 1 | 2 | 3 | 3 | 4 |
| i=1 (b) | | 1 | 2 | 2 | 3 |
| i=2 (b) | | | 1 | 1 | 3 |
| i=3 (a) | | | | 1 | 1 |
| i=4 (b) | | | | | 1 |

The rows were filled from the bottom (`i = 4`) to the top. A few cells worked out. `dp[3][4]` compares `a` with `b`, which differ, so it is `max(dp[4][4] = 1, dp[3][3] = 1) = 1`. `dp[2][4]` compares `b` with `b`, which match, so it is `dp[3][3] + 2 = 3`. `dp[1][2]` compares `b` with `b`, which match, with an empty inside, so it is `dp[2][1] + 2 = 0 + 2 = 2`. `dp[1][3]` compares `b` with `a`, which differ, so it is `max(dp[2][3] = 1, dp[1][2] = 2) = 2`. The last cell `dp[0][4]` compares `b` with `b`, which match, so it is `dp[1][3] + 2 = 4`.

Walking back from the corner shows the palindrome. `(0,4)` has equal ends, so the first and the last `b` are used, and the inside is `(1,3)`. The ends of `(1,3)` differ and the better neighbor is `(1,2)`, which drops the `a`. `(1,2)` has equal ends, so the second and third `b` are used. The kept letters are the `b` at positions 0, 1, 2 and 4, which is `"bbbb"`, the four of worked example 1.

## Java 8 solution
```java
public class LongestPalindromicSubsequence {

    // Length of the longest palindromic subsequence of s.
    //
    // dp[i][j] is the length of the longest palindromic subsequence of the
    // substring s[i..j] (both ends included), for i <= j. Cells below the
    // diagonal (i > j) stand for the empty interval and stay 0.
    public static int solve(String s) {
        int n = s.length();
        if (n == 0) {
            // Outside the stated bounds, but cheap to answer: nothing to keep.
            return 0;
        }
        int[][] dp = new int[n][n];

        // Row i reads row i + 1 (already final) and its own cell to the left,
        // so i runs from the last index down to 0 and j runs left to right.
        for (int i = n - 1; i >= 0; i--) {
            // A single character is a palindrome of length 1.
            dp[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    // Equal ends wrap the best palindrome of the inside. When
                    // j == i + 1 the inside is empty, and dp[i + 1][i] is a
                    // below-the-diagonal cell that holds 0.
                    dp[i][j] = dp[i + 1][j - 1] + 2;
                } else {
                    // Different ends: at most one of them is used.
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[0][n - 1];
    }

    private static void check(int caseNum, String s, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(s);
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

        check(1, "bbbab", 4, fail, total);
        check(2, "cbbd", 2, fail, total);
        check(3, "a", 1, fail, total);
        check(4, "aa", 2, fail, total);
        check(5, "ab", 1, fail, total);
        check(6, "abc", 1, fail, total);
        check(7, "aaaa", 4, fail, total);
        check(8, "racecar", 7, fail, total);
        check(9, "agbdba", 5, fail, total);
        check(10, "abcb", 3, fail, total);
        check(11, "character", 5, fail, total);
        check(12, "abacdfgdcaba", 11, fail, total);
        check(13, "aabb", 2, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n^2): about `n * (n - 1) / 2` cells are filled (at most `499500`, plus `1000` on the diagonal), with constant work per cell. Space O(n^2) for the full table (`1000000` ints, about 4 MB, of which only the upper half is used). A version that keeps a single row uses O(n) space, but it cannot rebuild the palindrome (see Variants).

## Java 8 pitfalls for this problem

- Reading `dp[i + 1][j - 1]` when `j == i + 1`. That cell is below the diagonal and must hold `0`. Do not fill the table with `-1` or any other marker, or the answer for every adjacent equal pair comes out wrong. A top-down version needs the explicit base case `i > j` returning `0`.
- Filling `i` upward from `0`. Row `i` reads row `i + 1`, which would still be all zeros, so the code gives wrong answers with no exception at all. The rows must run from `n - 1` down to `0`.
- Treating `i` as a prefix length. In the two-string tables of this folder, index `i` means "the first `i` characters" and the table has an extra row and column. Here `i` and `j` are positions, the table is `n` by `n`, and the answer is `dp[0][n - 1]`, not `dp[n][n]` or `dp[n - 1][n - 1]` (that one is just 1).
- Array sizing for an empty string. `new int[0][0]` is legal, but reading `dp[0][n - 1]` then throws an `ArrayIndexOutOfBoundsException`. The input bounds exclude the empty string, and the guard at the top of `solve` makes it return `0` anyway.
- Mutable default values. A memoized recursion can use `0` for "not computed yet" only because every non-empty interval has an answer of at least `1`, and only if the empty interval (`i > j`) returns before the lookup. A static memo reused between calls keeps the values of the previous string, so allocate it inside the method.
- Integer overflow is not a risk. The largest value is 1000, so `int` is plenty. Avoid `Integer.MAX_VALUE` as a "not computed" marker in a table that later has `2` added to its values, because the sum wraps to a negative number.
- A top-down memoized version recurses up to `n` frames deep (1000 at the bounds), because every call shrinks the interval by at least one. That usually fits in the default stack, but the bottom-up loops avoid the question.
- Building long test strings with `"a".repeat(1000)`. `String.repeat` was added in Java 11 and does not compile with `--release 8`. Use a `StringBuilder` and a loop.
- Comparing characters through `substring` or `String` objects with `==`. That compares references. `charAt` returns a primitive `char`, so `==` on two `charAt` results is correct.

## Wrong approaches and why they fail

1. **Find the longest palindromic substring (contiguous).** A subsequence may skip characters, so the best palindrome is often not contiguous. Counterexample: `s = "bbbab"`. The longest palindromic substring is `"bbb"`, length `3`. The correct answer is `4`. For `"agbdba"` the substring answer is `3` and the correct answer is `5`.
2. **Count the letters in pairs: add up the even part of every letter count, and add 1 if any count is odd.** This ignores order. Counterexample: `s = "aabb"`. Each letter appears twice, so this approach says `4`, but the two `a` and the two `b` cannot nest, and the correct answer is `2`. For `"bbbab"` it says `5` (four `b`, plus the single `a` in the middle), and the correct answer is `4`.
3. **On a mismatch, drop both ends: `dp[i][j] = dp[i + 1][j - 1]`.** That throws away an end that may belong to the answer. Counterexample: `s = "ab"`. The ends differ, the inside is empty, and the approach returns `0`. The correct answer is `1`. For `"abcb"` it drops `a` and `b`, then `b` and `c`, and ends with `0`, and the correct answer is `3` (`"bcb"`). For `"bbbab"` it answers `3`, and the correct answer is `4`.
4. **On a mismatch, always drop the left end.** The better choice depends on the string, and one branch is not enough. Counterexample: `s = "cbbd"`. Dropping `c` leaves `"bbd"`, dropping `b` leaves `"bd"`, and dropping `b` again leaves `"d"`, so the approach answers `1`. The correct answer is `2` (`"bb"`), which needs the right end dropped first. For `"bbbab"` it answers `3`, and the correct answer is `4`.

## Variants

1. **Reverse and compare.** The answer equals the longest common subsequence of `s` and the reverse of `s`. That reuses the two-string table of `longest-common-subsequence.md`, with the same `O(n^2)` time and space.
2. **One-row version.** Row `i` only reads row `i + 1` and its own left neighbor, so a single array of length `n` plus one saved diagonal value is enough and the space drops to O(n). Keep the full table if you need to rebuild the palindrome.
3. **Recover the palindrome.** Walk back from `dp[0][n - 1]` as in the trace. Equal ends: take both characters and move to `(i + 1, j - 1)`. Different ends: move to the neighbor, `(i + 1, j)` or `(i, j - 1)`, with the larger value. Collect the character of every equal-ends step in order. If the walk ends on a single character, that one is the middle. The collected characters, then the middle character if there is one, then the collected characters in reverse, make the palindrome.
4. **Minimum Insertion Steps to Make a String Palindrome (LC 1312).** The answer is `n` minus the longest palindromic subsequence. Every character outside the kept palindrome needs one inserted partner.
5. **Valid Palindrome III (LC 1216).** Can the string become a palindrome after deleting at most `k` characters? Yes exactly when `n` minus the longest palindromic subsequence is at most `k`.
6. **Longest Palindromic Substring (LC 5).** The contiguous version. Expand around each of the `2n - 1` centers, for `O(n^2)` time and `O(1)` space.
7. **Delete Operation for Two Strings (LC 583).** Another problem that comes down to the longest common subsequence. See `delete-operation-for-two-strings.md`.
8. **Count Different Palindromic Subsequences (LC 730).** Counts them instead of measuring the longest, with the same interval table but a harder rule for repeated letters.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `s="bbbab"` | `4` | first example from the statement; `"bbbb"` skips the `a`; the longest palindromic substring is only 3, counting letter pairs gives 5, always dropping the left end gives 3 |
| 2 | `s="cbbd"` | `2` | second example from the statement; `"bb"`; the ends differ and neither end is used; always dropping the left end gives 1 |
| 3 | `s="a"` | `1` | smallest input, one character |
| 4 | `s="aa"` | `2` | two equal characters; the inside is empty, so this reads the zero below the diagonal |
| 5 | `s="ab"` | `1` | two different characters; a "drop both ends" rule gives 0 |
| 6 | `s="abc"` | `1` | all characters different; any single character |
| 7 | `s="aaaa"` | `4` | the whole string is a palindrome, even length |
| 8 | `s="racecar"` | `7` | the whole string is a palindrome, odd length |
| 9 | `s="agbdba"` | `5` | `"abdba"` skips the `g`; the longest palindromic substring is only 3 |
| 10 | `s="abcb"` | `3` | the ends differ but the right end is part of the answer (`"bcb"`); a "drop both ends" rule gives 0 |
| 11 | `s="character"` | `5` | mixed case with gaps (`"carac"`); counting letter pairs gives 7 |
| 12 | `s="abacdfgdcaba"` | `11` | long case with a one-letter middle taken from `f` or `g`; a "drop both ends" rule gives 10 |
| 13 | `s="aabb"` | `2` | each letter appears twice but the pairs cannot nest; counting letter pairs gives 4 |
