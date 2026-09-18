public class MaxConsecutiveOnesIII {

    // Longest run of 1s achievable after flipping at most k zeros to 1.
    public static int solve(int[] nums, int k) {
        int left = 0;
        int zeroCount = 0;
        int best = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                zeroCount++;
            }
            while (zeroCount > k) {
                if (nums[left] == 0) {
                    zeroCount--;
                }
                left++;
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    private static void check(int caseNum, int[] nums, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums, k);
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

        check(1, new int[]{1,1,1,0,0,0,1,1,1,1,0}, 2, 6, fail, total);
        check(2, new int[]{0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1}, 3, 10, fail, total);
        check(3, new int[]{0,0,0,1}, 0, 1, fail, total);
        check(4, new int[]{1,1,1,1}, 0, 4, fail, total);
        check(5, new int[]{0,0,0}, 3, 3, fail, total);
        check(6, new int[]{0}, 0, 0, fail, total);
        check(7, new int[]{1}, 0, 1, fail, total);
        check(8, new int[]{1,0,1,0,1,0,1}, 1, 3, fail, total);
        check(9, new int[]{0,1}, 0, 1, fail, total);
        check(10, new int[]{1,1,0,0,1,1}, 1, 3, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
