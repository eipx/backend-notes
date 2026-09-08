import java.util.*;

public class SlidingWindowMaximum {

    public static int[] solve(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<Integer>(); // stores indices, values strictly decreasing front-to-back
        for (int i = 0; i < n; i++) {
            // drop indices that fell out of the window on the left
            while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }
            // maintain the decreasing invariant: anything smaller than nums[i] can never be
            // the max again while nums[i] is still in the window, so it is safe to discard
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }
            deque.offerLast(i);
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{1,3,-1,-3,5,3,6,7}, 3, new int[]{3,3,5,5,6,7}},
            {new int[]{1}, 1, new int[]{1}},
            {new int[]{1,-1}, 1, new int[]{1,-1}},
            {new int[]{9,11}, 2, new int[]{11}},
            {new int[]{4,-2}, 2, new int[]{4}},
            {new int[]{5,5,5,5}, 2, new int[]{5,5,5}},
            {new int[]{5,4,3,2,1}, 2, new int[]{5,4,3,2}},
            {new int[]{1,2,3,4,5}, 2, new int[]{2,3,4,5}},
            {new int[]{3,1,2}, 3, new int[]{3}},
            {new int[]{-7,-8,-3,-9,-1}, 3, new int[]{-3,-3,-1}}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            int k = (Integer) cases[i][1];
            int[] expected = (int[]) cases[i][2];
            int[] got = solve(nums, k);
            boolean ok = Arrays.equals(expected, got);
            if (ok) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected) + " got " + Arrays.toString(got));
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
