# Minimum Inversion Merge

ref: custom (no public reference). Difficulty: hard. Pattern: grid DP over two sequences with prefix counts.

## Problem

Two strings `a` and `b` of lowercase letters are given. Merge them into one string that uses every letter of both, keeping the letters of `a` in their original relative order and the letters of `b` in their original relative order. A conflict is a pair of positions `i < j` in the merged string where the letter at `i` is strictly greater than the letter at `j`. Return the smallest possible number of conflicts.

Equal letters never conflict. The order inside each input string is fixed, so the conflicts that already exist inside `a` and inside `b` cannot be avoided. Only the conflicts between a letter of `a` and a letter of `b` depend on the merge.

## Constraints

- `0 <= a.length, b.length <= 2000`
- letters are `'a'` to `'z'`
- return a `long`

## Worked examples

1. `a = "zdc"`, `b = ""`. The only merge is `"zdc"`. Conflicts: (z,d), (z,c), (d,c). Answer 3. Work this one by hand before writing code, because it fixes the definition of a conflict.
2. `a = "cd"`, `b = "ab"`. Take all of `b` first: `"abcd"` has no conflicts. Answer 0.
3. `a = "ba"`, `b = "ab"`. Inside `a` the pair (b,a) is fixed, which is 1 conflict. The merge `"abab"` (a from `b`, then b from `a`, then a from `a`, then b from `b`) adds no conflict between the two strings. Answer 1.
4. `a = "zyx"`, `b = "abc"`. Inside `a` there are 3 conflicts. Placing all of `b` first adds none. Answer 3.

## Edge cases checklist

- One or both strings empty.
- All letters equal (answer 0).
- Strings already sorted, or sorted in reverse.
- The same letter in both strings (no conflict between them in either order).
- Large input: `n * m` is 4,000,000 states.
- Compute the sample by hand under your reading of the rule before coding. A different reading of "conflict" gives a program that passes a few cases and fails the rest.

## Approach

### Brute force

Try every merge. There are C(n+m, n) of them. Count the conflicts of each. Only usable for total length up to about 12, but it is the reference for testing the fast version.

### Optimal

Split the conflicts into three groups: inside `a`, inside `b`, and across the two strings. The first two are constants. For the across group, use a grid DP.

`dp[i][j]` is the smallest number of across conflicts after placing the first `i` letters of `a` and the first `j` letters of `b`, in any legal order.

Key invariant: when a letter is placed, every letter already placed sits before it. So the across conflicts it creates are exactly the already placed letters of the other string that are strictly greater than it. That number depends only on `(i, j)`, not on the order in which the prefix was built.

- place `a[i-1]` last: `dp[i-1][j] + greaterB[j][a[i-1]]`
- place `b[j-1]` last: `dp[i][j-1] + greaterA[i][b[j-1]]`

`greaterA[i][c]` is how many of the first `i` letters of `a` are strictly greater than letter `c`. Build it from 26 running counts in O(26 * n).

Answer: `dp[n][m] + inversions(a) + inversions(b)`.

### Step-by-step trace

`a = "ba"`, `b = "ab"`. Inside `a`: 1 conflict. Inside `b`: 0.

| state (i,j) | choices | dp |
|---|---|---|
| (0,0) | start | 0 |
| (1,0) | place `b` of `a`, nothing of `b` placed yet | 0 |
| (2,0) | place `a` of `a`, nothing of `b` placed yet | 0 |
| (0,1) | place `a` of `b`, nothing of `a` placed yet | 0 |
| (0,2) | place `b` of `b`, nothing of `a` placed yet | 0 |
| (1,1) | from (0,1): `b` after "a" costs 0. From (1,0): `a` after "b" costs 1 | 0 |
| (1,2) | from (0,2): `b` after "ab" costs 0. From (1,1): `b` after "b" costs 0 | 0 |
| (2,1) | from (1,1): `a` after "a" of `b` costs 0. From (2,0): `a` after "ba" costs 1 | 0 |
| (2,2) | from (1,2): `a` after "ab" of `b` costs 1. From (2,1): `b` after "ba" of `a` costs 0 | 0 |

Across conflicts 0, so the total is 0 + 1 + 0 = 1.

