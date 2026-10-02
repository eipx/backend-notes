// Palindrome Number
// ref: LC 9
// Decide whether an integer x reads the same from left to right and from right
// to left when written in decimal. A negative number is never a palindrome (its
// minus sign appears only at the front).
// Input: int x. Output: boolean, true if x is a palindrome.
// Examples:
//   121 gives true. -121 gives false (it reads 121- backwards).
//   10 gives false (it reads 01 backwards).
// Constraints: x is any 32-bit int, from -2147483648 to 2147483647
// Required: do not convert the number to a string. This is the target, and the
// tests cannot enforce it. Aim for O(log10 |x|) time (one step per digit) and
// O(1) extra space.
// Run: javac --release 8 PalindromeNumber.java && java PalindromeNumber

import java.util.*;

class Solution {
    public boolean isPalindrome(int x) {
        // TODO: implement
        return false;
    }
}

public class PalindromeNumber {

    private static void check(int caseNum, int x, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            boolean got = new Solution().isPalindrome(x);
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

        check(1, 121, true, fail, total);
        check(2, -121, false, fail, total);
        check(3, 10, false, fail, total);
        check(4, 0, true, fail, total);
        check(5, 7, true, fail, total);
        check(6, 1221, true, fail, total);
        check(7, 1210, false, fail, total);
        check(8, Integer.MAX_VALUE, false, fail, total);
        check(9, Integer.MIN_VALUE, false, fail, total);
        check(10, 2147447412, true, fail, total);
        check(11, 1000001, true, fail, total);
        check(12, 1000021, false, fail, total);
        check(13, 12321, true, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
