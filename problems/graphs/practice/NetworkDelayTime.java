// Network Delay Time
// ref: LC 743
// Given directed weighted edges times[i] = [u,v,w], n nodes, and a source k,
// find the minimum time for a signal from k to reach every node, or -1 if some
// node is unreachable.
// Required complexity: O(E log E) time, O(V + E) space
// Study page: ../network-delay-time.md
// Run: javac --release 8 NetworkDelayTime.java && java NetworkDelayTime

import java.util.*;

class Solution {
    public int networkDelayTime(int[][] times, int n, int k) {
        // TODO: implement
        return -1;
    }
}

public class NetworkDelayTime {
    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[][]{{2,1,1},{2,3,1},{3,4,1}}, 4, 2, 2 },
            { new int[][]{{1,2,1}}, 2, 1, 1 },
            { new int[][]{{1,2,1}}, 2, 2, -1 },
            { new int[][]{{1,2,1},{2,3,2},{1,3,4}}, 3, 1, 3 },
            { new int[][]{}, 1, 1, 0 },
            { new int[][]{{1,2,1},{2,1,3}}, 2, 1, 1 },
            { new int[][]{{1,2,1},{2,1,3}}, 2, 2, 3 },
            { new int[][]{{1,2,10},{1,3,5},{3,2,1},{2,4,1},{3,4,9},{4,1,7}}, 4, 1, 7 },
            { new int[][]{{1,2,1},{1,3,1},{2,3,1}}, 3, 1, 1 },
            { new int[][]{{1,2,5},{2,3,5},{1,3,3}}, 3, 1, 5 },
            { new int[][]{{1,2,1}}, 3, 1, -1 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[][] times = (int[][]) cases[i][0];
            int n = (Integer) cases[i][1];
            int k = (Integer) cases[i][2];
            int expected = (Integer) cases[i][3];
            try {
                int actual = new Solution().networkDelayTime(times, n, k);
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
