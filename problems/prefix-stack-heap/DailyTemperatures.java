import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Arrays;

public class DailyTemperatures {

    // For each day, returns how many days until a strictly warmer day; 0 if none exists.
    public static int[] solve(int[] temps) {
        int n = temps.length;
        int[] answer = new int[n];
        Deque<Integer> indexStack = new ArrayDeque<Integer>(); // ArrayDeque, never java.util.Stack, holds indices in decreasing temp order
        for (int i = 0; i < n; i++) {
            while (!indexStack.isEmpty() && temps[indexStack.peek()] < temps[i]) {
                int prevIndex = indexStack.pop();
                answer[prevIndex] = i - prevIndex; // strictly warmer day found, distance in days
            }
            indexStack.push(i);
        }
        // anything left on the stack never finds a warmer day; answer[] defaults to 0 already
        return answer;
    }

    private static void check(int caseNum, int[] temps, int[] expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int[] got = solve(temps);
        if (Arrays.equals(got, expected)) {
            System.out.println("PASS");
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + Arrays.toString(expected) + " got " + Arrays.toString(got));
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{70, 71, 69, 72, 70, 73}, new int[]{1, 2, 1, 2, 1, 0}, fail, total);
        check(2, new int[]{80, 80, 80}, new int[]{0, 0, 0}, fail, total);
        check(3, new int[]{100}, new int[]{0}, fail, total);
        check(4, new int[]{60, 65, 70, 75}, new int[]{1, 1, 1, 0}, fail, total);
        check(5, new int[]{90, 80, 70, 60}, new int[]{0, 0, 0, 0}, fail, total);
        check(6, new int[]{}, new int[]{}, fail, total);
        check(7, new int[]{50, 50, 51}, new int[]{2, 1, 0}, fail, total);
        check(8, new int[]{1, 2, 1, 2, 1, 2}, new int[]{1, 0, 1, 0, 1, 0}, fail, total);
        check(9, new int[]{40, 50}, new int[]{1, 0}, fail, total);
        check(10, new int[]{50, 40}, new int[]{0, 0}, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
