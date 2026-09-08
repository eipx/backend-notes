// Redundant Connection
// ref: LC 684
// Given n edges for n nodes describing a tree plus exactly one extra edge, find
// the first edge (scanning left to right) whose endpoints are already connected
// by earlier edges -- the one edge that creates the graph's only cycle.
// Required complexity: O(n * alpha(n)) time, O(n) space
// Study page: ../redundant-connection.md
// Run: javac --release 8 RedundantConnection.java && java RedundantConnection

import java.util.*;

class Solution {
    public int[] findRedundantConnection(int[][] edges) {
        // TODO: implement
        return new int[0];
    }
}

public class RedundantConnection {
    private static int[][] deepCopy(int[][] edges) {
        int[][] copy = new int[edges.length][];
        for (int i = 0; i < edges.length; i++) {
            copy[i] = edges[i].clone();
        }
        return copy;
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[][]{{2,3},{3,1},{1,2}}, new int[]{1,2} },
            { new int[][]{{1,2},{1,3},{3,4},{4,5},{2,5}}, new int[]{2,5} },
            { new int[][]{{1,2},{2,3},{2,4},{3,4}}, new int[]{3,4} },
            { new int[][]{{1,3},{1,2},{2,3}}, new int[]{2,3} },
            { new int[][]{{1,2},{2,3},{3,4},{4,5},{5,6},{6,2}}, new int[]{6,2} },
            { new int[][]{{1,2},{1,3},{1,4},{1,5},{4,5}}, new int[]{4,5} },
            { new int[][]{{2,1},{1,3},{3,2}}, new int[]{3,2} },
            { new int[][]{{1,2},{2,3},{1,4},{3,4}}, new int[]{3,4} },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[][] edges = (int[][]) cases[i][0];
            int[] expected = (int[]) cases[i][1];
            try {
                int[][] input = deepCopy(edges);
                int[] actual = new Solution().findRedundantConnection(input);
                if (Arrays.equals(actual, expected)) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected)
                            + " got " + Arrays.toString(actual));
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected)
                        + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
