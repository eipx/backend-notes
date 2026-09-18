// Remove K Digits
// ref: LC 402
// Given a digit string num and an integer k, delete exactly k digits (keeping
// the relative order of the rest) so the remaining digits form the smallest
// possible number. No leading zeros in the result, unless the result is "0".
// Required: O(n) time, O(n) space.
// Study page: ../remove-k-digits.md
// Run: javac --release 8 RemoveKDigits.java && java RemoveKDigits

import java.util.*;

class Solution {
    public String removeKdigits(String num, int k) {
        // TODO: implement
        return "";
    }
}

public class RemoveKDigits {

    private static void check(int caseNum, String num, int k, String expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            String got = new Solution().removeKdigits(num, k);
            if (got.equals(expected)) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected \"" + expected + "\" got \"" + got + "\"");
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected \"" + expected + "\" got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, "1432219", 3, "1219", fail, total);
        check(2, "10200", 1, "200", fail, total);
        check(3, "10", 2, "0", fail, total);
        check(4, "9", 1, "0", fail, total);
        check(5, "112", 1, "11", fail, total);
        check(6, "1234567890", 9, "0", fail, total);
        check(7, "10", 1, "0", fail, total);
        check(8, "5337", 2, "33", fail, total);
        check(9, "100", 1, "0", fail, total);
        check(10, "1111111", 3, "1111", fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
