public class EditDistance {

    // Fewest single-character edits (insert, delete, replace) that turn word1
    // into word2.
    //
    // dp[i][j] is the fewest edits that turn the first i characters of word1
    // into the first j characters of word2. The table has one extra row and
    // one extra column so that the empty prefix (i == 0 or j == 0) has a cell.
    public static int solve(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Turning i characters into nothing takes i deletes.
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        // Turning nothing into j characters takes j inserts.
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // Table index i covers i characters, so the last one of the
                // prefix is charAt(i - 1).
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int replace = dp[i - 1][j - 1];
                    int delete = dp[i - 1][j];
                    int insert = dp[i][j - 1];
                    dp[i][j] = 1 + Math.min(replace, Math.min(delete, insert));
                }
            }
        }
        return dp[m][n];
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

        check(1, "horse", "ros", 3, fail, total);
        check(2, "intention", "execution", 5, fail, total);
        check(3, "", "", 0, fail, total);
        check(4, "", "abc", 3, fail, total);
        check(5, "abc", "", 3, fail, total);
        check(6, "abc", "abc", 0, fail, total);
        check(7, "a", "b", 1, fail, total);
        check(8, "ab", "ba", 2, fail, total);
        check(9, "kitten", "sitting", 3, fail, total);
        check(10, "sunday", "saturday", 3, fail, total);
        check(11, "abcdef", "azced", 3, fail, total);
        check(12, "aaaa", "a", 3, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
