# Word Break
`ref: LC 139` · Difficulty: Medium · Pattern: 1-D DP over prefixes with a hash set of words; dp[i] = the first i characters can be segmented

## Problem

You are given a string `s` and a list of words `wordDict`. Decide whether `s` can be cut into one or more pieces so that every piece is a word from `wordDict`. The pieces must cover `s` completely, from the first character to the last, in order, with no gaps and no overlaps. A word may be used as many times as you like, and words that are never used are fine.

Input: a string `s` and a list of strings `wordDict`.
Output: `true` if such a cut exists, otherwise `false`.

## Constraints

- `1 <= s.length <= 300`
- `1 <= wordDict.length <= 1000`
- `1 <= wordDict[i].length <= 20`
- `s` and every word contain only lowercase letters `a` to `z`.
- All words in `wordDict` are distinct.
- A plain recursion that tries every word at every position can make exponentially many calls. On test 12 (thirty `a` characters followed by `b`) it makes more than 400 million. Only `n + 1` different start positions exist (at most `301`), so a table with one cell per position is fast.

## Worked examples

1. `s = "leetcode"`, `wordDict = ["leet", "code"]` -> `true`. The pieces are `"leet"` and `"code"`.
2. `s = "applepenapple"`, `wordDict = ["apple", "pen"]` -> `true`. The pieces are `"apple"`, `"pen"`, `"apple"`. The word `"apple"` is used twice, which is allowed.
3. `s = "catsandog"`, `wordDict = ["cats", "dog", "sand", "and", "cat"]` -> `false`. The last word would have to end at the final `g`, and only `"dog"` does. It would need `"catsan"` in front of it, and `"catsan"` cannot be cut (`"cat"` then `"san"`, or `"cats"` then `"an"`, both fail).
4. `s = "cars"`, `wordDict = ["car", "ca", "rs"]` -> `true`. The pieces are `"ca"` and `"rs"`. Taking the longer word `"car"` first leaves only `"s"`, which is not a word.
5. `s = "abcd"`, `wordDict = ["a", "abc", "b", "cd"]` -> `true`. The pieces are `"a"`, `"b"`, `"cd"`. Taking `"abc"` first leaves `"d"`, which is not a word.

## Edge cases checklist

- The smallest string, one character, matching (`"a"` with `["a"]` is `true`) and not matching (`"a"` with `["b"]` is `false`).
- A word longer than the string, or longer than the part still left (`"ab"` with `["abc", "b"]` is `false`). The substring end must never go past the end of `s`.
- A word that matches at the right end while the part in front of it cannot be cut (in `"ab"` the word `"b"` fits the last character, but `"a"` is not a word).
- Words that share a prefix, so the first match can be a dead end (`"cars"` with `["car", "ca", "rs"]`: `"car"` leads nowhere and `"ca"` works).
- The longest word is not always the right first piece (`"abcd"` with `["a", "abc", "b", "cd"]`), and the shortest is not always right either (`"abcd"` with `["ab", "abc", "d"]`).
- The same word used more than once (`"aaaaaaa"` with `["aaaa", "aaa"]` is `4 + 3`).
- A string made of one repeated letter, where the number of ways to cut it explodes but the answer needs only a linear number of table cells (test 12).
- A letter that no word contains, so the answer is `false` however the rest is cut (the `b` in test 12).
- Odd and even lengths with only even-length words (`"aaaaaaa"` with `["aa", "aaaa"]` is `false`).
- The upper bound, 300 characters with the longest allowed word (test 13).
- An empty `s` is not allowed by the constraints. The table would answer `true` for it, because zero words cover zero characters.

## Approach

### Brute force

Let `canBreak(start)` say whether the part of `s` from index `start` to the end can be cut into words. If `start == n`, nothing is left, so the answer is `true`. Otherwise try every end `end > start`: if `s.substring(start, end)` is a word and `canBreak(end)` is `true`, the answer is `true`. With no memory, the same `start` is reached again and again by different ways of cutting the front. For `n` letters `a` followed by `b` with the words `a` and `aa`, the number of calls grows like the Fibonacci numbers: 232 calls at `n = 10`, 3,524,577 at `n = 30`, and 433,494,436 at `n = 40`. Only `n + 1` different values of `start` exist, so storing each answer removes the blow-up.

