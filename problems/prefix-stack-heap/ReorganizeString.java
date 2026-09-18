import java.util.PriorityQueue;

public class ReorganizeString {

    // Returns a permutation of s with no two adjacent equal characters, or ""
    // if no such permutation exists.
    public static String solve(String s) {
        int n = s.length();
        int[] counts = new int[26];
        for (int i = 0; i < n; i++) {
            counts[s.charAt(i) - 'a']++;
        }

        int maxFreq = 0;
        for (int i = 0; i < 26; i++) {
            maxFreq = Math.max(maxFreq, counts[i]);
        }
        if (maxFreq > (n + 1) / 2) {
            return ""; // some letter would be forced to sit next to itself
        }

        // Max-heap of {count, letterIndex}, ordered by count descending.
        PriorityQueue<int[]> heap = new PriorityQueue<int[]>((a, b) -> b[0] - a[0]);
        for (int i = 0; i < 26; i++) {
            if (counts[i] > 0) {
                heap.add(new int[]{counts[i], i});
            }
        }

        StringBuilder result = new StringBuilder(n);
        int prevCount = 0;
        int prevLetter = -1;
        while (!heap.isEmpty()) {
            int[] top = heap.poll();
            result.append((char) ('a' + top[1]));
            if (prevCount > 0) {
                heap.add(new int[]{prevCount, prevLetter});
            }
            prevCount = top[0] - 1;
            prevLetter = top[1];
        }
        return result.toString();
    }

    // Validates a proposed answer instead of comparing to one fixed string,
    // since this problem can have many different valid outputs.
    private static boolean isValidRearrangement(String original, String attempt) {
        if (attempt.length() != original.length()) {
            return false;
        }
        int[] originalCounts = new int[26];
        int[] attemptCounts = new int[26];
        for (int i = 0; i < original.length(); i++) {
            originalCounts[original.charAt(i) - 'a']++;
        }
        for (int i = 0; i < attempt.length(); i++) {
            attemptCounts[attempt.charAt(i) - 'a']++;
        }
        for (int i = 0; i < 26; i++) {
            if (originalCounts[i] != attemptCounts[i]) {
                return false;
            }
        }
        for (int i = 1; i < attempt.length(); i++) {
            if (attempt.charAt(i) == attempt.charAt(i - 1)) {
                return false;
            }
        }
        return true;
    }

    private static void check(int caseNum, String input, boolean expectFeasible, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        String got = solve(input);
        boolean ok;
        if (!expectFeasible) {
            ok = got.equals("");
        } else {
            ok = isValidRearrangement(input, got);
        }
        if (ok) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            String expectedDesc = expectFeasible ? ("a valid rearrangement of \"" + input + "\"") : "\"\" (impossible)";
            System.out.println("FAIL case " + caseNum + ": expected " + expectedDesc + " got \"" + got + "\"");
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, "aab", true, fail, total);
        check(2, "aaab", false, fail, total);
        check(3, "a", true, fail, total);
        check(4, "aa", false, fail, total);
        check(5, "aabb", true, fail, total);
        check(6, "aaabbbcc", true, fail, total);
        check(7, "vvvlo", true, fail, total);
        check(8, "abc", true, fail, total);
        check(9, "aaaa", false, fail, total);
        check(10, "ab", true, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
