// Majority Element
// ref: LC 169
// Given an array of n integers, return the value that occurs strictly more than
// half of the time, that is, in more than n / 2 positions (n / 2 taken as an exact
// fraction, so 3 of 5 qualifies and 2 of 4 does not). Such a value is guaranteed
// to exist.
// Input: int[] nums. Output: int, the majority value.
// Examples:
//   [3,2,3] gives 3 (it fills 2 of the 3 positions).
//   [2,2,1,1,1,2,2] gives 2 (it fills 4 of the 7 positions).
// Constraints: 1 <= nums.length <= 50000, every value fits in a 32-bit int
// (negative values and the extreme values are allowed), a majority value exists.
// Required complexity: O(n) time, O(1) extra space
// Run: javac --release 8 MajorityElement.java && java MajorityElement

import java.util.*;

class Solution {
    public int majorityElement(int[] nums) {
        // TODO: implement
        return 0;
    }
}

public class MajorityElement {

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().majorityElement(nums);
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

    // 50000 entries. Even positions and the last position hold Integer.MIN_VALUE
    // (25001 copies, exactly one more than half); every other odd position holds
    // its own index, so those values are all different.
    private static int[] barelyAMajority() {
        int[] a = new int[50000];
        for (int i = 0; i < a.length; i++) {
            a[i] = (i % 2 == 0) ? Integer.MIN_VALUE : i;
        }
        a[a.length - 1] = Integer.MIN_VALUE;
        return a;
    }

    // 50000 entries: 24999 copies of 1000 first, then 25001 copies of 999.
    private static int[] bigRivalFirst() {
        int[] a = new int[50000];
        for (int i = 0; i < a.length; i++) {
            a[i] = (i < 24999) ? 1000 : 999;
        }
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{3, 2, 3}, 3, fail, total);
        check(2, new int[]{2, 2, 1, 1, 1, 2, 2}, 2, fail, total);
        check(3, new int[]{1}, 1, fail, total);
        check(4, new int[]{Integer.MIN_VALUE}, Integer.MIN_VALUE, fail, total);
        check(5, new int[]{5, 5}, 5, fail, total);
        check(6, new int[]{-1, -1, -1, 5, 6}, -1, fail, total);
        check(7, new int[]{1, 2, 3, 4, 4, 4, 4}, 4, fail, total);
        check(8, new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MAX_VALUE, fail, total);
        check(9, new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE}, Integer.MIN_VALUE, fail, total);
        check(10, new int[]{1, 2, 3, 3, 3}, 3, fail, total);
        check(11, new int[]{0, -1, 0, -1, 0}, 0, fail, total);
        check(12, barelyAMajority(), Integer.MIN_VALUE, fail, total);
        check(13, bigRivalFirst(), 999, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
