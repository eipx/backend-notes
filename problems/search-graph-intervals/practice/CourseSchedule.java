// Course Schedule
// ref: LC 207
// Given a number of courses and a list of prerequisite pairs [a, b] (b must be taken
// before a), determine whether it is possible to complete every course at all.
// Required complexity: O(V + E) time, O(V + E) space
// Study page: ../course-schedule.md
// Run: javac --release 8 CourseSchedule.java && java CourseSchedule

import java.util.*;

class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // TODO: implement
        return false;
    }
}

public class CourseSchedule {
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
            try {
                boolean actual = new Solution().canFinish(numCourses, prerequisites);
                if (Boolean.valueOf(actual).equals(Boolean.valueOf(expected))) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (numCourses=" + numCourses + ", prerequisites=" + Arrays.deepToString(prerequisites) + ")");
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception "
                        + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
