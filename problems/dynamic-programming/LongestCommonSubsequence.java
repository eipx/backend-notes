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