### Optimal

Let `dp[i]` be `true` when the first `i` characters of `s` can be cut into dictionary words. The array has `n + 1` entries. Entry `0` stands for the empty prefix:

- `dp[0] = true`: zero words cover zero characters.

For `i >= 1`, the prefix of length `i` ends with some last word. Say that word starts at index `j`, so it is `s.substring(j, i)` and has `i - j` characters. Then the front part `s[0..j)` must be cuttable and the last word must be in the dictionary:

- `dp[i] = true` if some `j` with `0 <= j < i` has `dp[j] == true` and `s.substring(j, i)` in the set. Otherwise `dp[i] = false`.

The answer is `dp[n]`. The array is filled from left to right. Each entry reads only smaller indices, which are final by then.

The words are copied into a hash set, so "is this piece a word" is one lookup instead of a scan of the whole list. No word is longer than `maxLen`, the length of the longest word, so the inner loop only tries `j` with `i - j <= maxLen`. It also stops at the first success, since `dp[i]` can only go from `false` to `true`.

**Key invariant:** after index `i` has been handled, `dp[i]` is `true` exactly when the first `i` characters are a concatenation of dictionary words. `dp[0]` is correct by definition. Every cut of a prefix has a last word, and the loop tries every possible last word, so no cut is missed. Each entry reads only entries that are already final, so by induction `dp[n]` is the answer for the whole string.

### Step-by-step trace

`s = "cars"`, `wordDict = ["car", "ca", "rs"]`, so `maxLen = 3`. For each `i` the loop tries `j = i - 1` down to `0`, and stops at the first success.

| i | j | piece `s[j..i)` | `dp[j]` | piece in set | `dp[i]` |
|---|---|---|---|---|---|
| 0 | - | (empty) | - | - | `true` (base case) |
| 1 | 0 | `c` | `true` | no | `false` |
| 2 | 1 | `a` | `false` | skipped | `false` so far |
| 2 | 0 | `ca` | `true` | yes | `true` |
| 3 | 2 | `r` | `true` | no | `false` so far |
| 3 | 1 | `ar` | `false` | skipped | `false` so far |
| 3 | 0 | `car` | `true` | yes | `true` |
| 4 | 3 | `s` | `true` | no | `false` so far |
| 4 | 2 | `rs` | `true` | yes | `true` |

The answer is `dp[4] = true`. Notice that `dp[3]` is `true` (the piece `"car"`), yet `dp[4]` does not come from it: the piece `"s"` is not a word. It comes from `dp[2]` and the piece `"rs"`. A method that commits to `"car"` early gets stuck, and the table keeps both options alive.

Walking back from the end: `dp[4]` was set by the piece `"rs"` starting at index 2; `dp[2]` was set by the piece `"ca"` starting at index 0. The cut is `"ca"` and `"rs"`, as in worked example 4.

