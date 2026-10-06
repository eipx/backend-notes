// Delete Operation for Two Strings
// ref: LC 583
// Given two strings word1 and word2, return the smallest number of steps that
// makes the two strings equal. One step is deleting exactly one character from
// either string.
// Both strings hold lowercase letters; each length is from 1 to 500.
// Required: O(m * n) time.
// Study page: ../delete-operation-for-two-strings.md
// Run: javac --release 8 DeleteOperationForTwoStrings.java && java DeleteOperationForTwoStrings

import java.util.*;

class Solution {
    public int minDistance(String word1, String word2) {
        // TODO: implement
        return 0;
    }
}

public class DeleteOperationForTwoStrings {

    private static void check(int caseNum, String word1, String word2, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().minDistance(word1, word2);
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

        check(1, "sea", "eat", 2, fail, total);
        check(2, "leetcode", "etco", 4, fail, total);
        check(3, "a", "a", 0, fail, total);
        check(4, "a", "b", 2, fail, total);
        check(5, "abc", "abc", 0, fail, total);
        check(6, "abc", "def", 6, fail, total);
        check(7, "abcde", "ace", 2, fail, total);
        check(8, "ace", "abcde", 2, fail, total);
        check(9, "ab", "ba", 2, fail, total);
        check(10, "aaaa", "aa", 2, fail, total);
        check(11, "abcd", "dcba", 6, fail, total);
        check(12, "abcd", "bcda", 2, fail, total);
        check(13, "intention", "execution", 8, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