## Java 8 solution

```java
class Solution {
    public long minConflicts(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] gA = greater(a), gB = greater(b);
        long[][] dp = new long[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                if (i == 0 && j == 0) continue;
                long best = Long.MAX_VALUE;
                if (i > 0) best = Math.min(best, dp[i - 1][j] + gB[j][a.charAt(i - 1) - 'a']);
                if (j > 0) best = Math.min(best, dp[i][j - 1] + gA[i][b.charAt(j - 1) - 'a']);
                dp[i][j] = best;
            }
        }
        return dp[n][m] + inversions(a) + inversions(b);
    }

    // g[i][c] = how many of the first i letters of s are strictly greater than letter c
    private int[][] greater(String s) {
        int n = s.length();
        int[][] g = new int[n + 1][26];
        int[] cnt = new int[26];
        for (int i = 0; i <= n; i++) {
            int run = 0;
            for (int c = 25; c >= 0; c--) { g[i][c] = run; run += cnt[c]; }
            if (i < n) cnt[s.charAt(i) - 'a']++;
        }
        return g;
    }

    private long inversions(String s) {
        int[] cnt = new int[26];
        long r = 0;
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i) - 'a';
            for (int k = c + 1; k < 26; k++) r += cnt[k];
            cnt[c]++;
        }
        return r;
    }
}
```

## Complexity

Time O(26 * (n + m) + n * m). Space O(n * m) for the grid, which can be cut to two rows, plus O(26 * (n + m)) for the counts.

## Java 8 pitfalls for this problem

- Use `long` for the counts from the start. Changing the type after a wrong answer costs minutes.
- `s.charAt(i) - 'a'` is an `int`. Do not index an array with the `char` itself.
- Fill row 0 and column 0 inside the same loop. Skipping them leaves zeros that look plausible and are wrong.
- `Long.MAX_VALUE + x` overflows. Only add to a value that came from a real predecessor, as the two `if` branches do.

## Wrong approaches and why they fail

- Reading a conflict as "positions where the two strings differ" and writing a longest common subsequence. Counterexample: `a = "zdc"`, `b = ""`. That reading gives 0. The definition gives 3.
- Greedy, always take the smaller of the two front letters. Counterexample: `a = "caa"`, `b = "b"`. Greedy takes `b` first and builds `"bcaa"` with 4 conflicts. The merge `"caab"` has 3, because `b` should wait behind the small letters hidden after `c`.
- Counting the across conflicts with a loop inside each DP transition. That is O(n * m * (n + m)) and is too slow at 2000 letters. The prefix counts make each transition O(1).

## Variants

- A large alphabet or integers instead of letters: replace the 26 counts with a Fenwick tree over compressed values.
- Return one optimal merged string: keep parent pointers and walk back from `(n, m)`.
- Count the inversions of a single array with merge sort or a Fenwick tree (ref: LC 315, LC 493).

## Test cases

The generated case uses `a[i] = 'a' + (i * 7) % 26` and `b[i] = 'z' - (i * 11) % 26` for `i` from 0 to 1999.

| # | a | b | expected | what it tests |
|---|---|---|---|---|
| 1 | "zdc" | "" | 3 | the definition, conflicts inside one string |
| 2 | "" | "" | 0 | both empty |
| 3 | "a" | "a" | 0 | equal letters never conflict |
| 4 | "ab" | "cd" | 0 | already in order |
| 5 | "cd" | "ab" | 0 | take the second string first |
| 6 | "ba" | "ab" | 1 | fixed conflict inside one string |
| 7 | "zz" | "aa" | 0 | repeated letters |
| 8 | "az" | "za" | 1 | one unavoidable conflict |
| 9 | "dcba" | "dcba" | 18 | both reversed |
| 10 | "aaa" | "aaa" | 0 | all equal |
| 11 | "bca" | "acb" | 4 | mixed order |
| 12 | "zyx" | "abc" | 3 | only inside conflicts remain |
| 13 | "caa" | "b" | 3 | breaks the take-the-smaller greedy |
| 14 | generated, 2000 letters | generated, 2000 letters | 3841316 | size and long arithmetic |
