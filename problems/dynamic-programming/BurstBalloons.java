public class BurstBalloons {

    // Maximum coins from bursting every balloon. Bursting balloon i earns
    // nums[i - 1] * nums[i] * nums[i + 1] using the neighbors that are still
    // there, and a missing neighbor past either end counts as 1.
    //
    // The balloons are copied into a padded array a with a 1 at each end, so
    // that every real balloon always has two neighbors. dp[i][j] is the most
    // coins from bursting every balloon strictly between positions i and j of
    // a, while positions i and j themselves are still standing. The question
    // for each interval is which balloon k in it bursts LAST. At that moment
    // only a[i] and a[j] are left beside it, so it earns a[i] * a[k] * a[j],
    // and the two sides (i..k and k..j) are independent problems because k is
    // still standing between them the whole time.
    public static int solve(int[] nums) {
        int n = nums.length;
        int[] a = new int[n + 2];
        a[0] = 1;
        a[n + 1] = 1;
        for (int i = 0; i < n; i++) {
            a[i + 1] = nums[i];
        }

        // Cells with j == i + 1 hold an interval with no balloon inside, and
        // stay 0 from the allocation.
        int[][] dp = new int[n + 2][n + 2];
        for (int gap = 2; gap <= n + 1; gap++) {
            for (int i = 0; i + gap <= n + 1; i++) {
                int j = i + gap;
                // Coins are never negative, so 0 is a safe starting maximum.
                int best = 0;
                for (int k = i + 1; k < j; k++) {
                    int coins = dp[i][k] + dp[k][j] + a[i] * a[k] * a[j];
                    if (coins > best) {
                        best = coins;
                    }
                }
                dp[i][j] = best;
            }
        }
        return dp[0][n + 1];
    }

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums);
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

        check(1, new int[]{3, 1, 5, 8}, 167, fail, total);
        check(2, new int[]{1, 5}, 10, fail, total);
        check(3, new int[]{}, 0, fail, total);
        check(4, new int[]{7}, 7, fail, total);
        check(5, new int[]{0, 0, 0}, 0, fail, total);
        check(6, new int[]{1, 1, 1, 1}, 4, fail, total);
        check(7, new int[]{3, 0, 5}, 20, fail, total);
        check(8, new int[]{2, 3, 4}, 36, fail, total);
        check(9, new int[]{1, 2, 3, 4, 5}, 110, fail, total);
        check(10, new int[]{5, 4, 3, 2, 1}, 110, fail, total);
        check(11, new int[]{2, 5, 1, 7, 3}, 179, fail, total);
        check(12, new int[]{100, 100, 100}, 1010100, fail, total);

        // 300 balloons worth 100 each: the largest input the limits allow.
        int[] big = new int[300];
        for (int i = 0; i < big.length; i++) {
            big[i] = 100;
        }
        check(13, big, 298010100, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
