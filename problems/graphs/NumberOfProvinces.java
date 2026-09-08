public class NumberOfProvinces {

    public static int solve(int[][] isConnected) {
        int n = isConnected.length;
        int[] parent = new int[n];
        int[] size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isConnected[i][j] == 1) {
                    union(parent, size, i, j);
                }
            }
        }

        int provinces = 0;
        for (int i = 0; i < n; i++) {
            if (find(parent, i) == i) {
                provinces++;
            }
        }
        return provinces;
    }

    private static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    private static void union(int[] parent, int[] size, int a, int b) {
        int rootA = find(parent, a);
        int rootB = find(parent, b);
        if (rootA == rootB) {
            return;
        }
        if (size[rootA] < size[rootB]) {
            int tmp = rootA;
            rootA = rootB;
            rootB = tmp;
        }
        parent[rootB] = rootA;
        size[rootA] += size[rootB];
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[][]{{1,1,0},{1,1,0},{0,0,1}}, 2 },
            { new int[][]{{1,0,0},{0,1,0},{0,0,1}}, 3 },
            { new int[][]{{1,1,1},{1,1,1},{1,1,1}}, 1 },
            { new int[][]{{1}}, 1 },
            { new int[][]{{1,0},{0,1}}, 2 },
            { new int[][]{{1,1,0,0},{1,1,0,0},{0,0,1,1},{0,0,1,1}}, 2 },
            { new int[][]{{1,0,0,1},{0,1,1,0},{0,1,1,0},{1,0,0,1}}, 2 },
            { new int[][]{{1,1,0,0,0},{1,1,1,0,0},{0,1,1,0,0},{0,0,0,1,1},{0,0,0,1,1}}, 2 },
            { new int[][]{{1,0,0,0,0},{0,1,0,0,0},{0,0,1,0,0},{0,0,0,1,0},{0,0,0,0,1}}, 5 },
            { new int[][]{{1,1,1,1,1,1},{1,1,1,1,1,1},{1,1,1,1,1,1},{1,1,1,1,1,1},{1,1,1,1,1,1},{1,1,1,1,1,1}}, 1 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[][] isConnected = (int[][]) cases[i][0];
            int expected = (Integer) cases[i][1];
            try {
                int actual = solve(isConnected);
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
