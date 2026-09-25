public class HouseRobberII {

    // Maximum money that can be taken from houses arranged in a circle, where
    // the first and last houses are adjacent and no two adjacent houses can
    // both be taken.
    public static int solve(int[] nums) {
        int n = nums.length;
        if (n == 1) {
            return nums[0];
        }
        return Math.max(robLine(nums, 0, n - 2), robLine(nums, 1, n - 1));
    }

    // Classic linear House Robber DP over the inclusive range [start, end] of
    // nums, using two rolling variables instead of a table. Returns 0 when
    // the range is empty (start > end).
    private static int robLine(int[] nums, int start, int end) {
        int prev2 = 0;
        int prev1 = 0;
        for (int i = start; i <= end; i++) {
            int cur = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
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

        check(1, new int[]{5}, 5, fail, total);
        check(2, new int[]{2, 3}, 3, fail, total);
        check(3, new int[]{2, 10, 2}, 10, fail, total);
        check(4, new int[]{4, 4, 4, 4, 4, 4}, 12, fail, total);
        check(5, new int[]{0, 0, 0, 0, 0}, 0, fail, total);
        check(6, new int[]{10, 1, 1, 1}, 11, fail, total);
        check(7, new int[]{1, 1, 1, 10}, 11, fail, total);
        check(8, new int[]{3, 6, 4, 8, 3}, 14, fail, total);
        check(9, new int[]{10, 1, 10, 1, 10, 1}, 30, fail, total);
        check(10, new int[]{5, 1, 1, 5}, 6, fail, total);
        check(11, new int[]{5, 1, 5, 1, 1}, 10, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
