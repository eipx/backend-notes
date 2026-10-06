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
