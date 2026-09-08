// Longest Substring Without Repeating Characters
// ref: LC 3
// Given a string s, find the length of the longest contiguous run of
// characters in which no character appears twice.
// Required complexity: O(n) time, O(1) extra space (bounded by the character set).
// Study page: ../longest-substring-without-repeating-characters.md
// Run: javac --release 8 LongestSubstringWithoutRepeatingCharacters.java && java LongestSubstringWithoutRepeatingCharacters

import java.util.*;

class Solution {
    public int lengthOfLongestSubstring(String s) {
        // TODO: implement
        return 0;
    }
}

public class LongestSubstringWithoutRepeatingCharacters {
    public static void main(String[] args) {
        Object[][] cases = {
            {"", 0},
            {"a", 1},
            {"abcabcbb", 3},
            {"bbbbb", 1},
            {"pwwkew", 3},
            {"abba", 2},
            {"dvdf", 3},
            {" ", 1},
            {"au", 2},
            {"abcdefg", 7}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            String input = (String) cases[i][0];
            int expected = (Integer) cases[i][1];
            try {
                int got = new Solution().lengthOfLongestSubstring(input);
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
