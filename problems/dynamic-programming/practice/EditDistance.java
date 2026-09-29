// Edit Distance
// ref: LC 72
// Given two strings word1 and word2, return the smallest number of steps that
// turns word1 into word2. One step is one of: insert one character, delete one
// character, or replace one character with another.
// Both strings hold lowercase letters; each length is from 0 to 500.
// Required: O(m * n) time.
// Study page: ../edit-distance.md
// Run: javac --release 8 EditDistance.java && java EditDistance

import java.util.*;

class Solution {
    public int minDistance(String word1, String word2) {
        // TODO: implement
        return 0;
    }
}

public class EditDistance {

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

        check(1, "horse", "ros", 3, fail, total);
        check(2, "intention", "execution", 5, fail, total);
        check(3, "", "", 0, fail, total);
        check(4, "", "abc", 3, fail, total);
        check(5, "abc", "", 3, fail, total);
        check(6, "abc", "abc", 0, fail, total);
        check(7, "a", "b", 1, fail, total);
        check(8, "ab", "ba", 2, fail, total);
        check(9, "kitten", "sitting", 3, fail, total);
        check(10, "sunday", "saturday", 3, fail, total);
        check(11, "abcdef", "azced", 3, fail, total);
        check(12, "aaaa", "a", 3, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
