// Cheapest Flights Within K Stops
// ref: LC 787
// There are n cities numbered 0 to n - 1. Each flight is [from, to, price], a
// one-way flight from city from to city to that costs price. Given a start city
// src, a destination city dst and an integer k, return the lowest total price of a
// trip from src to dst that makes at most k stops, or -1 if there is no such trip.
// A stop is a city strictly between src and dst, so a trip with at most k stops
// uses at most k + 1 flights.
// 1 <= n <= 100; 0 <= flights.length <= n * (n - 1) / 2; 0 <= from, to < n and
// from != to; 1 <= price <= 10000; no two flights join the same pair of cities;
// 0 <= src, dst, k < n and src != dst.
// Required: O(k * F) time, where F = flights.length.
// Study page: ../cheapest-flights-within-k-stops.md
// Run: javac --release 8 CheapestFlightsWithinKStops.java && java CheapestFlightsWithinKStops

import java.util.*;

class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        // TODO: implement
        return 0;
    }
}

public class CheapestFlightsWithinKStops {

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
        try {
            int got = new Solution().findCheapestPrice(n, flights, src, dst, k);
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
