// Climbing Stairs
// ref: LC 70
// A staircase has n steps. Each move climbs either 1 step or 2 steps. Return the
// number of different move sequences that go from the ground to exactly the top
// step. Order matters: 1 then 2 and 2 then 1 are different sequences.
// n is from 1 to 45; the answer for every legal n fits in an int.
// Examples: n = 3 returns 3 (1+1+1, 1+2, 2+1); n = 4 returns 5.
// Required: O(n) time, O(1) space.
// Run: javac --release 8 ClimbingStairs.java && java ClimbingStairs

import java.util.*;

class Solution {
    public int climbStairs(int n) {
        // TODO: implement
        return 0;
    }
}

public class ClimbingStairs {

    private static void check(int caseNum, int n, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().climbStairs(n);
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

        check(1, 1, 1, fail, total);
        check(2, 2, 2, fail, total);
        check(3, 3, 3, fail, total);
        check(4, 4, 5, fail, total);
        check(5, 5, 8, fail, total);
        check(6, 6, 13, fail, total);
        check(7, 10, 89, fail, total);
        check(8, 20, 10946, fail, total);
        check(9, 30, 1346269, fail, total);
        check(10, 40, 165580141, fail, total);
        check(11, 43, 701408733, fail, total);
        check(12, 44, 1134903170, fail, total);
        check(13, 45, 1836311903, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
