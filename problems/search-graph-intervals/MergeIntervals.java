import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MergeIntervals {

    public static int[][] solve(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0][];
        }
        int[][] sorted = intervals.clone(); // shallow clone: reorders the outer array without touching inner arrays yet
        Arrays.sort(sorted, new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Integer.compare(a[0], b[0]); // compare by start; avoids overflow risk of a[0] - b[0]
            }
        });

        List<int[]> merged = new ArrayList<int[]>(); // declared type List, concrete type ArrayList
        for (int[] interval : sorted) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                // no overlap with the current running interval: start a fresh one.
                // a NEW array is created here (not a reference into `sorted`) so later
                // mutation of the merged end never corrupts the caller's original input.
                merged.add(new int[]{interval[0], interval[1]});
            } else {
                int[] last = merged.get(merged.size() - 1);
                last[1] = Math.max(last[1], interval[1]); // nested interval trap: must take max, not overwrite
            }
        }
        return merged.toArray(new int[merged.size()][]);
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[][]{{1,3},{2,6},{8,10},{15,18}}, new int[][]{{1,6},{8,10},{15,18}} },
            { new int[][]{{1,4},{4,5}}, new int[][]{{1,5}} },
            { new int[][]{}, new int[][]{} },
            { new int[][]{{1,4}}, new int[][]{{1,4}} },
            { new int[][]{{1,4},{0,4}}, new int[][]{{0,4}} },
            { new int[][]{{1,4},{2,3}}, new int[][]{{1,4}} },
            { new int[][]{{1,4},{5,6}}, new int[][]{{1,4},{5,6}} },
            { new int[][]{{1,10},{2,3},{4,5},{6,7}}, new int[][]{{1,10}} },
            { new int[][]{{1,4},{1,4}}, new int[][]{{1,4}} },
            { new int[][]{{1,4},{0,2},{3,5}}, new int[][]{{0,5}} },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[][] input = (int[][]) cases[i][0];
            int[][] expected = (int[][]) cases[i][1];
            int[][] actual = solve(input);
            if (Arrays.deepEquals(actual, expected)) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.deepToString(expected)
                        + " got " + Arrays.deepToString(actual));
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
