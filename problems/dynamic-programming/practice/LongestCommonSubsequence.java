// Longest Common Subsequence
// ref: LC 1143
// Given two strings, return the length of the longest sequence of characters
// that appears in both of them in the same order. The characters do not have
// to be next to each other. Return 0 when the strings share no character.
// Both strings hold lowercase letters; each length is from 1 to 1000.
// Required: O(m * n) time.
// Study page: ../longest-common-subsequence.md
// Run: javac --release 8 LongestCommonSubsequence.java && java LongestCommonSubsequence

import java.util.*;

class Solution {
    public int longestCommonSubsequence(String text1, String text2) {
        // TODO: implement
        return 0;
    }
}

public class LongestCommonSubsequence {

    private static void check(int caseNum, String text1, String text2, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().longestCommonSubsequence(text1, text2);
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

        check(1, "abcde", "ace", 3, fail, total);
        check(2, "abc", "abc", 3, fail, total);
        check(3, "abc", "def", 0, fail, total);
        check(4, "a", "a", 1, fail, total);
        check(5, "a", "b", 0, fail, total);
        check(6, "aaaa", "aa", 2, fail, total);
        check(7, "abcba", "abcbcba", 5, fail, total);
        check(8, "abcdgh", "aedfhr", 3, fail, total);
        check(9, "ezupkr", "ubmrapg", 2, fail, total);
        check(10, "bsbininm", "jmjkbkjkv", 1, fail, total);
        check(11, "oxcpqrsvwf", "shmtulqrypy", 2, fail, total);
        check(12, "ace", "abcde", 3, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
