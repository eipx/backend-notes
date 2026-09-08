import java.util.PriorityQueue;

public class KthLargestElement {

    // Returns the kth largest element (k = 1 means the maximum), counting duplicates by position.
    public static int solve(int[] nums, int k) {
        // PriorityQueue is a MIN-heap by default in Java, which is exactly what we want here:
        // keep the k largest values seen so far, with the smallest of that group at the root.
        PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>(k);
        for (int num : nums) {
            minHeap.add(num);
            if (minHeap.size() > k) {
                minHeap.poll(); // evict the current smallest of the retained top-k group
            }
        }
        return minHeap.peek();
    }

    private static void check(int caseNum, int[] nums, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums, k);
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

        check(1, new int[]{3, 2, 1, 5, 6, 4}, 2, 5, fail, total);
        check(2, new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4, 4, fail, total);
        check(3, new int[]{1}, 1, 1, fail, total);
        check(4, new int[]{7, 6, 5, 4, 3, 2, 1}, 1, 7, fail, total);
        check(5, new int[]{7, 6, 5, 4, 3, 2, 1}, 7, 1, fail, total);
        check(6, new int[]{2, 2, 2, 2}, 2, 2, fail, total);
        check(7, new int[]{-1, -2, -3, -4}, 2, -2, fail, total);
        check(8, new int[]{5, 5, 5, 1, 1}, 3, 5, fail, total);
        check(9, new int[]{1, 2}, 2, 1, fail, total);
        check(10, new int[]{9, 3, 2, 4, 8}, 3, 4, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
