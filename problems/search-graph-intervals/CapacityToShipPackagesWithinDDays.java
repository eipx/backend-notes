import java.util.Arrays;

public class CapacityToShipPackagesWithinDDays {

    public static int solve(int[] weights, int d) {
        int lo = 0;
        long sum = 0;
        for (int w : weights) {
            lo = Math.max(lo, w); // capacity must be able to carry the single heaviest package
            sum += w;
        }
        int hi = (int) sum; // sum(weights) <= 5e4 * 500 = 2.5e7, safely fits in int, but summed as long first
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (daysNeeded(weights, mid) <= d) {
                hi = mid; // mid works: the answer is mid or something smaller
            } else {
                lo = mid + 1; // mid is too small a capacity: too many days needed
            }
        }
        return lo;
    }

    private static int daysNeeded(int[] weights, int capacity) {
        int days = 1;
        long current = 0; // long defensively, though weights[i] <= 500 keeps this small in practice
        for (int w : weights) {
            if (current + w > capacity) { // this package would overflow today's load
                days++;
                current = 0;
            }
            current += w;
        }
        return days;
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[]{1,2,3,4,5,6,7,8,9,10}, 5, 15 },
            { new int[]{3,2,2,4,1,4}, 3, 6 },
            { new int[]{1,2,3,1,1}, 4, 3 },
            { new int[]{1,2,3,1,1}, 1, 8 },
            { new int[]{7,2,5,10,8}, 5, 10 },
            { new int[]{1,1,1,1,1}, 5, 1 },
            { new int[]{10}, 1, 10 },
            { new int[]{5,5,5,5}, 2, 10 },
            { new int[]{1,2,3,4,5,6,7,8,9,10}, 10, 10 },
            { new int[]{100,200,300}, 2, 300 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] weights = (int[]) cases[i][0];
            int d = (Integer) cases[i][1];
            int expected = (Integer) cases[i][2];
            int actual = solve(weights, d);
            if (Integer.compare(actual, expected) == 0) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                        + " (weights=" + Arrays.toString(weights) + ", d=" + d + ")");
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
