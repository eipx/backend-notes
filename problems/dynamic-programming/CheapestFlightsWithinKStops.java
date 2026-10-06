import java.util.*;

public class CheapestFlightsWithinKStops {

    // Cheapest total price to fly from src to dst using at most k stops, or -1
    // when no such trip exists. A stop is a city strictly between src and dst,
    // so a trip with at most k stops uses at most k + 1 flights.
    //
    // Let dp[r][v] be the cheapest price to reach city v using at most r
    // flights. Round r is built from round r - 1 only, so after k + 1 rounds
    // the table holds the best price for every city under the flight limit.
    // Only the previous round is ever read, so two arrays are enough: prev is
    // row r - 1 and cur is row r.
    public static int solve(int n, int[][] flights, int src, int dst, int k) {
        final int INF = Integer.MAX_VALUE;

        // Row 0: with zero flights only the start city is reachable, for free.
        int[] prev = new int[n];
        Arrays.fill(prev, INF);
        prev[src] = 0;

        for (int round = 1; round <= k + 1; round++) {
            // Start from a copy of the previous round: a city that was
            // reachable with r - 1 flights is still reachable with r.
            int[] cur = Arrays.copyOf(prev, n);
            for (int[] flight : flights) {
                int from = flight[0];
                int to = flight[1];
                int price = flight[2];
                // Read only prev, never cur, so each flight is used at most
                // once per round. Skip unreachable cities so INF + price can
                // never overflow.
                if (prev[from] == INF) {
                    continue;
                }
                cur[to] = Math.min(cur[to], prev[from] + price);
            }
            prev = cur;
        }
        return prev[dst] == INF ? -1 : prev[dst];
    }

    // A straight line of cities 0 -> 1 -> ... -> n - 1, every flight costs 1.
    private static int[][] chain(int n) {
        int[][] flights = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) {
            flights[i] = new int[]{i, i + 1, 1};
        }
        return flights;
    }

    private static void check(int caseNum, int n, int[][] flights, int src, int dst, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(n, flights, src, dst, k);
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

        check(1, 4, new int[][]{{0, 1, 100}, {1, 2, 100}, {2, 0, 100}, {1, 3, 600}, {2, 3, 200}}, 0, 3, 1, 700, fail, total);
        check(2, 3, new int[][]{{0, 1, 100}, {1, 2, 100}, {0, 2, 500}}, 0, 2, 1, 200, fail, total);
        check(3, 3, new int[][]{{0, 1, 100}, {1, 2, 100}, {0, 2, 500}}, 0, 2, 0, 500, fail, total);
        check(4, 2, new int[][]{{0, 1, 5}}, 0, 1, 0, 5, fail, total);
        check(5, 2, new int[][]{}, 0, 1, 0, -1, fail, total);
        check(6, 3, new int[][]{{1, 0, 5}}, 0, 1, 1, -1, fail, total);
        check(7, 3, new int[][]{{0, 1, 100}, {1, 2, 100}}, 0, 2, 0, -1, fail, total);
        check(8, 4, new int[][]{{0, 1, 1}, {1, 2, 1}, {0, 2, 5}, {2, 3, 1}}, 0, 3, 1, 6, fail, total);
        check(9, 5, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {3, 4, 1}, {0, 4, 10}}, 0, 4, 2, 10, fail, total);
        check(10, 5, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {3, 4, 1}, {0, 4, 10}}, 0, 4, 3, 4, fail, total);
        check(11, 4, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 0, 1}, {2, 3, 1}}, 0, 3, 3, 3, fail, total);
        check(12, 100, chain(100), 0, 99, 98, 99, fail, total);
        check(13, 100, chain(100), 0, 99, 97, -1, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
