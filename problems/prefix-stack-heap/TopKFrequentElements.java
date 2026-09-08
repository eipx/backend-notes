import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.TreeSet;

public class TopKFrequentElements {

    // Returns the k most frequent distinct values. Order in the returned array is not significant.
    public static int[] solve(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<Integer, Integer>();
        for (int num : nums) {
            // merge semantics: if num is absent, seed it with 1; otherwise add 1 to the existing count
            freq.merge(num, 1, Integer::sum);
        }

        // Min-heap of [value, count] pairs ordered by count, so the least-frequent kept
        // element is always at the root and can be evicted in O(log k).
        PriorityQueue<int[]> minHeap = new PriorityQueue<int[]>(new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Integer.compare(a[1], b[1]); // compare counts, never subtract for ordering-critical code
            }
        });

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            minHeap.add(new int[]{entry.getKey(), entry.getValue()});
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        int[] result = new int[minHeap.size()];
        int i = 0;
        for (int[] pair : minHeap) {
            result[i++] = pair[0];
        }
        return result;
    }

    // Order-independent comparison: convert both arrays to sorted sets, since the problem
    // guarantees a unique answer set of distinct values for every test case below.
    private static boolean sameSet(int[] a, int[] b) {
        TreeSet<Integer> setA = new TreeSet<Integer>();
        for (int x : a) setA.add(x);
        TreeSet<Integer> setB = new TreeSet<Integer>();
        for (int x : b) setB.add(x);
        return setA.equals(setB) && a.length == b.length;
    }

    private static void check(int caseNum, int[] nums, int k, int[] expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int[] got = solve(nums, k);
        if (sameSet(got, expected)) {
            System.out.println("PASS");
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + java.util.Arrays.toString(expected) + " got " + java.util.Arrays.toString(got));
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{1, 1, 1, 2, 2, 3}, 2, new int[]{1, 2}, fail, total);
        check(2, new int[]{1}, 1, new int[]{1}, fail, total);
        check(3, new int[]{4, 4, 4, 4, 5, 5, 6}, 1, new int[]{4}, fail, total);
        check(4, new int[]{7, 7, 8, 8, 8, 9, 9, 9, 9}, 2, new int[]{9, 8}, fail, total);
        check(5, new int[]{-1, -1, -2, -3, -3, -3}, 1, new int[]{-3}, fail, total);
        check(6, new int[]{5, 5, 5, 5, 5}, 1, new int[]{5}, fail, total);
        check(7, new int[]{1, 2, 2, 3, 3, 3, 4, 4, 4, 4}, 3, new int[]{4, 3, 2}, fail, total);
        check(8, new int[]{10, 20, 20, 30, 30, 30, 40, 40, 40, 40, 50}, 3, new int[]{40, 30, 20}, fail, total);
        check(9, new int[]{0, 0, 0, 1}, 2, new int[]{0, 1}, fail, total);
        check(10, new int[]{3, 3, 1, 1, 1, 2, 2, 2, 2}, 2, new int[]{2, 1}, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
