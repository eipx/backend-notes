import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class NetworkDelayTime {

    public static int solve(int[][] times, int n, int k) {
        List<List<int[]>> graph = new ArrayList<List<int[]>>();
        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<int[]>());
        }
        for (int[] edge : times) {
            graph.get(edge[0]).add(new int[]{edge[1], edge[2]});
        }

        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        PriorityQueue<int[]> minHeap = new PriorityQueue<int[]>((a, b) -> Integer.compare(a[1], b[1]));
        minHeap.add(new int[]{k, 0});

        boolean[] finalized = new boolean[n + 1];
        int finalizedCount = 0;
        int maxDist = 0;

        while (!minHeap.isEmpty()) {
            int[] current = minHeap.poll();
            int node = current[0];
            int d = current[1];
            if (finalized[node]) {
                continue;
            }
            finalized[node] = true;
            finalizedCount++;
            maxDist = Math.max(maxDist, d);
            for (int[] edge : graph.get(node)) {
                int next = edge[0];
                int weight = edge[1];
                if (!finalized[next] && d + weight < dist[next]) {
                    dist[next] = d + weight;
                    minHeap.add(new int[]{next, dist[next]});
                }
            }
        }

        return finalizedCount == n ? maxDist : -1;
    }

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
            int actual = solve(times, n, k);
            if (Integer.compare(actual, expected) == 0) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
