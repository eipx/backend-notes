import java.util.*;

public class FreedomTrail {

    // Fewest steps to spell key on the ring. One step is either turning the
    // ring one position (clockwise or anticlockwise) or pressing the button.
    // At the start ring[0] is at the top, and every letter of key needs one
    // press while that letter is at the top.
    //
    // dp[i][j] is the fewest steps after the first i characters of key have
    // been spelled, with ring index j at the top. Only an index whose letter
    // equals key[i - 1] can have a real value for i >= 1; every other cell
    // stays at INF (unreachable).
    public static int solve(String ring, String key) {
        int n = ring.length();
        int m = key.length();

        // positions.get(c) lists every index of ring that holds the letter
        // 'a' + c, so a key character only tries the indices that can match.
        List<List<Integer>> positions = new ArrayList<List<Integer>>();
        for (int c = 0; c < 26; c++) {
            positions.add(new ArrayList<Integer>());
        }
        for (int p = 0; p < n; p++) {
            positions.get(ring.charAt(p) - 'a').add(p);
        }

        // Half of the int range, so that adding a small cost to a cell that
        // is still INF could never overflow.
        final int INF = Integer.MAX_VALUE / 2;
        int[][] dp = new int[m + 1][n];
        for (int[] row : dp) {
            Arrays.fill(row, INF);
        }
        // Nothing spelled yet and ring[0] is at the top.
        dp[0][0] = 0;

        for (int i = 1; i <= m; i++) {
            // key[i - 1] is the i-th character, so table row i covers i
            // characters.
            List<Integer> targets = positions.get(key.charAt(i - 1) - 'a');
            for (int to : targets) {
                int best = INF;
                for (int from = 0; from < n; from++) {
                    if (dp[i - 1][from] == INF) {
                        continue;
                    }
                    // Turn from -> to by the shorter way round, then press.
                    int cost = dp[i - 1][from] + ringDistance(from, to, n) + 1;
                    best = Math.min(best, cost);
                }
                dp[i][to] = best;
            }
        }

        int answer = INF;
        for (int j = 0; j < n; j++) {
            answer = Math.min(answer, dp[m][j]);
        }
        return answer;
    }

    // Turns needed to bring index b to the top when index a is at the top.
    // The ring is a circle, so going the other way round costs n - |a - b|.
    private static int ringDistance(int a, int b, int n) {
        int d = Math.abs(a - b);
        return Math.min(d, n - d);
    }

    // Java 8 has no String.repeat, so build repeated text with a loop.
    private static String rep(String unit, int times) {
        StringBuilder sb = new StringBuilder();
        for (int t = 0; t < times; t++) {
            sb.append(unit);
        }
        return sb.toString();
    }

    private static void check(int caseNum, String ring, String key, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(ring, key);
        if (got == expected) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, "godding", "gd", 4, fail, total);
        check(2, "godding", "godding", 13, fail, total);
        check(3, "a", "a", 1, fail, total);
        check(4, "ab", "b", 2, fail, total);
        check(5, "abcde", "ea", 4, fail, total);
        check(6, "abcde", "aaa", 3, fail, total);
        check(7, "abcde", "edcba", 10, fail, total);
        check(8, "abcd", "c", 3, fail, total);
        check(9, "ccabaaa", "ab", 5, fail, total);
        check(10, "abcdd", "d", 2, fail, total);
        check(11, "aaaaa", "aaaaa", 5, fail, total);
        check(12, rep("ab", 50), rep("ab", 50), 199, fail, total);
        check(13, rep("a", 100), rep("a", 100), 100, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
