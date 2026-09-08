import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LC 155 - a stack that supports push, pop, top, and getMin, all in O(1) time.
 * Two implementations:
 *   1) MinStackTwoStacks - a value stack plus a parallel min-tracking stack
 *      (equivalent in cost to a single stack of {value, currentMin} pairs)
 *   2) MinStackDelta      - a single stack storing (value - runningMin) deltas,
 *      the classic O(1)-extra-space follow-up
 */
public class MinStack {

    interface IMinStack {
        void push(int val);
        void pop();
        int top();
        int getMin();
    }

    // ---------------------------------------------------------------
    // Version A: two parallel stacks
    // ---------------------------------------------------------------
    static class MinStackTwoStacks implements IMinStack {
        private final Deque<Integer> values;
        private final Deque<Integer> mins; // mins.peek() is always the min of everything below it, inclusive

        MinStackTwoStacks() {
            values = new ArrayDeque<Integer>();
            mins = new ArrayDeque<Integer>();
        }

        @Override
        public void push(int val) {
            values.push(val);
            // Push a duplicate onto mins even on a tie, so pop() cannot pop the wrong min later.
            if (mins.isEmpty() || val <= mins.peek()) {
                mins.push(val);
            } else {
                mins.push(mins.peek());
            }
        }

        @Override
        public void pop() {
            values.pop();
            mins.pop();
        }

        @Override
        public int top() {
            return values.peek();
        }

        @Override
        public int getMin() {
            return mins.peek();
        }
    }

    // ---------------------------------------------------------------
    // Version B: single stack of deltas (val - currentMin), O(1) extra space
    // ---------------------------------------------------------------
    static class MinStackDelta implements IMinStack {
        // long avoids overflow: val - minVal can exceed the int range when val and
        // minVal sit near opposite ends of Integer.MIN_VALUE..MAX_VALUE.
        private final Deque<Long> deltas;
        private long minVal;

        MinStackDelta() {
            deltas = new ArrayDeque<Long>();
        }

        @Override
        public void push(int val) {
            if (deltas.isEmpty()) {
                deltas.push(0L);
                minVal = val;
            } else {
                long delta = (long) val - minVal;
                deltas.push(delta);
                if (delta < 0) minVal = val; // val is a new minimum
            }
        }

        @Override
        public void pop() {
            long delta = deltas.pop();
            if (delta < 0) {
                minVal = minVal - delta; // undo: recover the min that was active before this push
            }
            // if delta >= 0, minVal was not changed by this push, so it needs no update
        }

        @Override
        public int top() {
            long delta = deltas.peek();
            if (delta < 0) {
                return (int) minVal; // this element WAS the minimum when pushed
            } else {
                return (int) (minVal + delta);
            }
        }

        @Override
        public int getMin() {
            return (int) minVal;
        }
    }

    // ---------------------------------------------------------------
    // test harness
    // ---------------------------------------------------------------
    static int[] counters = new int[2];

    // ops encode: "push v", "pop", "top", "getMin"; expected holds the value for
    // top/getMin steps and is ignored (Integer, can be null) for push/pop steps.
    static void runCase(int caseNum, String label, String[] ops, int[] vals, Integer[] expected) {
        runOnImpl(caseNum, label + " [TwoStacks]", ops, vals, expected, new MinStackTwoStacks());
        runOnImpl(caseNum, label + " [Delta]", ops, vals, expected, new MinStackDelta());
    }

    static void runOnImpl(int caseNum, String label, String[] ops, int[] vals, Integer[] expected, IMinStack stack) {
        counters[1]++;
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
