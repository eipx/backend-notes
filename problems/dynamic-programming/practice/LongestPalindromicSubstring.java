// Longest Palindromic Substring
// ref: LC 5
// Given a string s, return a longest contiguous piece of s that reads the same
// forwards and backwards. Letters are case-sensitive, so 'A' and 'a' are different
// characters. When several pieces tie for the longest length, any one of them is
// accepted.
// s has length from 1 to 1000 and holds only digits and English letters.
// Examples: "babad" returns "bab" ("aba" is also accepted); "cbbd" returns "bb".
// Required: O(n^2) time or better.
// Run: javac --release 8 LongestPalindromicSubstring.java && java LongestPalindromicSubstring

import java.util.*;

class Solution {
    public String longestPalindrome(String s) {
        // TODO: implement
        return "";
    }
}

public class LongestPalindromicSubstring {

    private static boolean isPalindrome(String t) {
        for (int i = 0, j = t.length() - 1; i < j; i++, j--) {
            if (t.charAt(i) != t.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    private static String show(String t) {
        if (t.length() > 40) {
            return "\"" + t.substring(0, 37) + "...\"";
        }
        return "\"" + t + "\"";
    }

    private static String repeat(String unit, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(unit);
        }
        return sb.toString();
    }

    // Any longest palindromic piece is accepted: the answer must be a substring of s,
    // must read the same in both directions, and must have the expected length.
    private static void check(int caseNum, String s, int expectedLen, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        String expected = "a palindromic substring of length " + expectedLen;
        try {
            String got = new Solution().longestPalindrome(s);
            String problem = null;
            if (got == null) {
                problem = "null";
            } else if (!s.contains(got)) {
                problem = show(got) + " (not a substring of s)";
            } else if (!isPalindrome(got)) {
                problem = show(got) + " (not a palindrome)";
            } else if (got.length() != expectedLen) {
                problem = show(got) + " (length " + got.length() + ")";
            }
            if (problem == null) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + problem);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, "babad", 3, fail, total);
        check(2, "cbbd", 2, fail, total);
        check(3, "a", 1, fail, total);
        check(4, "ac", 1, fail, total);
        check(5, "zzzzzz", 6, fail, total);
        check(6, "xyzabccba", 6, fail, total);
        check(7, "qwertyracecar", 7, fail, total);
        check(8, "abcdeXYYZedcba", 2, fail, total);
        check(9, "x12321y", 5, fail, total);
        check(10, "AbBa", 1, fail, total);
        check(11, repeat("k", 1000), 1000, fail, total);
        check(12, repeat("ab", 500), 999, fail, total);
        check(13, "abaxyzzyxf", 6, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
