import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordLadder {

    public static int solve(String beginWord, String endWord, List<String> wordList) {
        Set<String> dict = new HashSet<String>(wordList);
        if (!dict.contains(endWord)) {
            return 0;
        }
        int len = beginWord.length();
        Map<String, List<String>> patternMap = new HashMap<String, List<String>>();
        for (String word : dict) {
            for (int i = 0; i < len; i++) {
                String pattern = word.substring(0, i) + "*" + word.substring(i + 1);
                patternMap.computeIfAbsent(pattern, k -> new ArrayList<String>()).add(word);
            }
        }

        Set<String> visited = new HashSet<String>();
        Deque<String> queue = new ArrayDeque<String>();
        queue.add(beginWord);
        visited.add(beginWord);
        int steps = 1;
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                String word = queue.poll();
                if (word.equals(endWord)) {
                    return steps;
                }
                for (int j = 0; j < len; j++) {
                    String pattern = word.substring(0, j) + "*" + word.substring(j + 1);
                    List<String> neighbors = patternMap.getOrDefault(pattern, new ArrayList<String>());
                    for (String next : neighbors) {
                        if (!visited.contains(next)) {
                            visited.add(next);
                            queue.add(next);
                        }
                    }
                }
            }
            steps++;
        }
        return 0;
    }

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
            List<String> wordList = (List<String>) cases[i][2];
            int expected = (Integer) cases[i][3];
            try {
                int actual = solve(beginWord, endWord, wordList);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS");
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
