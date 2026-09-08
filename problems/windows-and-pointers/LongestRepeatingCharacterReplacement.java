public class LongestRepeatingCharacterReplacement {

    public static int solve(String s, int k) {
        int[] freq = new int[26];
        int windowStart = 0;
        int maxFreqInWindow = 0;
        int best = 0;
        for (int windowEnd = 0; windowEnd < s.length(); windowEnd++) {
            char c = s.charAt(windowEnd);
            freq[c - 'A']++;
            maxFreqInWindow = Math.max(maxFreqInWindow, freq[c - 'A']);
            int windowSize = windowEnd - windowStart + 1;
            // chars needing replacement = window size - count of the most frequent char in it;
            // maxFreqInWindow is allowed to be stale (never decreased on shrink) because a
            // window that was valid at this size can only be beaten by a strictly larger one
            if (windowSize - maxFreqInWindow > k) {
                char leaving = s.charAt(windowStart);
                freq[leaving - 'A']--;
                windowStart++;
            }
            best = Math.max(best, windowEnd - windowStart + 1);
        }
        return best;
    }

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
            int got = solve(s, k);
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
