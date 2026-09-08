import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class CourseSchedule {

    public static boolean solve(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adjacency = new ArrayList<List<Integer>>();
        for (int i = 0; i < numCourses; i++) {
            adjacency.add(new ArrayList<Integer>()); // declared type List, concrete type ArrayList (Java 8 style)
        }
        int[] inDegree = new int[numCourses];
        for (int[] pair : prerequisites) {
            int course = pair[0], prereq = pair[1];
            adjacency.get(prereq).add(course); // edge prereq -> course
            inDegree[course]++; // course now needs one more prerequisite satisfied
        }

        Deque<Integer> queue = new ArrayDeque<Integer>(); // ArrayDeque rejects null; boxed Integer is fine here
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.add(i); // no prerequisites at all: can be taken immediately
            }
        }

        int processed = 0;
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            processed++;
            for (int next : adjacency.get(cur)) {
                inDegree[next]--; // one of next's prerequisites is now satisfied
                if (inDegree[next] == 0) {
                    queue.add(next); // next just became available
                }
            }
        }

        return processed == numCourses; // fewer means some courses are stuck in a cycle
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { 2, new int[][]{{1,0}}, Boolean.TRUE },
            { 2, new int[][]{{1,0},{0,1}}, Boolean.FALSE },
            { 1, new int[][]{}, Boolean.TRUE },
            { 1, new int[][]{{0,0}}, Boolean.FALSE },
            { 3, new int[][]{{1,0},{2,1}}, Boolean.TRUE },
            { 4, new int[][]{{1,0},{2,0},{3,1},{3,2}}, Boolean.TRUE },
            { 3, new int[][]{{0,1},{1,2},{2,0}}, Boolean.FALSE },
            { 5, new int[][]{}, Boolean.TRUE },
            { 2, new int[][]{{0,1}}, Boolean.TRUE },
            { 6, new int[][]{{1,0},{2,0},{3,1},{3,2},{4,3},{5,4}}, Boolean.TRUE },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int numCourses = (Integer) cases[i][0];
            int[][] prerequisites = (int[][]) cases[i][1];
            boolean expected = (Boolean) cases[i][2];
            boolean actual = solve(numCourses, prerequisites);
            if (Boolean.valueOf(actual).equals(Boolean.valueOf(expected))) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                        + " (numCourses=" + numCourses + ", prerequisites=" + Arrays.deepToString(prerequisites) + ")");
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
