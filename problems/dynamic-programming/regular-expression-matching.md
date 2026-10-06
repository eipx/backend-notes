# Regular Expression Matching
`ref: LC 10` · Difficulty: Hard · Pattern: 2-D DP over the prefixes of the string and the pattern, with an extra row and column for the empty prefix; a star means zero copies or one more copy of the preceding element

## Problem

You are given a string `s` and a pattern `p`. Decide whether the pattern matches the whole string. The pattern is built from three kinds of characters. A lowercase letter matches only itself. A dot `.` matches any single character. A star `*` matches zero or more copies of the element right before it, where that element is a letter or a dot. The match has to cover all of `s` and use all of `p`, so a pattern that matches only the beginning of the string does not count.

Input: a string `s` and a pattern `p`.
Output: a single boolean, `true` when `p` matches all of `s`.

## Constraints

- `1 <= s.length <= 20` and `1 <= p.length <= 20`
- `s` contains only lowercase letters `a` to `z`.
- `p` contains only lowercase letters, `.` and `*`.
- Every `*` has an element before it to repeat. So `p` never starts with a star, and a star never follows another star.
- This page's runner also tries an empty string and an empty pattern (cases 6, 7 and 8), because the table has a row and a column for the empty prefix, and the code handles them.
- At a star there are two ways to continue, so a plain recursion can make a huge number of calls even at length 20. A string of nineteen `a` followed by one `b`, matched against ten `a*` groups, makes about fifty million calls. Only `(m + 1) * (n + 1)` different prefix pairs exist (at most `441`), so a table with one cell per pair is fast.

## Worked examples

1. `s = "aa"`, `p = "a"` -> `false`. The pattern covers one character, but the string has two. The match must cover the whole string.
2. `s = "aa"`, `p = "a*"` -> `true`. The star repeats `a` twice.
3. `s = "ab"`, `p = ".*"` -> `true`. A dot matches any character, so `.*` matches any string. The copies do not have to be the same letter.
4. `s = "aab"`, `p = "c*a*b"` -> `true`. `c*` is used zero times, `a*` covers `aa`, and `b` matches `b`.
5. `s = "mississippi"`, `p = "mis*is*p*."` -> `false`. The pattern has to read `m`, `i`, then `ss` for the first `s*`, then `i`, then `ss` for the second `s*`. At that point the next letter is `i`, so `p*` is used zero times and the dot takes the `i`. The pattern is used up, but `ppi` is left over.

## Edge cases checklist

- Both strings empty (the answer is `true`, and the table is a single cell).
- An empty string against a pattern made only of star groups (`a*b*c*`): every group is used zero times, so the answer is `true`.
- An empty string against a pattern with a plain letter in it (`s = ""` and `p = "a"`): the answer is `false`, because the letter needs a character (not among the tests here).
- A non-empty string against an empty pattern (the answer is `false`).
- A star used zero times at the start, in the middle and at the end of the pattern (`c*a*b`, `.*a*a`, `ab*`).
- A star that has to give characters back so that later pattern elements can match (`a*a` against `aaa`).
- `.*` matches everything, including nothing, but the pattern elements after it still have to match (`.*c` against `ab` is `false`).
- A dot always consumes exactly one character (`.*..a*` against `a` is `false`, because the two plain dots need two characters).
- A pattern that matches only a prefix of the string (`a` against `aa`, which is `false`).
- A star on a letter that the string does not contain (`d*` against `abcd` is `false`, because the star cannot cover the rest) (not among the tests here).
- Several star groups on the same letter in a row (`a*a*a*`): the split of the letters between them does not matter, only whether some split works (not among the tests here).
- The upper bound, `20 x 20`, which is `441` cells (not among the tests here).

## Approach

### Brute force

Compare from the left. Let `f(i, j)` say whether the rest of the string from position `i` matches the rest of the pattern from position `j`. If `j` is at the end of the pattern, the answer is whether `i` is at the end of the string. Otherwise, let `first` be true when `i` is inside the string and the pattern character at `j` is a dot or equals the string character at `i`. If the pattern character at `j + 1` is a star, there are two choices: use the group zero times (`f(i, j + 2)`), or use it once more (`first && f(i + 1, j)`, where the same group stays available for the next character). Otherwise the answer is `first && f(i + 1, j + 1)`. Each star splits the search in two, so the number of calls grows very fast with several stars. Only `(m + 1) * (n + 1)` different pairs `(i, j)` exist, and the same pairs are reached over and over, so storing the answers in a table removes the blow-up.

