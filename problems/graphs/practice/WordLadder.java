// Word Ladder
// ref: LC 127
// Given a start word, an end word, and a dictionary of same-length words, find
// the fewest words needed in a chain from start to end where each step changes
// exactly one letter and every intermediate word is in the dictionary, or 0 if
// the end word is absent or no such chain exists.
// Required complexity: O(dict * len^2) time, O(dict * len) space
// Study page: ../word-ladder.md
// Run: javac --release 8 WordLadder.java && java WordLadder

import java.util.*;

class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        // TODO: implement
        return 0;
    }
}

public class WordLadder {
    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { "hit", "cog", Arrays.asList("hot","dot","dog","lot","log","cog"), 5 },
            { "hit", "cog", Arrays.asList("hot","dot","dog","lot","log"), 0 },
            { "a", "c", Arrays.asList("a","b","c"), 2 },
            { "hot", "dog", Arrays.asList("hot","cog"), 0 },
            { "hot", "dog", Arrays.asList("hot","dog","dot"), 3 },
            { "cat", "bat", Arrays.asList("bat"), 2 },
            { "cat", "dog", Arrays.asList("cat","cot","cog","dog"), 4 },
            { "qa", "rl", Arrays.asList("ql","rl"), 3 },
            { "cold", "warm", Arrays.asList("cold","cord","card","ward","warm"), 5 },
            { "dog", "dot", Arrays.asList("dot"), 2 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            String beginWord = (String) cases[i][0];
            String endWord = (String) cases[i][1];
            @SuppressWarnings("unchecked")
            List<String> wordList = new ArrayList<String>((List<String>) cases[i][2]);
            int expected = (Integer) cases[i][3];
            try {
                int actual = new Solution().ladderLength(beginWord, endWord, wordList);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual);
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
