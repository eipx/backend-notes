import java.util.Arrays;

public class MagneticForceBetweenTwoBalls {

    public static int solve(int[] position, int m) {
        int[] sorted = position.clone(); // never mutate the caller's array
        Arrays.sort(sorted);
        int lo = 1;
        int hi = sorted[sorted.length - 1] - sorted[0];
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2; // bias toward the upper half: we move lo=mid on success
            if (countPlaceable(sorted, mid) >= m) {
                lo = mid; // mid is achievable; try for something even larger
            } else {
                hi = mid - 1; // mid is too large a minimum gap to fit m balls
            }
        }
        return lo;
    }

    // Greedily places balls left to right, always taking the earliest basket that keeps
    // the running gap at least minGap, and returns how many balls fit under that rule.
    private static int countPlaceable(int[] sortedPositions, int minGap) {
        int count = 1;
        int last = sortedPositions[0]; // the first ball always goes in the leftmost basket
        for (int i = 1; i < sortedPositions.length; i++) {
            if (sortedPositions[i] - last >= minGap) {
                count++;
                last = sortedPositions[i];
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[]{1,2,3,4,7}, 3, 3 },
            { new int[]{5,4,3,2,1,1000000000}, 2, 999999999 },
            { new int[]{1,2,3,4,5,6,7}, 4, 2 },
            { new int[]{1,2}, 2, 1 },
            { new int[]{1,10}, 2, 9 },
            { new int[]{1,3,6,10}, 2, 9 },
            { new int[]{1,3,6,10}, 3, 4 },
            { new int[]{79,74,57,22}, 4, 5 },
            { new int[]{3,6,9,12,15}, 3, 6 },
            { new int[]{1,1000000000}, 2, 999999999 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] position = (int[]) cases[i][0];
            int m = (Integer) cases[i][1];
            int expected = (Integer) cases[i][2];
            int actual = solve(position, m);
            if (Integer.compare(actual, expected) == 0) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                        + " (position=" + Arrays.toString(position) + ", m=" + m + ")");
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
