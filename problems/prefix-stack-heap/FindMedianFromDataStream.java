import java.util.PriorityQueue;

class MedianFinder {
    private final PriorityQueue<Integer> lowerHalf; // max-heap: largest of the lower half on top
    private final PriorityQueue<Integer> upperHalf; // min-heap: smallest of the upper half on top

    public MedianFinder() {
        lowerHalf = new PriorityQueue<Integer>((a, b) -> Integer.compare(b, a));
        upperHalf = new PriorityQueue<Integer>();
    }

    public void addNum(int num) {
        lowerHalf.add(num);
        upperHalf.add(lowerHalf.poll()); // keeps every lowerHalf value <= every upperHalf value
        if (upperHalf.size() > lowerHalf.size()) {
            lowerHalf.add(upperHalf.poll()); // rebalance: lowerHalf may hold at most one extra
        }
    }

    public double findMedian() {
        if (lowerHalf.size() > upperHalf.size()) {
            return lowerHalf.peek();
        }
        return (lowerHalf.peek() + upperHalf.peek()) / 2.0;
    }
}

public class FindMedianFromDataStream {

    private static void check(int caseNum, String label, String[] ops, int[] vals, Double[] expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        MedianFinder mf = new MedianFinder();
        for (int i = 0; i < ops.length; i++) {
            if (ops[i].equals("add")) {
                mf.addNum(vals[i]);
            } else {
                double got = mf.findMedian();
                double exp = expected[i];
                if (Math.abs(got - exp) > 1e-9) {
                    failCount[0]++;
                    System.out.println("FAIL case " + caseNum + " (" + label + ") step " + i + ": expected " + exp + " got " + got);
                    return;
                }
            }
        }
        System.out.println("PASS case " + caseNum + " (" + label + ")");
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, "increasing then odd count", new String[]{"add", "add", "find", "add", "find"},
                new int[]{1, 2, 0, 3, 0}, new Double[]{null, null, 1.5, null, 2.0}, fail, total);

        check(2, "single element", new String[]{"add", "find"},
                new int[]{5, 0}, new Double[]{null, 5.0}, fail, total);

        check(3, "out of order insertion", new String[]{"add", "add", "find"},
                new int[]{2, 1, 0}, new Double[]{null, null, 1.5}, fail, total);

        check(4, "all negative values", new String[]{"add", "add", "add", "find"},
                new int[]{-5, -1, -3, 0}, new Double[]{null, null, null, -3.0}, fail, total);

        check(5, "duplicate values", new String[]{"add", "add", "add", "find"},
                new int[]{1, 1, 1, 0}, new Double[]{null, null, null, 1.0}, fail, total);

        check(6, "even count average with .5", new String[]{"add", "add", "add", "add", "find"},
                new int[]{1, 2, 3, 4, 0}, new Double[]{null, null, null, null, 2.5}, fail, total);

        check(7, "repeated zero values", new String[]{"add", "find", "add", "find"},
                new int[]{0, 0, 0, 0}, new Double[]{null, 0.0, null, 0.0}, fail, total);

        check(8, "mixed sign values, odd count", new String[]{"add", "add", "add", "add", "add", "find"},
                new int[]{-1, 2, -3, 4, -5, 0}, new Double[]{null, null, null, null, null, -1.0}, fail, total);

        check(9, "extreme bound values", new String[]{"add", "add", "find"},
                new int[]{100000, -100000, 0}, new Double[]{null, null, 0.0}, fail, total);

        check(10, "median re-queried after every insertion", new String[]{"add", "find", "add", "find", "add", "find"},
                new int[]{3, 0, 1, 0, 2, 0}, new Double[]{null, 3.0, null, 2.0, null, 2.0}, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
