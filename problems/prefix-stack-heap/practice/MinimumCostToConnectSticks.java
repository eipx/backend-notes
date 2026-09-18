// Minimum Cost to Connect Sticks
// ref: LC 1167
// Given stick lengths, repeatedly combine any two sticks into one, paying a
// cost equal to their sum each time, until one stick remains. Return the
// minimum total cost. Equivalent to: minimum total cost of repeatedly adding
// two numbers from a multiset until one remains.
// Required: O(n log n) time, O(n) space.
// Study page: ../minimum-cost-to-connect-sticks.md
// Run: javac --release 8 MinimumCostToConnectSticks.java && java MinimumCostToConnectSticks

import java.util.*;

class Solution {
    public int connectSticks(int[] sticks) {
        // TODO: implement
        return 0;
    }
}

public class MinimumCostToConnectSticks {

    private static void check(int caseNum, int[] sticks, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().connectSticks(sticks);
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{2, 4, 3}, 14, fail, total);
        check(2, new int[]{1, 8, 3, 5}, 30, fail, total);
        check(3, new int[]{5}, 0, fail, total);
        check(4, new int[]{1, 1}, 2, fail, total);
        check(5, new int[]{1, 2, 3, 4}, 19, fail, total);
        check(6, new int[]{4, 3, 2, 1}, 19, fail, total);
        check(7, new int[]{10000, 10000, 10000}, 50000, fail, total);
        check(8, new int[]{1, 1, 1, 1}, 8, fail, total);
        check(9, new int[]{1, 2}, 3, fail, total);
        check(10, new int[]{3, 3, 3, 3, 3}, 36, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
