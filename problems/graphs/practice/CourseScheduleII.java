// Course Schedule II
// ref: LC 210
// Given numCourses and a list of [course, prereq] pairs meaning prereq must be
// taken before course, return one valid course order, or an empty array if the
// prerequisites contain a cycle and no valid order exists.
// Required complexity: O(V + E) time, O(V + E) space
// Study page: ../course-schedule-ii.md
// Run: javac --release 8 CourseScheduleII.java && java CourseScheduleII

import java.util.*;

class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        // TODO: implement
        return new int[0];
    }
}

public class CourseScheduleII {
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
                int[] actual = new Solution().findOrder(numCourses, prerequisites);
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
