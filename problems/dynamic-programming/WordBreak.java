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
