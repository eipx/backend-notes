// Longest Palindromic Subsequence
// ref: LC 516
// Given a string s, return the length of the longest palindromic subsequence
// of s. A subsequence keeps some of the characters in their original order,
// and they do not have to be next to each other. A palindrome reads the same
// forward and backward.
// s holds lowercase letters; its length is from 1 to 1000.
// Required: O(n^2) time.
// Study page: ../longest-palindromic-subsequence.md
// Run: javac --release 8 LongestPalindromicSubsequence.java && java LongestPalindromicSubsequence

import java.util.*;

class Solution {
    public int longestPalindromeSubseq(String s) {
        // TODO: implement
        return 0;
    }
}

public class LongestPalindromicSubsequence {

    private static void check(int caseNum, String s, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().longestPalindromeSubseq(s);
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

        check(1, "bbbab", 4, fail, total);
        check(2, "cbbd", 2, fail, total);
        check(3, "a", 1, fail, total);
        check(4, "aa", 2, fail, total);
        check(5, "ab", 1, fail, total);
        check(6, "abc", 1, fail, total);
        check(7, "aaaa", 4, fail, total);
        check(8, "racecar", 7, fail, total);
        check(9, "agbdba", 5, fail, total);
        check(10, "abcb", 3, fail, total);
        check(11, "character", 5, fail, total);
        check(12, "abacdfgdcaba", 11, fail, total);
        check(13, "aabb", 2, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
