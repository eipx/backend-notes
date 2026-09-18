// Max Consecutive Ones III
// ref: LC 1004
// Given a binary array nums and an integer k, you may flip at most k zeros to
// ones. Return the length of the longest run of ones achievable.
// Required: O(n) time, O(1) space.
// Study page: ../max-consecutive-ones-iii.md
// Run: javac --release 8 MaxConsecutiveOnesIII.java && java MaxConsecutiveOnesIII

import java.util.*;

class Solution {
    public int longestOnes(int[] nums, int k) {
        // TODO: implement
        return 0;
    }
}

public class MaxConsecutiveOnesIII {

    private static void check(int caseNum, int[] nums, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().longestOnes(nums, k);
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

        check(1, new int[]{1,1,1,0,0,0,1,1,1,1,0}, 2, 6, fail, total);
        check(2, new int[]{0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1}, 3, 10, fail, total);
        check(3, new int[]{0,0,0,1}, 0, 1, fail, total);
        check(4, new int[]{1,1,1,1}, 0, 4, fail, total);
        check(5, new int[]{0,0,0}, 3, 3, fail, total);
        check(6, new int[]{0}, 0, 0, fail, total);
        check(7, new int[]{1}, 0, 1, fail, total);
        check(8, new int[]{1,0,1,0,1,0,1}, 1, 3, fail, total);
        check(9, new int[]{0,1}, 0, 1, fail, total);
        check(10, new int[]{1,1,0,0,1,1}, 1, 3, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
