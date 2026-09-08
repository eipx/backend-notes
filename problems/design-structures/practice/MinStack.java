/**
 * Min Stack
 * ref: LC 155
 *
 * Implement class MinStack: push(val) adds val to the top of the stack;
 * pop() removes the element on top; top() returns the element on top;
 * getMin() returns the smallest element currently in the stack. All four
 * operations must run in O(1) time.
 *
 * Note: the required class name (MinStack) coincides with this file's public
 * class name, so the fill-in methods below and the test runner live in one
 * class (Java forbids two top-level types sharing a name in one file) -
 * push/pop/top/getMin below are exactly the methods to implement.
 *
 * study page: ../min-stack.md
 * run: javac --release 8 MinStack.java && java MinStack
 */
import java.util.*;

public class MinStack {

    public MinStack() {
        // TODO: implement
    }

    public void push(int val) {
        // TODO: implement
    }

    public void pop() {
        // TODO: implement
    }

    public int top() {
        // TODO: implement
        return 0;
    }

    public int getMin() {
        // TODO: implement
        return 0;
    }

    // ---------------------------------------------------------------
    // test harness
    // ---------------------------------------------------------------
    static int[] counters = new int[2]; // {passed, total}

    // ops encode: "push v", "pop", "top", "getMin"; expected holds the value for
    // top/getMin steps and is ignored (Integer, can be null) for push/pop steps.
    static void runCase(int caseNum, String label, String[] ops, int[] vals, Integer[] expected) {
        counters[1]++;
        try {
            MinStack stack = new MinStack();
            for (int i = 0; i < ops.length; i++) {
                if (ops[i].equals("push")) {
                    stack.push(vals[i]);
                } else if (ops[i].equals("pop")) {
                    stack.pop();
                } else {
                    int got = ops[i].equals("top") ? stack.top() : stack.getMin();
                    int exp = expected[i];
                    if (got != exp) {
                        System.out.println("FAIL case " + caseNum + " (" + label + ") step " + i + ": expected " + exp + " got " + got);
                        return;
                    }
                }
            }
            counters[0]++;
            System.out.println("PASS case " + caseNum + " (" + label + ")");
        } catch (Exception e) {
            System.out.println("FAIL case " + caseNum + " (" + label + "): threw " + e);
        }
    }

    public static void main(String[] args) {
        // case 1: classic example
        runCase(1, "classic example",
                new String[]{"push", "push", "push", "getMin", "pop", "top", "getMin"},
                new int[]{-2, 0, -3, 0, 0, 0, 0},
                new Integer[]{null, null, null, -3, null, 0, -2});

        // case 2: single element - getMin and top agree
        runCase(2, "single element",
                new String[]{"push", "getMin", "top"},
                new int[]{5, 0, 0},
                new Integer[]{null, 5, 5});

        // case 3: pushing a new minimum repeatedly, then popping back through them
        runCase(3, "strictly decreasing pushes then pops",
                new String[]{"push", "push", "push", "getMin", "pop", "getMin", "pop", "getMin"},
                new int[]{3, 2, 1, 0, 0, 0, 0, 0},
                new Integer[]{null, null, null, 1, null, 2, null, 3});

        // case 4: duplicate minimum values - popping one copy must not lose the min
        runCase(4, "duplicate minimum values",
                new String[]{"push", "push", "push", "getMin", "pop", "getMin", "pop", "getMin"},
                new int[]{1, 1, 1, 0, 0, 0, 0, 0},
                new Integer[]{null, null, null, 1, null, 1, null, 1});

        // case 5: min stays the same across a larger push and pop that never touch the min element
        runCase(5, "min unaffected by larger pushes",
                new String[]{"push", "push", "push", "getMin", "pop", "getMin", "top"},
                new int[]{5, 1, 10, 0, 0, 0, 0},
                new Integer[]{null, null, null, 1, null, 1, 1});

        // case 6: negative and positive values mixed, including values far apart (delta overflow check)
        runCase(6, "wide value range for delta overflow safety",
                new String[]{"push", "push", "getMin", "top", "pop", "getMin", "top"},
                new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0, 0, 0, 0, 0},
                new Integer[]{null, null, Integer.MIN_VALUE, Integer.MIN_VALUE, null, Integer.MAX_VALUE, Integer.MAX_VALUE});

        System.out.println(counters[0] + "/" + counters[1] + " passed");
        if (counters[0] != counters[1]) System.exit(1);
    }
}
