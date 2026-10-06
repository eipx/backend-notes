public class StoneGame {

    // Does the first player (Alice) win a game on a row of piles, when both
    // players play perfectly? On each turn the player to move takes the whole
    // pile at the left end or at the right end. The player with more stones at
    // the end wins, so a tie is not a win.
    //
    // dp[i][j] is the best score difference (stones of the player to move minus
    // stones of the other player) that the player to move can force on the
    // piles i..j. After taking a pile, the roles swap, so what the opponent
    // forces on the rest is subtracted.
    public static boolean solve(int[] piles) {
        int n = piles.length;
        if (n == 0) {
            return false;
        }
        int[][] dp = new int[n][n];

        // One pile left: the player to move takes it.
        for (int i = 0; i < n; i++) {
            dp[i][i] = piles[i];
        }

        // Fill by the length of the range, so that both smaller ranges that a
        // cell reads are already final.
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                int takeLeft = piles[i] - dp[i + 1][j];
                int takeRight = piles[j] - dp[i][j - 1];
                dp[i][j] = Math.max(takeLeft, takeRight);
            }
        }
        return dp[0][n - 1] > 0;
    }

    private static void check(int caseNum, int[] piles, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        boolean got = solve(piles);
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

        check(1, new int[]{5, 3, 4, 5}, true, fail, total);
        check(2, new int[]{3, 7, 2, 3}, true, fail, total);
        check(3, new int[]{1, 2}, true, fail, total);
        check(4, new int[]{2, 1}, true, fail, total);
        check(5, new int[]{1, 1, 3, 2}, true, fail, total);
        check(6, new int[]{4, 100, 3, 2}, true, fail, total);
        check(7, new int[]{6, 1, 1, 1, 1, 7}, true, fail, total);

        // 500 piles: 499 piles of 500 stones and a last pile of 499 (the
        // largest input the limits allow, with an odd total).
        int[] big = new int[500];
        for (int i = 0; i < big.length; i++) {
            big[i] = 500;
        }
        big[499] = 499;
        check(8, big, true, fail, total);

        check(9, new int[]{}, false, fail, total);
        check(10, new int[]{7}, true, fail, total);
        check(11, new int[]{2, 2}, false, fail, total);
        check(12, new int[]{1, 100, 1}, false, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