## Java 8 solution
```java
import java.util.*;

public class WordBreak {

    // True when s can be cut into one or more pieces, each of which is a word
    // from wordDict. A word may be used any number of times.
    //
    // dp[i] is true when the first i characters of s can be segmented. The
    // table has n + 1 entries so that i == 0 (the empty prefix) has a cell.
    public static boolean solve(String s, List<String> wordDict) {
        int n = s.length();
        Set<String> words = new HashSet<String>(wordDict);

        // No word is longer than maxLen, so a last piece longer than that can
        // never match and does not need to be looked up.
        int maxLen = 0;
        for (String w : words) {
            maxLen = Math.max(maxLen, w.length());
        }

        boolean[] dp = new boolean[n + 1];
        // The empty prefix is segmented by zero words.
        dp[0] = true;

        for (int i = 1; i <= n; i++) {
            // The last word of the prefix is s.substring(j, i): it starts at
            // index j and ends before index i, so it has i - j characters.
            for (int j = i - 1; j >= 0 && i - j <= maxLen; j--) {
                if (dp[j] && words.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[n];
    }

    // Java 8 has no String.repeat, so build repeated text with a loop.
    private static String rep(String unit, int times) {
        StringBuilder sb = new StringBuilder();
        for (int t = 0; t < times; t++) {
            sb.append(unit);
        }
        return sb.toString();
    }

    private static void check(int caseNum, String s, String[] words, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        boolean got = solve(s, Arrays.asList(words));
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

        check(1, "leetcode", new String[]{"leet", "code"}, true, fail, total);
        check(2, "applepenapple", new String[]{"apple", "pen"}, true, fail, total);
        check(3, "catsandog", new String[]{"cats", "dog", "sand", "and", "cat"}, false, fail, total);
        check(4, "a", new String[]{"a"}, true, fail, total);
        check(5, "a", new String[]{"b"}, false, fail, total);
        check(6, "ab", new String[]{"abc", "b"}, false, fail, total);
        check(7, "cars", new String[]{"car", "ca", "rs"}, true, fail, total);
        check(8, "abcd", new String[]{"a", "abc", "b", "cd"}, true, fail, total);
        check(9, "abcd", new String[]{"ab", "abc", "d"}, true, fail, total);
        check(10, "aaaaaaa", new String[]{"aaaa", "aaa"}, true, fail, total);
        check(11, "aaaaaaa", new String[]{"aa", "aaaa"}, false, fail, total);
        check(12, rep("a", 30) + "b", new String[]{"a", "aa", "aaa", "aaaa"}, false, fail, total);
        check(13, rep("a", 300), new String[]{rep("a", 20)}, true, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n * L) lookups, where `n = s.length` and `L` is the length of the longest word (at most 20): each index `i` tries at most `L` pieces. Building and hashing a piece costs O(L), so the total is O(n * L^2), at most about `300 * 20 * 20 = 120000` character operations. Without the `maxLen` cap the loop would try every `j < i`, which is O(n^2) lookups and O(n^3) work, still small at `n = 300` but wasteful. Space O(n + W): the `n + 1` booleans, plus the set, which holds the `W` characters of the words (at most `1000 * 20 = 20000`).

## Java 8 pitfalls for this problem

- Off-by-one on the prefix index. `dp[i]` stands for the first `i` characters, so the last piece is `s.substring(j, i)`, and the end index is exclusive. Writing `substring(j, i + 1)` shifts every comparison and throws a `StringIndexOutOfBoundsException` when `i == n`.
- Forgetting `dp[0] = true`. A new `boolean[]` is all `false`, so no entry can ever become `true`, and even `"a"` with `["a"]` returns `false`.
- Sizing the array as `new boolean[n]`. Entry `dp[n]` must exist, and the failure is an `ArrayIndexOutOfBoundsException`.
- Integer overflow if you turn the question into counting. The yes or no answer needs only booleans, but the number of ways to cut `n` letters `a` with the words `a` and `aa` is the `(n + 1)`-th Fibonacci number, and it passes `2147483647` at `n = 46`. Use `long` or a remainder for a counting version.
- Recursion depth. A top-down version goes as deep as `n`, which is 300 here and safe, but a much longer string would risk a `StackOverflowError`. The bottom-up loop has no depth problem.
- Mutable and shared state. A memo kept in a `static` field survives between calls, so a second test case would read answers left by the first. Allocate the table inside `solve`. A `boolean[]` starts as all `false`, which is the right value for every cell except index `0`.
- `Arrays.asList(...)` returns a fixed-size list backed by the array, and `wordDict.contains(piece)` on a list scans every word. Copy the words into a `HashSet` once and do not change the caller's list.
- Comparing a piece with a word using `==`. That compares references, and it is `false` for equal text built by `substring`. `HashSet.contains` and `String.equals` compare the characters and are correct.
- Building long test strings with `"a".repeat(300)`. `String.repeat` was added in Java 11 and does not compile with `--release 8`. Use a `StringBuilder` and a loop.

## Wrong approaches and why they fail

1. **Greedy: at each position take the longest word that matches.** A longer word can swallow letters that a later word needed. Counterexample: `s = "abcd"`, `wordDict = ["a", "abc", "b", "cd"]`. Greedy takes `"abc"`, is left with `"d"`, which is not a word, and answers `false`. The correct answer is `true` (`"a"`, `"b"`, `"cd"`). The same happens for `"cars"` with `["car", "ca", "rs"]`.
2. **Greedy: at each position take the shortest word that matches.** The mirror image fails the same way. Counterexample: `s = "abcd"`, `wordDict = ["ab", "abc", "d"]`. Greedy takes `"ab"`, is left with `"cd"`, and answers `false`. The correct answer is `true` (`"abc"`, `"d"`).
3. **Delete dictionary words from `s` over and over and check whether nothing is left.** Deleting a word in the middle joins the two parts around it, and the joined text can look like a word even though no cut of the original string produces it. Counterexample: `s = "aabb"`, `wordDict = ["ab"]`. Deleting the middle `"ab"` leaves `"ab"`, and deleting again leaves nothing, so the method answers `true`. The correct answer is `false`, because the first two characters `"aa"` cannot be a piece. The method also depends on the order of deletion: for `"cars"` with `["car", "ca", "rs"]`, deleting `"car"` first leaves `"s"` and the method answers `false`, while the correct answer is `true`.
4. **Leave `dp[0]` as `false`.** The first piece of any cut starts at index `0` and reads `dp[0]`, so every entry stays `false`. Counterexample: `s = "a"`, `wordDict = ["a"]` returns `false`, and the correct answer is `true`. `"leetcode"` with `["leet", "code"]` returns `false` too.

## Variants

1. **Word Break II (LC 140).** Return every sentence, not just a yes or no. Use backtracking with a memo per start index. The number of sentences can be exponential, so the size of the output, not the table, decides the running time.
2. **Count the ways to cut `s`.** Set `dp[0] = 1` and add `dp[i] += dp[j]` for every valid `j`. Use `long` or a remainder, because the count overflows `int` (see the pitfalls).
3. **Fewest words.** Make `dp[i]` the smallest number of words for the prefix, with a large value for "impossible", and take `dp[i] = min(dp[j] + 1)` over valid `j`. Guard the large value before adding 1.
4. **A trie instead of a hash set.** From every reachable `j`, walk the trie one letter at a time and stop when there is no edge. No substring is built, and each start index costs at most `L` steps.
5. **The same problem as reachability.** Make each index `0..n` a node, with an edge from `j` to `i` when `s[j..i)` is a word. The answer is whether node `n` can be reached from node `0`. A queue and a visited array give the same bound.
6. **Concatenated Words (LC 472).** Find the words in a list that are built from other words in the same list. Run this table once per word, with that word left out of the set.
7. **Extra Characters in a String (LC 2707).** Letters outside every piece are allowed, and the goal is to leave as few as possible: `dp[i] = min(dp[i - 1] + 1, dp[j])` over pieces `s[j..i)` that are words.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `s="leetcode", wordDict=["leet","code"]` | `true` | two words, the basic case |
| 2 | `s="applepenapple", wordDict=["apple","pen"]` | `true` | the same word used twice |
| 3 | `s="catsandog", wordDict=["cats","dog","sand","and","cat"]` | `false` | words match everywhere but the tail `"og"` cannot be covered |
| 4 | `s="a", wordDict=["a"]` | `true` | smallest string, matching (fails without `dp[0] = true`) |
| 5 | `s="a", wordDict=["b"]` | `false` | smallest string, no match |
| 6 | `s="ab", wordDict=["abc","b"]` | `false` | a word longer than the string, and a last word whose front part is not cuttable |
| 7 | `s="cars", wordDict=["car","ca","rs"]` | `true` | shared prefix; longest-first greedy takes `"car"` and fails |
| 8 | `s="abcd", wordDict=["a","abc","b","cd"]` | `true` | longest-first greedy takes `"abc"` and fails |
| 9 | `s="abcd", wordDict=["ab","abc","d"]` | `true` | shortest-first greedy takes `"ab"` and fails |
| 10 | `s="aaaaaaa", wordDict=["aaaa","aaa"]` | `true` | words of different lengths that must be mixed (`4 + 3`) |
| 11 | `s="aaaaaaa", wordDict=["aa","aaaa"]` | `false` | odd length against even-length words |
| 12 | `s=` thirty `a` then `b`, `wordDict=["a","aa","aaa","aaaa"]` | `false` | exponentially many cuts of the front, plain recursion makes over 400 million calls |
| 13 | `s=` 300 times `a`, `wordDict=[` 20 times `a` `]` | `true` | upper bound on length and on word length |
