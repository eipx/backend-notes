import java.util.PriorityQueue;

public class MinimumCostToConnectSticks {

    // Minimum total cost to combine every stick into one, always combining
    // the two currently shortest sticks first.
    public static int solve(int[] sticks) {
        PriorityQueue<Integer> heap = new PriorityQueue<Integer>(); // natural ordering is already a min-heap
        for (int stick : sticks) {
            heap.add(stick);
        }

        long totalCost = 0; // long as a defensive margin against the worst-case accumulated sum
        while (heap.size() > 1) {
            int first = heap.poll();
            int second = heap.poll();
            int combined = first + second;
            totalCost += combined;
            heap.add(combined);
        }
        return (int) totalCost;
    }

    private static void check(int caseNum, int[] sticks, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(sticks);
        if (got == expected) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{2, 4, 3}, 14, fail, total);
        check(2, new int[]{1, 8, 3, 5}, 30, fail, total);
        check(3, new int[]{5}, 0, fail, total);
        check(4, new int[]{1, 1}, 2, fail, total);
        check(5, new int[]{1, 2, 3, 4}, 19, fail, total);
        check(6, new int[]{4, 3, 2, 1}, 19, fail, total);
        check(7, new int[]{10000, 10000, 10000}, 50000, fail, total);
        check(8, new int[]{1, 1, 1, 1}, 8, fail, total);
        check(9, new int[]{1, 2}, 3, fail, total);
        check(10, new int[]{3, 3, 3, 3, 3}, 36, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
