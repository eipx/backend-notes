import java.util.*;

public class LongestSubstringWithoutRepeatingCharacters {

    public static int solve(String s) {
        int[] lastSeen = new int[128]; // ASCII index -> last index seen, -1 means not seen
        Arrays.fill(lastSeen, -1);
        int windowStart = 0;
        int best = 0;
        for (int windowEnd = 0; windowEnd < s.length(); windowEnd++) {
            char c = s.charAt(windowEnd);
            int prevIndex = lastSeen[c];
            // Only jump windowStart forward if the earlier occurrence is INSIDE the current
            // window; a stale index from before windowStart must never pull windowStart backward.
            if (prevIndex >= windowStart) {
                windowStart = prevIndex + 1;
            }
            lastSeen[c] = windowEnd;
            best = Math.max(best, windowEnd - windowStart + 1);
        }
        return best;
    }

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
            int got = solve(input);
            if (Integer.compare(expected, got) == 0) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + got);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
