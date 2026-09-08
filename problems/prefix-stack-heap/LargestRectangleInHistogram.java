import java.util.ArrayDeque;
import java.util.Deque;

public class LargestRectangleInHistogram {

    // Returns the area of the largest rectangle that fits under the histogram outline.
    public static int solve(int[] heights) {
        int n = heights.length;
        // Build an extended array with a 0-height sentinel bar at the end.
        // The sentinel is <= every real height, so it forces the stack to fully drain
        // at the end without a separate cleanup loop after the main scan.
        int[] extended = new int[n + 1];
        System.arraycopy(heights, 0, extended, 0, n);
        extended[n] = 0;

        Deque<Integer> indexStack = new ArrayDeque<Integer>(); // ArrayDeque as stack; holds indices with increasing heights
        int maxArea = 0;
        for (int i = 0; i < extended.length; i++) {
            while (!indexStack.isEmpty() && extended[indexStack.peek()] > extended[i]) {
                int height = extended[indexStack.pop()];
                // width spans from just after the new stack top to i - 1;
                // if the stack is now empty, the rectangle reaches back to index 0
                int width = indexStack.isEmpty() ? i : i - indexStack.peek() - 1;
                int area = height * width;
                if (area > maxArea) {
                    maxArea = area;
                }
            }
            indexStack.push(i);
        }
        return maxArea;
    }

    private static void check(int caseNum, int[] heights, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(heights);
        if (Integer.compare(got, expected) == 0) {
            System.out.println("PASS");
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{4, 2, 6, 7, 3, 4}, 12, fail, total);
        check(2, new int[]{}, 0, fail, total);
        check(3, new int[]{5}, 5, fail, total);
        check(4, new int[]{1, 1, 1, 1}, 4, fail, total);
        check(5, new int[]{5, 4, 3, 2, 1}, 9, fail, total);
        check(6, new int[]{1, 2, 3, 4, 5}, 9, fail, total);
        check(7, new int[]{0, 0, 0}, 0, fail, total);
        check(8, new int[]{2, 2, 2}, 6, fail, total);
        check(9, new int[]{3, 6, 5, 7, 4, 8, 1, 0}, 20, fail, total);
        check(10, new int[]{5, 5, 0, 5, 5}, 10, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
