import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class CourseScheduleII {

    public static int[] solve(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adjacency = new ArrayList<List<Integer>>();
        for (int i = 0; i < numCourses; i++) {
            adjacency.add(new ArrayList<Integer>());
        }
        int[] indegree = new int[numCourses];
        for (int[] pre : prerequisites) {
            int course = pre[0], prereq = pre[1];
            adjacency.get(prereq).add(course);
            indegree[course]++;
        }

        Deque<Integer> queue = new ArrayDeque<Integer>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }

        int[] order = new int[numCourses];
        int idx = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            order[idx++] = course;
            for (int next : adjacency.get(course)) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    queue.add(next);
                }
            }
        }
        return idx == numCourses ? order : new int[0];
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { 2, new int[][]{{1,0}}, new int[]{0,1} },
            { 4, new int[][]{{1,0},{2,0},{3,1},{3,2}}, new int[]{0,1,2,3} },
            { 2, new int[][]{{1,0},{0,1}}, new int[]{} },
            { 1, new int[][]{}, new int[]{0} },
            { 3, new int[][]{}, new int[]{0,1,2} },
            { 6, new int[][]{{1,0},{2,1},{3,2},{4,3},{5,4}}, new int[]{0,1,2,3,4,5} },
            { 4, new int[][]{{1,0},{2,1},{3,2},{1,3}}, new int[]{} },
            { 5, new int[][]{{1,0},{2,0},{3,1},{4,2},{4,3}}, new int[]{0,1,2,3,4} },
            { 3, new int[][]{{0,1},{1,2},{2,0}}, new int[]{} },
            { 2, new int[][]{{0,1}}, new int[]{1,0} },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int numCourses = (Integer) cases[i][0];
            int[][] prerequisites = (int[][]) cases[i][1];
            int[] expected = (int[]) cases[i][2];
            try {
                int[] actual = solve(numCourses, prerequisites);
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
