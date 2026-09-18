public class FirstMissingPositive {

    // Smallest positive integer not present in nums. O(n) time, O(1) extra space.
    public static int solve(int[] nums) {
        int n = nums.length;

        for (int i = 0; i < n; i++) {
            // Keep swapping nums[i] into its home slot (index value-1) as long
            // as it belongs in range and isn't already correctly placed there.
            while (nums[i] >= 1 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) {
                int targetIndex = nums[i] - 1;
                int temp = nums[targetIndex];
                nums[targetIndex] = nums[i];
                nums[i] = temp;
            }
        }

        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) {
                return i + 1;
            }
        }
        return n + 1; // every value 1..n was present
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

        check(1, new int[]{1, 2, 0}, 3, fail, total);
        check(2, new int[]{3, 4, -1, 1}, 2, fail, total);
        check(3, new int[]{7, 8, 9, 11, 12}, 1, fail, total);
        check(4, new int[]{1, 2, 3}, 4, fail, total);
        check(5, new int[]{1}, 2, fail, total);
        check(6, new int[]{2}, 1, fail, total);
        check(7, new int[]{-1}, 1, fail, total);
        check(8, new int[]{1, 1}, 2, fail, total);
        check(9, new int[]{0, 0, 0}, 1, fail, total);
        check(10, new int[]{2, 3, 4, 5, 6}, 1, fail, total);
        check(11, new int[]{1, 2, 2, 3}, 4, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
