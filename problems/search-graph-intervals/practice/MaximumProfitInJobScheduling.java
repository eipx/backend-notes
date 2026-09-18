// Maximum Profit in Task Scheduling
// ref: LC 1235
// Given parallel arrays of start times, end times, and profits, choose a subset
// of non-overlapping tasks (touching is allowed) that maximizes total profit.
// Required complexity: O(n log n) time, O(n) space
// Study page: ../maximum-profit-in-job-scheduling.md
// Run: javac --release 8 MaximumProfitInJobScheduling.java && java MaximumProfitInJobScheduling

import java.util.*;

class Solution {
    public int taskScheduling(int[] startTime, int[] endTime, int[] profit) {
        // TODO: implement
        return 0;
    }
}

public class MaximumProfitInJobScheduling {
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
            try {
                int actual = new Solution().taskScheduling(startTime, endTime, profit);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual);
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
