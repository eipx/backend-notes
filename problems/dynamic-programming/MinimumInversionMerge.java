// Minimum Inversion Merge
// ref: custom (no public reference)
// Merge two lowercase strings, keeping each string's own letter order, so that the
// number of pairs (i < j) with merged[i] > merged[j] is as small as possible.
// Study page: minimum-inversion-merge.md
// Run: javac --release 8 MinimumInversionMerge.java && java MinimumInversionMerge

class Solution {
    public long minConflicts(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] gA = greater(a), gB = greater(b);
        long[][] dp = new long[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                if (i == 0 && j == 0) continue;
                long best = Long.MAX_VALUE;
                // place a[i-1] last: it conflicts with every placed letter of b that is greater
                if (i > 0) best = Math.min(best, dp[i - 1][j] + gB[j][a.charAt(i - 1) - 'a']);
                // place b[j-1] last: it conflicts with every placed letter of a that is greater
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

public class MinimumInversionMerge {

    private static void check(int caseNum, String a, String b, long expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            long got = new Solution().minConflicts(a, b);
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": threw " + e);
        }
    }

    private static String generated(boolean first) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            sb.append(first ? (char) ('a' + (i * 7) % 26) : (char) ('z' - (i * 11) % 26));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int[] fail = new int[1], total = new int[1];
        check(1, "zdc", "", 3L, fail, total);
        check(2, "", "", 0L, fail, total);
        check(3, "a", "a", 0L, fail, total);
        check(4, "ab", "cd", 0L, fail, total);
        check(5, "cd", "ab", 0L, fail, total);
        check(6, "ba", "ab", 1L, fail, total);
        check(7, "zz", "aa", 0L, fail, total);
        check(8, "az", "za", 1L, fail, total);
        check(9, "dcba", "dcba", 18L, fail, total);
        check(10, "aaa", "aaa", 0L, fail, total);
        check(11, "bca", "acb", 4L, fail, total);
        check(12, "zyx", "abc", 3L, fail, total);
        check(13, "caa", "b", 3L, fail, total);
        check(14, generated(true), generated(false), 3841316L, fail, total);
        System.out.println((total[0] - fail[0]) + "/" + total[0] + " passed");
        if (fail[0] > 0) System.exit(1);
    }
}