### Optimal

Let `dp[i][j]` be true when the first `i` characters of `s` are matched exactly by the first `j` characters of `p`. The table has `m + 1` rows and `n + 1` columns. The extra row `0` and the extra column `0` stand for the empty prefix:

- `dp[0][0] = true`: the empty string matches the empty pattern.
- `dp[i][0] = false` for `i >= 1`: a non-empty string cannot match an empty pattern. A new boolean array is already false here.
- `dp[0][j]` for `j >= 2`: the empty string can only be matched by a pattern prefix that ends with a star, with that group used zero times, so `dp[0][j] = dp[0][j - 2]` when `p[j - 1]` is a star. Otherwise it is `false`.

For `i >= 1` and `j >= 1`, look at the last pattern character in the prefix, `p[j - 1]`:

- If it is not a star, it has to consume exactly the last string character `s[i - 1]`, and the shorter prefixes must match: `dp[i][j] = dp[i - 1][j - 1]` and (`p[j - 1]` is a dot or equals `s[i - 1]`).
- If it is a star, the element it repeats is the character before it, `p[j - 2]`. There are two cases. Zero copies: the whole group `p[j - 2]` and the star is dropped, which leaves `dp[i][j - 2]`. One more copy: the element absorbs the last string character (`p[j - 2]` is a dot or equals `s[i - 1]`), and the string prefix shrinks by one while the same pattern prefix, star included, stays: `dp[i - 1][j]`. So `dp[i][j]` is true when either case holds.

The answer is `dp[m][n]`. The table is filled row by row, left to right. Each cell reads the cell diagonally up-left, the cell above, or the cell two columns to the left, which are all final by then.

The "one more copy" case keeps the same pattern prefix on purpose. After the star has absorbed one character, it may absorb another, or stop, and that is exactly what the smaller cell `dp[i - 1][j]` decides, so any number of copies is covered by repeating this step.

**Key invariant:** for every cell, `dp[i][j]` is true exactly when the pattern prefix of length `j` matches the whole string prefix of length `i`. A star is never judged alone: it always goes together with the element before it, and the two cases above (drop the pair, or absorb one character and stay) cover every way such a pair can match. The first row and column are correct base cases, and each later cell only reads cells that are already final, so by induction `dp[m][n]` is the answer for the whole input.

### Step-by-step trace

Filled table for `s = "aab"` (rows) and `p = "c*a*b"` (columns). Each column is labeled with the pattern character that was added last. The row and column labeled `""` are the extra empty-prefix ones.

|   | "" | c | * | a | * | b |
|---|---|---|---|---|---|---|
| "" | T | F | T | F | T | F |
| a | F | F | F | T | T | F |
| aa | F | F | F | F | T | F |
| aab | F | F | F | F | F | T |

A few cells worked out. In row `""`, the star at column 2 drops `c*`, so `dp[0][2] = dp[0][0] = T`, and the star at column 4 drops `a*` as well, so `dp[0][4] = dp[0][2] = T`. Then `dp[1][3]` compares the pattern character `a` with the string character `a`, which match, and copies the diagonal `dp[0][2] = T`: that is where using `c*` zero times pays off. For `dp[1][4]`, the star on `a`: zero copies would give `dp[1][2] = F`, but one more copy works because `a` matches `a` and `dp[0][4] = T`, so it is `T`. For `dp[2][4]`, one more copy again: `a` matches the second `a` and `dp[1][4] = T`. Finally `dp[3][5]` compares `b` with `b`, which match, and copies the diagonal `dp[2][4] = T`. The answer is `dp[3][5] = true`.

Walking back from the corner: `(3,5)` came from `(2,4)` by matching `b`; `(2,4)` came from `(1,4)` by absorbing the second `a` into `a*`; `(1,4)` came from `(0,4)` by absorbing the first `a`; and `(0,4)` is true because both star groups are dropped, through `(0,2)` and `(0,0)`. That is worked example 4: `c*` used zero times, `a*` used twice, then `b`.

