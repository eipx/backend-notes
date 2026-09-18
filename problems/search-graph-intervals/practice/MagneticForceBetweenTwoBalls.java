// Magnetic Force Between Two Balls
// ref: LC 1552
// Place m balls into distinct baskets (given, unsorted positions) so that the
// smallest distance between any two placed balls is as large as possible.
// Required complexity: O(n log n + n log(maxPosition)) time, O(n) space
// Study page: ../magnetic-force-between-two-balls.md
// Run: javac --release 8 MagneticForceBetweenTwoBalls.java && java MagneticForceBetweenTwoBalls

import java.util.*;

class Solution {
    public int maxDistance(int[] position, int m) {
        // TODO: implement
        return 0;
    }
}

public class MagneticForceBetweenTwoBalls {
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
            try {
                int actual = new Solution().maxDistance(position, m);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (position=" + Arrays.toString(position) + ", m=" + m + ")");
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
