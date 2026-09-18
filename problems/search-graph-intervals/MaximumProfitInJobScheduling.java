import java.util.Arrays;
import java.util.Comparator;

public class MaximumProfitInJobScheduling {

    public static int solve(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        final int[] endTimeRef = endTime; // captured by the comparator below
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        Arrays.sort(order, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                return Integer.compare(endTimeRef[a], endTimeRef[b]);
            }
        });

        int[] start = new int[n];
        int[] end = new int[n];
        int[] prof = new int[n];
        for (int i = 0; i < n; i++) {
            start[i] = startTime[order[i]];
            end[i] = endTime[order[i]];
            prof[i] = profit[order[i]];
        }

        int[] dp = new int[n + 1];
        for (int i = 0; i < n; i++) {
            int j = latestCompatible(end, i, start[i]);
            int takeProfit = prof[i] + dp[j + 1];
            dp[i + 1] = Math.max(dp[i], takeProfit);
        }
        return dp[n];
    }

    private static int latestCompatible(int[] end, int upperExclusive, int target) {
        int lo = 0, hi = upperExclusive - 1, result = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (end[mid] <= target) {
                result = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[]{1,2,3,3}, new int[]{3,4,5,6}, new int[]{50,10,40,70}, 120 },
            { new int[]{1,2,3,4,6}, new int[]{3,5,10,6,9}, new int[]{20,20,100,70,60}, 150 },
            { new int[]{1,1,1}, new int[]{2,3,4}, new int[]{5,6,4}, 6 },
            { new int[]{1}, new int[]{2}, new int[]{5}, 5 },
            { new int[]{1,2}, new int[]{2,3}, new int[]{5,6}, 11 },
            { new int[]{1,2}, new int[]{3,4}, new int[]{5,6}, 6 },
            { new int[]{1,2,3}, new int[]{10,10,10}, new int[]{5,6,4}, 6 },
            { new int[]{1,2,3,4}, new int[]{2,3,4,5}, new int[]{10,10,10,10}, 40 },
            { new int[]{1,2}, new int[]{3,3}, new int[]{10,20}, 20 },
            { new int[]{10}, new int[]{20}, new int[]{100}, 100 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] startTime = (int[]) cases[i][0];
            int[] endTime = (int[]) cases[i][1];
            int[] profit = (int[]) cases[i][2];
            int expected = (Integer) cases[i][3];
            int actual = solve(startTime, endTime, profit);
            if (Integer.compare(actual, expected) == 0) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
