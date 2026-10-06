public class TargetSum {

    // Number of ways to put a + or - sign in front of every number in nums so
    // that the signed numbers add up to target. Two sign assignments are
    // different when they differ at any position, even if the numbers there are
    // equal (and +0 and -0 are different).
    //
    // Let P be the sum of the numbers that get a plus sign and N the sum of the
    // numbers that get a minus sign. Then P + N = total and P - N = target, so
    // P = (total + target) / 2. Choosing signs is the same as choosing which
    // positions form the plus group, so the answer is the number of subsets of
    // nums whose sum is exactly P.
    public static int solve(int[] nums, int target) {
        int total = 0;
        for (int x : nums) {
            total += x;
        }
        // |target| > total is out of reach. An odd total + target would make P
        // a fraction. Test with != 0, because % can return -1 for a negative
        // number. After this line total + target is not negative, so P is a
        // whole number from 0 to total.
        if (Math.abs(target) > total || (total + target) % 2 != 0) {
            return 0;
        }
        int plusSum = (total + target) / 2;

        // dp[s] is the number of subsets of the numbers processed so far that
        // sum to s. The empty subset gives sum 0, so dp[0] is 1.
        int[] dp = new int[plusSum + 1];
        dp[0] = 1;
        for (int x : nums) {
            // Walk the sums from high to low so that dp[s - x] still describes
            // subsets without x. That limits each position to one use. For
            // x == 0 the loop reaches s == 0 and doubles every cell, which is
            // right: a zero can sit in the plus group or the minus group.
            for (int s = plusSum; s >= x; s--) {
                dp[s] += dp[s - x];
            }
        }
        return dp[plusSum];
    }

    private static void check(int caseNum, int[] nums, int target, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums, target);
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

        check(1, new int[]{1, 1, 1, 1, 1}, 3, 5, fail, total);
        check(2, new int[]{1}, 1, 1, fail, total);
        check(3, new int[]{1, 1, 1, 1, 1}, -3, 5, fail, total);
        check(4, new int[]{1}, 3, 0, fail, total);
        check(5, new int[]{0}, 0, 2, fail, total);
        check(6, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1}, 1, 256, fail, total);
        check(7, new int[]{1, 2}, 2, 0, fail, total);
        check(8, new int[]{1, 2, 3}, 6, 1, fail, total);
        check(9, new int[]{1, 2, 3}, -6, 1, fail, total);
        check(10, new int[]{1, 2, 3}, 0, 2, fail, total);
        check(11, new int[]{1, 2, 3}, -8, 0, fail, total);
        check(12, new int[]{1, 2, 3}, -7, 0, fail, total);
        check(13, new int[]{100, 200, 300, 400}, 200, 2, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
