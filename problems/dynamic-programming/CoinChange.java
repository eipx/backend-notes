public class CoinChange {

    // Fewest coins that add up to exactly amount, using as many copies of each
    // coin value as needed. Returns -1 when no combination of coins reaches
    // amount.
    //
    // dp[a] is the fewest coins that add up to exactly a, or amount + 1 when a
    // cannot be made. A reachable amount never needs more than amount coins
    // (the worst case is all coins of value 1), so amount + 1 is larger than
    // every real answer. It can stand for "unreachable" and still be safe to
    // add 1 to without overflowing.
    public static int solve(int[] coins, int amount) {
        int unreachable = amount + 1;
        int[] dp = new int[amount + 1];
        for (int a = 1; a <= amount; a++) {
            dp[a] = unreachable;
        }
        // Zero coins make amount 0. This is the only base case.
        dp[0] = 0;

        for (int a = 1; a <= amount; a++) {
            for (int coin : coins) {
                // Test coin <= a before subtracting, so a - coin is a valid
                // index. Coin values can be as large as Integer.MAX_VALUE.
                if (coin <= a) {
                    dp[a] = Math.min(dp[a], dp[a - coin] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    private static void check(int caseNum, int[] coins, int amount, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(coins, amount);
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

        check(1, new int[]{1, 2, 5}, 11, 3, fail, total);
        check(2, new int[]{2}, 3, -1, fail, total);
        check(3, new int[]{1}, 0, 0, fail, total);
        check(4, new int[]{1}, 1, 1, fail, total);
        check(5, new int[]{1}, 2, 2, fail, total);
        check(6, new int[]{1, 3, 4}, 6, 2, fail, total);
        check(7, new int[]{1, 5, 6, 9}, 11, 2, fail, total);
        check(8, new int[]{2, 5, 10, 1}, 27, 4, fail, total);
        check(9, new int[]{5, 10}, 3, -1, fail, total);
        check(10, new int[]{2, 4}, 7, -1, fail, total);
        check(11, new int[]{2147483647}, 2, -1, fail, total);
        check(12, new int[]{186, 419, 83, 408}, 6249, 20, fail, total);
        check(13, new int[]{1, 2, 5}, 10000, 2000, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
