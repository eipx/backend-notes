public class CoinChangeII {

    // Number of different combinations of coins that add up to exactly amount,
    // with an unlimited supply of every coin value. Two combinations are the
    // same when they use each coin value the same number of times, so 1 + 2
    // and 2 + 1 count once. The problem guarantees that the answer fits in an
    // int.
    //
    // dp[s] is the number of combinations that make sum s using only the coin
    // values processed so far. The empty combination makes sum 0, so dp[0] is 1.
    public static int solve(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;

        // Coins form the outer loop. Each combination is then built in a fixed
        // coin order (some copies of the first coin, then some of the second,
        // and so on), so it is counted exactly once.
        for (int coin : coins) {
            // Walk the sums from low to high. dp[s - coin] has already been
            // updated for this coin, so it includes combinations that use the
            // coin again. That is what allows unlimited copies.
            for (int s = coin; s <= amount; s++) {
                dp[s] += dp[s - coin];
            }
        }
        return dp[amount];
    }

    private static void check(int caseNum, int amount, int[] coins, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(amount, coins);
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

        check(1, 5, new int[]{1, 2, 5}, 4, fail, total);
        check(2, 3, new int[]{2}, 0, fail, total);
        check(3, 10, new int[]{10}, 1, fail, total);
        check(4, 0, new int[]{7}, 1, fail, total);
        check(5, 3, new int[]{1, 2}, 2, fail, total);
        check(6, 4, new int[]{1, 2, 3}, 4, fail, total);
        check(7, 7, new int[]{2, 4}, 0, fail, total);
        check(8, 1, new int[]{2, 3, 5}, 0, fail, total);
        check(9, 10, new int[]{1, 2, 5}, 10, fail, total);
        check(10, 3, new int[]{5, 1, 2}, 2, fail, total);
        check(11, 200, new int[]{1, 2, 5, 10, 20, 50, 100, 200}, 73682, fail, total);
        check(12, 500, new int[]{3, 5, 7, 8, 9, 10, 11}, 35502874, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
