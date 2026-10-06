public class PartitionEqualSubsetSum {

    // True when the positive integers in nums can be split into two groups with
    // the same sum. Every element goes into exactly one group.
    public static boolean solve(int[] nums) {
        int total = 0;
        for (int x : nums) {
            total += x;
        }
        // Two equal integer halves need an even total. Checking this first also
        // keeps the integer division below from rounding an odd total down.
        if (total % 2 != 0) {
            return false;
        }
        int target = total / 2;

        // dp[s] is true when some subset of the elements processed so far
        // sums to exactly s. The empty subset makes dp[0] true from the start.
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int x : nums) {
            // Walk the sums from high to low. dp[s - x] sits at a lower index
            // and has not been touched during this element's pass yet, so it
            // still describes the elements before x. That is what limits each
            // element to one use.
            for (int s = target; s >= x; s--) {
                dp[s] = dp[s] || dp[s - x];
            }
        }
        return dp[target];
    }

    private static void check(int caseNum, int[] nums, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        boolean got = solve(nums);
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

        check(1, new int[]{1, 5, 11, 5}, true, fail, total);
        check(2, new int[]{1, 2, 3, 5}, false, fail, total);
        check(3, new int[]{1}, false, fail, total);
        check(4, new int[]{2, 2}, true, fail, total);
        check(5, new int[]{1, 2}, false, fail, total);
        check(6, new int[]{1, 1, 1, 1}, true, fail, total);
        check(7, new int[]{3, 3, 3, 4, 5}, true, fail, total);
        check(8, new int[]{1, 2, 5}, false, fail, total);
        check(9, new int[]{2, 2, 3, 5}, false, fail, total);
        check(10, new int[]{14, 9, 8, 4, 3, 2}, true, fail, total);
        check(11, new int[]{1, 2, 3, 4, 5}, false, fail, total);
        check(12, new int[]{99, 1, 100}, true, fail, total);
        check(13, new int[]{100, 100, 100, 100, 100, 100, 100, 100}, true, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
