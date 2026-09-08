import java.util.Arrays;

public class KokoEatingBananas {

    public static int solve(int[] piles, int h) {
        int lo = 1, hi = 0;
        for (int p : piles) {
            hi = Math.max(hi, p); // upper bound: eating the biggest pile in one hour is always enough
        }
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2; // avoids (lo+hi) overflow, not that it matters at these bounds
            if (hoursNeeded(piles, mid) <= h) {
                hi = mid; // mid works: the answer is mid or something smaller
            } else {
                lo = mid + 1; // mid is too slow: the answer must be larger
            }
        }
        return lo;
    }

    private static long hoursNeeded(int[] piles, int k) {
        long hours = 0; // must be long: up to 1e4 piles each needing up to 1e9 hours
        for (int p : piles) {
            hours += (p + k - 1) / k; // ceil division without floating point
        }
        return hours;
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[]{3,6,7,11}, 8, 4 },
            { new int[]{30,11,23,4,20}, 5, 30 },
            { new int[]{30,11,23,4,20}, 6, 23 },
            { new int[]{1,1,1,1}, 4, 1 },
            { new int[]{1000000000}, 2, 500000000 },
            { new int[]{1}, 1, 1 },
            { new int[]{312884470}, 968709470, 1 },
            { new int[]{3,6,7,11}, 4, 11 },
            { new int[]{5,5,5,5,5}, 5, 5 },
            { new int[]{2,10,3}, 3, 10 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] piles = (int[]) cases[i][0];
            int h = (Integer) cases[i][1];
            int expected = (Integer) cases[i][2];
            int actual = solve(piles, h);
            if (Integer.compare(actual, expected) == 0) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                        + " (piles=" + Arrays.toString(piles) + ", h=" + h + ")");
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
