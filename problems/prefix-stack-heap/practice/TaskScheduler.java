// Task Scheduler
// ref: LC 621
// Given task labels (single characters) and a cooldown n, identical task
// types must be separated by at least n intervals; each interval runs one
// task or sits idle. Return the minimum total intervals to run every task.
// Required: O(m) time, O(1) space (m = tasks.length, fixed 26-letter alphabet).
// Study page: ../task-scheduler.md
// Run: javac --release 8 TaskScheduler.java && java TaskScheduler

import java.util.*;

class Solution {
    public int leastInterval(char[] tasks, int n) {
        // TODO: implement
        return -1;
    }
}

public class TaskScheduler {

    private static void check(int caseNum, char[] tasks, int n, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().leastInterval(tasks, n);
            if (Integer.compare(got, expected) == 0) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new char[]{'A', 'A', 'A', 'B', 'B', 'B'}, 2, 8, fail, total);
        check(2, new char[]{'A', 'A', 'A', 'B', 'B', 'B'}, 0, 6, fail, total);
        check(3, new char[]{'A', 'A', 'A', 'A', 'A', 'A', 'B', 'C', 'D', 'E', 'F', 'G'}, 2, 16, fail, total);
        check(4, new char[]{'A'}, 5, 1, fail, total);
        check(5, new char[]{'A', 'A'}, 0, 2, fail, total);
        check(6, new char[]{'A', 'A'}, 1, 3, fail, total);
        check(7, new char[]{'A', 'A', 'A', 'B', 'B', 'C'}, 2, 7, fail, total);
        check(8, new char[]{'A', 'B', 'C', 'D'}, 2, 4, fail, total);
        check(9, new char[]{}, 2, 0, fail, total);
        check(10, new char[]{'A', 'A', 'A', 'A'}, 3, 13, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
