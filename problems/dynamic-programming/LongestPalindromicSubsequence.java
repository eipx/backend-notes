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
