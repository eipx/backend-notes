// Capacity To Ship Packages Within D Days
// ref: LC 1011
// A conveyor belt loads packages onto a ship in order, splitting into days whenever
// the running load would exceed a chosen capacity. Find the smallest capacity that
// ships every package within the given number of days.
// Required complexity: O(n log(sum(weights))) time, O(1) extra space
// Study page: ../capacity-to-ship-packages-within-d-days.md
// Run: javac --release 8 CapacityToShipPackagesWithinDDays.java && java CapacityToShipPackagesWithinDDays

import java.util.*;

class Solution {
    public int shipWithinDays(int[] weights, int days) {
        // TODO: implement
        return 0;
    }
}

public class CapacityToShipPackagesWithinDDays {
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
            try {
                int actual = new Solution().shipWithinDays(weights, d);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (weights=" + Arrays.toString(weights) + ", d=" + d + ")");
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception "
                        + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