## Java 8 solution
```java
public class RegularExpressionMatching {

    // True when the pattern p matches the whole string s. In p, '.' matches
    // any single character, and '*' means zero or more copies of the element
    // just before it (a letter or '.'). A '*' always has an element before it.
    //
    // dp[i][j] is true when the first i characters of s are matched exactly by
    // the first j characters of p. The table has one extra row and one extra
    // column so that the empty prefix (i == 0 or j == 0) has a cell.
    public static boolean solve(String s, String p) {
        int m = s.length();
        int n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];

        // Empty string against empty pattern.
        dp[0][0] = true;
        // Empty string against a pattern prefix: only "x*" groups, each used
        // zero times, can match it. When p.charAt(j - 1) is a star, its element
        // sits at j - 2, so dropping the group leaves the prefix of length j - 2.
        for (int j = 2; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 2];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char pc = p.charAt(j - 1);
                if (pc != '*') {
                    // An ordinary letter or '.' consumes exactly one character.
                    dp[i][j] = dp[i - 1][j - 1] && matches(s.charAt(i - 1), pc);
                } else {
                    // The star's element is the character before it, p[j - 2].
                    // Zero copies: drop the whole "x*" group.
                    boolean zeroCopies = dp[i][j - 2];
                    // One more copy: the last character of the string prefix is
                    // absorbed by the element, and the same "x*" stays available.
                    boolean oneMore = matches(s.charAt(i - 1), p.charAt(j - 2)) && dp[i - 1][j];
                    dp[i][j] = zeroCopies || oneMore;
                }
            }
        }
        return dp[m][n];
    }

    private static boolean matches(char sc, char pc) {
        return pc == '.' || pc == sc;
    }

    private static void check(int caseNum, String s, String p, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        boolean got = solve(s, p);
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

        check(1, "aa", "a", false, fail, total);
        check(2, "aa", "a*", true, fail, total);
        check(3, "ab", ".*", true, fail, total);
        check(4, "aab", "c*a*b", true, fail, total);
        check(5, "mississippi", "mis*is*p*.", false, fail, total);
        check(6, "", "", true, fail, total);
        check(7, "", "a*b*c*", true, fail, total);
        check(8, "a", "", false, fail, total);
        check(9, "a", "ab*", true, fail, total);
        check(10, "aaa", "a*a", true, fail, total);
        check(11, "a", ".*..a*", false, fail, total);
        check(12, "bbbba", ".*a*a", true, fail, total);
        check(13, "ab", ".*c", false, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(m * n): every one of the `(m + 1) * (n + 1)` cells is filled once, with constant work per cell. Space O(m * n) for the full table (at most `441` booleans here). A version that keeps only two rows uses O(n) space, since cell `(i, j)` reads only row `i - 1` and row `i`.

## Java 8 pitfalls for this problem

- Reading `charAt(i)` or `charAt(j)` instead of `charAt(i - 1)` and `charAt(j - 1)`. Table index `i` stands for the first `i` characters, so the last one is at position `i - 1`. The wrong index compares the wrong characters and throws a `StringIndexOutOfBoundsException` when it reaches the length.
- Taking the element of a star from `p.charAt(j - 1)`. That is the star itself, which never equals a letter, so the star would only ever allow zero copies, and `"aa"` against `"a*"` would be `false`. The element is `p.charAt(j - 2)`.
- Allocating `new boolean[m][n]` instead of `new boolean[m + 1][n + 1]`. Cell `dp[m][n]` must exist, and the failure is an `ArrayIndexOutOfBoundsException`.
- Forgetting `dp[0][0] = true`. A new `boolean[][]` is all `false`, which is right for every cell except this one, and without it every cell stays `false`.
- Leaving the first row empty. A star group can match nothing, so `dp[0][j]` is not always `false`. Without the loop that fills it, `"aab"` against `"c*a*b"` is reported `false`.
- Starting the first-row loop at `j = 0`, where `p.charAt(j - 1)` is `p.charAt(-1)` and throws a `StringIndexOutOfBoundsException`, or at `j = 1`, where a star would make `dp[0][j - 2]` read column `-1`. A star never sits at pattern position 0, because the constraints promise an element before every star, so starting at `1` happens to be harmless on valid input, but starting at `2` is the form that cannot go wrong. A pattern that started with a star would throw an `ArrayIndexOutOfBoundsException` at `dp[i][j - 2]` in the main loop (when `s` is not empty).
- Comparing the other way round. The dot belongs to the pattern, so test `pc == '.'` on the pattern character only. The string never contains a dot.
- Comparing characters through `String` objects with `==`. That compares references. `charAt` returns a primitive `char`, so `==` on two `charAt` results is correct.
- A top-down recursion goes at most `m + n` frames deep, which is `40` here and safe. With much longer inputs the recursion could overflow the stack, and the table version has no such risk.
- Counting instead of deciding (a variant) would add numbers instead of combining booleans, and the counts can overflow `int`. Use `long`, or take the count modulo something.

## Wrong approaches and why they fail

1. **Let a star take as many characters as it can and never give any back.** Counterexample: `s = "aaa"`, `p = "a*a"`. The star takes all three letters, nothing is left for the final `a`, and the method returns `false`. The correct answer is `true`, with the star taking two letters. For `s = "bbbba"`, `p = ".*a*a"` the same method returns `false`, and the correct answer is `true`.
2. **Treat `x*` as one or more copies.** A star allows zero copies, and several pattern groups rely on that. Counterexample: `s = "aab"`, `p = "c*a*b"`. The group `c*` would need at least one `c`, so the method returns `false`. The correct answer is `true`. For `s = ""`, `p = "a*"` it returns `false`, and the correct answer is `true`.
3. **Leave the first row of the table empty (only `dp[0][0]` is true).** Then no string can ever start by skipping a star group at the front of the pattern. Counterexample: `s = "aa"`, `p = "a*"`. The first `a` can only be absorbed from `dp[0][2]`, which stays `false`, so the method returns `false`. The correct answer is `true`. For `s = "ab"`, `p = ".*"` it returns `false` as well.
4. **Let a star absorb exactly one character, by reading `dp[i - 1][j - 2]` instead of `dp[i - 1][j]` in the "one more copy" case.** That moves past the star group after one copy, and several copies can never chain. Counterexample: `s = "aa"`, `p = "a*"`. The method returns `false`, and the correct answer is `true`. For `s = "aaa"`, `p = "a*a"` it returns `false` too.
5. **Make `.*` repeat the same character.** A dot stands for any single character each time, so the copies may differ. Counterexample: `s = "ab"`, `p = ".*"`. If the dot is fixed to the first character it saw, `a`, the star cannot absorb `b`, and the method returns `false`. The correct answer is `true`.

## Variants

1. **Wildcard Matching (LC 44).** There `?` matches any single character and `*` matches any sequence, and the star stands alone with no element in front. The star cell becomes `dp[i][j] = dp[i][j - 1] || dp[i - 1][j]`.
2. **Two-row version.** Cell `(i, j)` reads only row `i - 1` and row `i`, so two arrays of length `n + 1` are enough and the space drops to O(n).
3. **Top-down with a memo.** Recurse as in the brute force and store each `(i, j)` answer. It has the same O(m * n) worst case, but it only visits the cells that the search actually reaches.
4. **More pattern characters.** To add `x+` (one or more) rewrite it as `xx*`, and to add `x?` (zero or one) allow only the "drop the group" case or a single absorb that moves on to `dp[i - 1][j - 2]`.
5. **Count the ways to match.** Replace the booleans by counts and the `||` by `+`. Different splits of the letters between star groups are then counted separately, and the numbers can overflow `int`.
6. **Simulate the automaton.** Track the set of pattern positions reachable after each string character. It takes the same O(m * n) time but only O(n) space, and it is the form that works when the string arrives as a stream.
7. **Match anywhere inside the string.** If the pattern only has to match some part of `s`, put `.*` at both ends of the pattern and run the same table.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `s="aa", p="a"` | `false` | the pattern covers only part of the string |
| 2 | `s="aa", p="a*"` | `true` | a star repeats a letter (fails if a star allows only zero or one copy) |
| 3 | `s="ab", p=".*"` | `true` | `.*` matches different letters (fails if the dot must repeat the same character) |
| 4 | `s="aab", p="c*a*b"` | `true` | a star used zero times at the front, then twice, then a plain letter (fails if a star needs one copy, or if the first row is empty) |
| 5 | `s="mississippi", p="mis*is*p*."` | `false` | the pattern fits a long prefix and leaves the tail `ppi` unmatched |
| 6 | `s="", p=""` | `true` | both empty |
| 7 | `s="", p="a*b*c*"` | `true` | an empty string matched by star groups used zero times (fails without the first row) |
| 8 | `s="a", p=""` | `false` | a non-empty string against an empty pattern |
| 9 | `s="a", p="ab*"` | `true` | a star at the end of the pattern used zero times |
| 10 | `s="aaa", p="a*a"` | `true` | the star must give a letter back (a greedy star gives false) |
| 11 | `s="a", p=".*..a*"` | `false` | two plain dots need two characters, even though `.*` and `a*` can both be empty |
| 12 | `s="bbbba", p=".*a*a"` | `true` | `.*` covers the b's, `a*` is used zero times, and the last `a` matches (a greedy `.*` gives false) |
| 13 | `s="ab", p=".*c"` | `false` | `.*` cannot supply the required final `c` |
