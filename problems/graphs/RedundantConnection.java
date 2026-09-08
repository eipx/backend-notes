import java.util.Arrays;

public class RedundantConnection {

    public static int[] solve(int[][] edges) {
        int n = edges.length;
        int[] parent = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1];
            int rootU = find(parent, u);
            int rootV = find(parent, v);
            if (rootU == rootV) {
                return edge;
            }
            parent[rootU] = rootV;
        }
        return new int[0];
    }

    private static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
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
                int[] actual = solve(edges);
                if (Arrays.equals(actual, expected)) {
                    System.out.println("PASS");
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
