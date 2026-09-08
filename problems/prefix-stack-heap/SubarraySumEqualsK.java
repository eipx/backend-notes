import java.util.HashMap;
import java.util.Map;

public class SubarraySumEqualsK {

    // Counts contiguous subarrays of nums whose elements sum exactly to k.
    public static int solve(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<Integer, Integer>();
        prefixCount.put(0, 1); // P[0] = 0 counts as a real prefix, seen before any element is read
        int runningSum = 0;
        int total = 0;
        for (int num : nums) {
            runningSum += num;
            int need = runningSum - k;
            Integer seen = prefixCount.get(need); // may be null -> use Integer, not int, to avoid NPE on unboxing
            if (seen != null) {
                total += seen;
            }
            Integer existing = prefixCount.get(runningSum);
            prefixCount.put(runningSum, existing == null ? 1 : existing + 1);
        }
        return total;
    }

    private static void check(int caseNum, int[] nums, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums, k);
        if (Integer.compare(got, expected) == 0) {
            System.out.println("PASS");
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{1, 1, 1}, 2, 2, fail, total);
        check(2, new int[]{1, 2, 3}, 3, 2, fail, total);
        check(3, new int[]{1}, 1, 1, fail, total);
        check(4, new int[]{1}, 0, 0, fail, total);
        check(5, new int[]{-1, -1, 1}, 0, 1, fail, total);
        check(6, new int[]{0, 0, 0, 0}, 0, 10, fail, total);
        check(7, new int[]{1, -1, 0}, 0, 3, fail, total);
        check(8, new int[]{1, 2, -3, 3}, 3, 3, fail, total);
        check(9, new int[]{-1, -2, -3}, -6, 1, fail, total);
        check(10, new int[]{5, -5, 5, -5}, 0, 4, fail, total);
        check(11, new int[]{1, 2, 3}, 100, 0, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
