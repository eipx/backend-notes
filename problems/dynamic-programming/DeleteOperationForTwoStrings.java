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
