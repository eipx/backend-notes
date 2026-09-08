public class TaskScheduler {

    // Returns the minimum number of intervals (including idle slots) to run all tasks,
    // given that identical task types must be separated by at least n intervals.
    public static int solve(char[] tasks, int n) {
        if (tasks.length == 0) {
            return 0;
        }
        int[] counts = new int[26];
        for (char c : tasks) {
            counts[c - 'A']++;
        }

        int maxFreq = 0;
        for (int c : counts) {
            if (c > maxFreq) {
                maxFreq = c;
            }
        }

        int maxFreqTaskCount = 0;
        for (int c : counts) {
            if (c == maxFreq) {
                maxFreqTaskCount++;
            }
        }

        // Frame the schedule around (maxFreq - 1) full gaps of size (n + 1), with the
        // last, partial frame holding just the tied most-frequent task types.
        int framedLength = (maxFreq - 1) * (n + 1) + maxFreqTaskCount;

        // If there are enough other distinct tasks to fill every gap, no idling is
        // needed at all, and the answer is simply the total task count.
        return Math.max(tasks.length, framedLength);
    }

    private static void check(int caseNum, char[] tasks, int n, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(tasks, n);
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
