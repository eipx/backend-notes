// Longest Common Prefix
// ref: LC 14
// Given an array of strings, return the longest string that is a prefix of every
// string in the array. If the strings share no first character (or one of them is
// empty), the answer is the empty string.
// Input: String[] strs. Output: String, the longest common prefix, or "".
// Examples:
//   ["flower","flow","flight"] gives "fl".
//   ["dog","racecar","car"] gives "" (nothing is shared).
// Constraints: 1 <= strs.length <= 200, 0 <= strs[i].length() <= 200, every
// character is a lowercase English letter
// Required complexity: O(S) time, where S is the total number of characters in
// the input, and O(1) extra space (not counting the returned string)
// Run: javac --release 8 LongestCommonPrefix.java && java LongestCommonPrefix

import java.util.*;

class Solution {
    public String longestCommonPrefix(String[] strs) {
        // TODO: implement
        return "";
    }
}

public class LongestCommonPrefix {

    private static void check(int caseNum, String[] strs, String expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            String got = new Solution().longestCommonPrefix(strs);
            if (expected.equals(got)) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected \"" + expected + "\" got " + (got == null ? "null" : "\"" + got + "\""));
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected \"" + expected + "\" got exception " + e);
        }
    }

    private static String repeat(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    // 200 copies of a string made of 200 letters 'a'.
    private static String[] allMaxLength() {
        String[] a = new String[200];
        for (int i = 0; i < a.length; i++) {
            a[i] = repeat('a', 200);
        }
        return a;
    }

    // 199 strings of 200 letters 'a', then one string with 199 'a' and a final 'b'.
    private static String[] lastOneBreaksTheEnd() {
        String[] a = new String[200];
        for (int i = 0; i < a.length - 1; i++) {
            a[i] = repeat('a', 200);
        }
        a[a.length - 1] = repeat('a', 199) + "b";
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new String[]{"flower", "flow", "flight"}, "fl", fail, total);
        check(2, new String[]{"dog", "racecar", "car"}, "", fail, total);
        check(3, new String[]{"alone"}, "alone", fail, total);
        check(4, new String[]{""}, "", fail, total);
        check(5, new String[]{"abc", "", "abcd"}, "", fail, total);
        check(6, new String[]{"", "abc"}, "", fail, total);
        check(7, new String[]{"ab", "abc", "abcd"}, "ab", fail, total);
        check(8, new String[]{"same", "same"}, "same", fail, total);
        check(9, new String[]{"cir", "car"}, "c", fail, total);
        check(10, new String[]{"abcd", "ab", "abc"}, "ab", fail, total);
        check(11, new String[]{"abc", "abd", "ax"}, "a", fail, total);
        check(12, allMaxLength(), repeat('a', 200), fail, total);
        check(13, lastOneBreaksTheEnd(), repeat('a', 199), fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
