public class LongestIncreasingSubsequence {

    // Length of the longest strictly increasing subsequence, table form.
    // dp[i] is the length of the longest strictly increasing subsequence that
    // ends exactly at index i (it must use nums[i] as its last element).
    // The answer is the largest value anywhere in dp, not dp[n - 1].
    public static int solve(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        int best = 0;
        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i] && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                }
            }
            if (dp[i] > best) {
                best = dp[i];
            }
        }
        return best;
    }

    // Same answer in O(n log n). tails[k] is the smallest last value of any
    // strictly increasing subsequence of length k + 1 among the elements seen
    // so far. Only tails[0..size-1] is meaningful, and it is strictly
    // increasing, so each new value can be placed with a binary search.
    public static int solveFast(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;
        for (int x : nums) {
            int pos = lowerBound(tails, size, x);
            tails[pos] = x;
            if (pos == size) {
                size++;
            }
        }
        return size;
    }

    // First index in tails[0..size-1] whose value is >= target, or size when
    // every stored value is smaller than target.
    private static int lowerBound(int[] tails, int size, int target) {
        int lo = 0;
        int hi = size;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (tails[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums);
        int gotFast = solveFast(nums);
        if (got == expected && gotFast == expected) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            if (got != expected) {
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            } else {
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + gotFast + " (solveFast)");
            }
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{10, 9, 2, 5, 3, 7, 101, 18}, 4, fail, total);
        check(2, new int[]{0, 1, 0, 3, 2, 3}, 4, fail, total);
        check(3, new int[]{7, 7, 7, 7, 7, 7, 7}, 1, fail, total);
        check(4, new int[]{1}, 1, fail, total);
        check(5, new int[]{1, 2, 3, 4, 5}, 5, fail, total);
        check(6, new int[]{5, 4, 3, 2, 1}, 1, fail, total);
        check(7, new int[]{4, 10, 4, 3, 8, 9}, 3, fail, total);
        check(8, new int[]{1, 3, 6, 7, 9, 4, 10, 5, 6}, 6, fail, total);
        check(9, new int[]{-1, -2, 0, -3, 1}, 3, fail, total);
        check(10, new int[]{2, 2, 3, 3, 4, 4}, 3, fail, total);
        check(11, new int[]{3, 5, 6, 2, 5, 4, 19, 5, 6, 7, 12}, 6, fail, total);
        check(12, new int[]{10000, -10000, 0, 10000}, 3, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
