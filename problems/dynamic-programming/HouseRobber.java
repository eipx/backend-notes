public class HouseRobber {

    // Largest total that can be taken from a row of houses when no two
    // neighboring houses may both be taken.
    //
    // After house i has been looked at, prev1 is the best total over houses
    // 0..i and prev2 is the best total over houses 0..i-1. Before any house
    // has been looked at, both are 0 (an empty row is worth nothing).
    public static int solve(int[] nums) {
        int prev2 = 0;
        int prev1 = 0;
        for (int i = 0; i < nums.length; i++) {
            // Take house i: its neighbor i - 1 is off limits, so the rest of
            // the total comes from houses 0..i-2.
            int take = prev2 + nums[i];
            // Skip house i: the best total over houses 0..i-1 carries over.
            int skip = prev1;
            int cur = Math.max(take, skip);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    // A row of n houses that all hold the same amount.
    private static int[] filled(int n, int value) {
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = value;
        }
        return nums;
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

        check(1, new int[]{1, 2, 3, 1}, 4, fail, total);
        check(2, new int[]{2, 7, 9, 3, 1}, 12, fail, total);
        check(3, new int[]{5}, 5, fail, total);
        check(4, new int[]{0}, 0, fail, total);
        check(5, new int[]{2, 1}, 2, fail, total);
        check(6, new int[]{1, 2}, 2, fail, total);
        check(7, new int[]{0, 0, 0, 0}, 0, fail, total);
        check(8, new int[]{4, 4, 4, 4, 4}, 12, fail, total);
        check(9, new int[]{2, 3, 2}, 4, fail, total);
        check(10, new int[]{1, 100, 1}, 100, fail, total);
        check(11, new int[]{2, 1, 1, 2}, 4, fail, total);
        check(12, new int[]{5, 1, 1, 5, 1, 1, 5}, 15, fail, total);
        check(13, filled(100, 400), 20000, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
