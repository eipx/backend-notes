// Regular Expression Matching
// ref: LC 10
// Given a string s and a pattern p, return true if p matches the whole of s.
// In p, '.' matches any single character, and '*' matches zero or more copies
// of the element just before it (a letter or '.'). The match must cover all of
// s, not just a part of it.
// 1 <= s.length <= 20, 1 <= p.length <= 20. s holds lowercase letters; p holds
// lowercase letters, '.', and '*'. Every '*' has an element before it to repeat.
// The runner also tries an empty string and an empty pattern.
// Required: O(m * n) time.
// Study page: ../regular-expression-matching.md
// Run: javac --release 8 RegularExpressionMatching.java && java RegularExpressionMatching

import java.util.*;

class Solution {
    public boolean isMatch(String s, String p) {
        // TODO: implement
        return false;
    }
}

public class RegularExpressionMatching {

    private static void check(int caseNum, String s, String p, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            boolean got = new Solution().isMatch(s, p);
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

        check(1, "aa", "a", false, fail, total);
        check(2, "aa", "a*", true, fail, total);
        check(3, "ab", ".*", true, fail, total);
        check(4, "aab", "c*a*b", true, fail, total);
        check(5, "mississippi", "mis*is*p*.", false, fail, total);
        check(6, "", "", true, fail, total);
        check(7, "", "a*b*c*", true, fail, total);
        check(8, "a", "", false, fail, total);
        check(9, "a", "ab*", true, fail, total);
        check(10, "aaa", "a*a", true, fail, total);
        check(11, "a", ".*..a*", false, fail, total);
        check(12, "bbbba", ".*a*a", true, fail, total);
        check(13, "ab", ".*c", false, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
