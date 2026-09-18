public class RemoveKDigits {

    // Deletes exactly k digits from num (order preserved) to make the smallest
    // possible remaining number, with no leading zeros (or "0" if empty).
    public static String solve(String num, int k) {
        StringBuilder stack = new StringBuilder(); // used as a stack: append = push, deleteCharAt(end) = pop
        for (int i = 0; i < num.length(); i++) {
            char digit = num.charAt(i);
            while (k > 0 && stack.length() > 0 && stack.charAt(stack.length() - 1) > digit) {
                stack.deleteCharAt(stack.length() - 1);
                k--;
            }
            stack.append(digit);
        }
        // Any deletions not yet used must come off the end: the stack is
        // non-decreasing at this point, so the tail holds the least useful digits.
        stack.setLength(stack.length() - k);

        // Strip leading zeros, but keep at least one character.
        int start = 0;
        while (start < stack.length() - 1 && stack.charAt(start) == '0') {
            start++;
        }
        String result = stack.substring(start);
        return result.isEmpty() ? "0" : result;
    }

    private static void check(int caseNum, String num, int k, String expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        String got = solve(num, k);
        if (got.equals(expected)) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected \"" + expected + "\" got \"" + got + "\"");
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, "1432219", 3, "1219", fail, total);
        check(2, "10200", 1, "200", fail, total);
        check(3, "10", 2, "0", fail, total);
        check(4, "9", 1, "0", fail, total);
        check(5, "112", 1, "11", fail, total);
        check(6, "1234567890", 9, "0", fail, total);
        check(7, "10", 1, "0", fail, total);
        check(8, "5337", 2, "33", fail, total);
        check(9, "100", 1, "0", fail, total);
        check(10, "1111111", 3, "1111", fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
