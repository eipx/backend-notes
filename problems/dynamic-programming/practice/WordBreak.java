// Word Break
// ref: LC 139
// Given a string s and a list of words wordDict, return true if s can be cut into
// one or more pieces so that every piece is a word from wordDict, and false
// otherwise. The pieces cover s from the first character to the last, in order,
// with no gaps and no overlaps. A word may be used any number of times.
// s has 1 to 300 lowercase letters; wordDict has 1 to 1000 distinct lowercase
// words of 1 to 20 letters each.
// Required: O(n * L) hash lookups, where L is the length of the longest word.
// Study page: ../word-break.md
// Run: javac --release 8 WordBreak.java && java WordBreak

import java.util.*;

class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        // TODO: implement
        return false;
    }
}

public class WordBreak {

    // Java 8 has no String.repeat, so build repeated text with a loop.
    private static String rep(String unit, int times) {
        StringBuilder sb = new StringBuilder();
        for (int t = 0; t < times; t++) {
            sb.append(unit);
        }
        return sb.toString();
    }

    private static void check(int caseNum, String s, String[] words, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            boolean got = new Solution().wordBreak(s, Arrays.asList(words));
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

        check(1, "leetcode", new String[]{"leet", "code"}, true, fail, total);
        check(2, "applepenapple", new String[]{"apple", "pen"}, true, fail, total);
        check(3, "catsandog", new String[]{"cats", "dog", "sand", "and", "cat"}, false, fail, total);
        check(4, "a", new String[]{"a"}, true, fail, total);
        check(5, "a", new String[]{"b"}, false, fail, total);
        check(6, "ab", new String[]{"abc", "b"}, false, fail, total);
        check(7, "cars", new String[]{"car", "ca", "rs"}, true, fail, total);
        check(8, "abcd", new String[]{"a", "abc", "b", "cd"}, true, fail, total);
        check(9, "abcd", new String[]{"ab", "abc", "d"}, true, fail, total);
        check(10, "aaaaaaa", new String[]{"aaaa", "aaa"}, true, fail, total);
        check(11, "aaaaaaa", new String[]{"aa", "aaaa"}, false, fail, total);
        check(12, rep("a", 30) + "b", new String[]{"a", "aa", "aaa", "aaaa"}, false, fail, total);
        check(13, rep("a", 300), new String[]{rep("a", 20)}, true, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
