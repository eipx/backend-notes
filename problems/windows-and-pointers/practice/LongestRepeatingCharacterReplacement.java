// Longest Repeating Character Replacement
// ref: LC 424
// Given a string s of uppercase letters and an integer k, you may change up to
// k characters in s. Find the length of the longest contiguous slice that can
// be made to consist of a single repeated letter.
// Required complexity: O(n) time, O(1) space.
// Study page: ../longest-repeating-character-replacement.md
// Run: javac --release 8 LongestRepeatingCharacterReplacement.java && java LongestRepeatingCharacterReplacement

import java.util.*;

class Solution {
    public int characterReplacement(String s, int k) {
        // TODO: implement
        return 0;
    }
}

public class LongestRepeatingCharacterReplacement {
    public static void main(String[] args) {
        Object[][] cases = {
            {"ABAB", 2, 4},
            {"AABABBA", 1, 4},
            {"", 2, 0},
            {"A", 0, 1},
            {"AAAA", 2, 4},
            {"ABCDE", 1, 2},
            {"ABBB", 2, 4},
            {"AAAB", 0, 3},
            {"AABA", 0, 2},
            {"AB", 5, 2}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            String s = (String) cases[i][0];
            int k = (Integer) cases[i][1];
            int expected = (Integer) cases[i][2];
            try {
                int got = new Solution().characterReplacement(s, k);
                if (Integer.compare(expected, got) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + got);
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
