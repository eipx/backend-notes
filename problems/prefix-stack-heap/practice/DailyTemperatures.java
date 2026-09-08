// Daily Temperatures
// ref: LC 739
// Given daily temperature readings in day order, for each day report how many
// days until a strictly warmer day occurs later; report 0 if none ever does.
// Required: O(n) time, O(n) space.
// Study page: ../daily-temperatures.md
// Run: javac --release 8 DailyTemperatures.java && java DailyTemperatures

import java.util.*;

class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        // TODO: implement
        return null;
    }
}

public class DailyTemperatures {

    private static void check(int caseNum, int[] temps, int[] expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int[] got = new Solution().dailyTemperatures(temps);
            if (Arrays.equals(got, expected)) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + Arrays.toString(expected) + " got " + Arrays.toString(got));
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + Arrays.toString(expected) + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{70, 71, 69, 72, 70, 73}, new int[]{1, 2, 1, 2, 1, 0}, fail, total);
        check(2, new int[]{80, 80, 80}, new int[]{0, 0, 0}, fail, total);
        check(3, new int[]{100}, new int[]{0}, fail, total);
        check(4, new int[]{60, 65, 70, 75}, new int[]{1, 1, 1, 0}, fail, total);
        check(5, new int[]{90, 80, 70, 60}, new int[]{0, 0, 0, 0}, fail, total);
        check(6, new int[]{}, new int[]{}, fail, total);
        check(7, new int[]{50, 50, 51}, new int[]{2, 1, 0}, fail, total);
        check(8, new int[]{1, 2, 1, 2, 1, 2}, new int[]{1, 0, 1, 0, 1, 0}, fail, total);
        check(9, new int[]{40, 50}, new int[]{1, 0}, fail, total);
        check(10, new int[]{50, 40}, new int[]{0, 0}, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
