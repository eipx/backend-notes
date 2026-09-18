/**
 * Find Median from Data Stream
 * ref: LC 295
 *
 * Implement class MedianFinder: addNum(num) records a new number from a
 * stream; findMedian() returns the median of every number recorded so far
 * (findMedian is only ever called after at least one addNum). Required:
 * O(log n) per addNum, O(1) per findMedian.
 *
 * study page: ../find-median-from-data-stream.md
 * run: javac --release 8 FindMedianFromDataStream.java && java FindMedianFromDataStream
 */
import java.util.*;

class MedianFinder {

    public MedianFinder() {
        // TODO: implement
    }

    public void addNum(int num) {
        // TODO: implement
    }

    public double findMedian() {
        // TODO: implement
        return 0.0;
    }
}

public class FindMedianFromDataStream {

    private static void check(int caseNum, String label, String[] ops, int[] vals, Double[] expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
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
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + " (" + label + "): threw " + e);
        }
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
