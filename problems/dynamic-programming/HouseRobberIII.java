import java.util.ArrayDeque;
import java.util.Deque;

public class HouseRobberIII {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    // What a subtree reports to its parent: the best total inside the subtree
    // when its root is taken, and the best total when its root is not taken.
    // The fields never change after construction, so one shared instance can
    // stand for every empty subtree.
    private static class Pair {
        final int robbed;
        final int skipped;
        Pair(int robbed, int skipped) {
            this.robbed = robbed;
            this.skipped = skipped;
        }
    }

    // An empty subtree has no money, whether or not its (missing) root is taken.
    private static final Pair EMPTY = new Pair(0, 0);

    // Largest total that can be taken from the houses of a binary tree when a
    // house and its parent house may not both be taken.
    public static int solve(TreeNode root) {
        Pair result = visit(root);
        return Math.max(result.robbed, result.skipped);
    }

    // Post-order: both children are finished before their parent is combined.
    private static Pair visit(TreeNode node) {
        if (node == null) {
            return EMPTY;
        }
        Pair left = visit(node.left);
        Pair right = visit(node.right);

        // Take this node: both children must be skipped, but everything below
        // them is free to choose.
        int robbed = node.val + left.skipped + right.skipped;
        // Skip this node: each child is free to be taken or skipped, whichever
        // is better for its own subtree.
        int skipped = Math.max(left.robbed, left.skipped) + Math.max(right.robbed, right.skipped);
        return new Pair(robbed, skipped);
    }

    // Standard level-order tree builder: null entries mark a missing child, and
    // that position's subtree is simply not expanded further.
    private static TreeNode build(Integer[] values) {
        if (values.length == 0 || values[0] == null) {
            return null;
        }
        TreeNode root = new TreeNode(values[0]);
        Deque<TreeNode> queue = new ArrayDeque<TreeNode>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < values.length) {
            TreeNode current = queue.poll();
            if (i < values.length) {
                Integer leftVal = values[i++];
                if (leftVal != null) {
                    current.left = new TreeNode(leftVal);
                    queue.add(current.left);
                }
            }
            if (i < values.length) {
                Integer rightVal = values[i++];
                if (rightVal != null) {
                    current.right = new TreeNode(rightVal);
                    queue.add(current.right);
                }
            }
        }
        return root;
    }

    // Level-order array of a complete tree with n nodes, every value equal to val.
    private static Integer[] complete(int n, int val) {
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) {
            values[i] = val;
        }
        return values;
    }

    // Level-order array of a tree that is a single chain of n nodes, each node
    // the left child of the one before it, every value equal to val.
    private static Integer[] leftChain(int n, int val) {
        Integer[] values = new Integer[2 * n - 1];
        values[0] = val;
        for (int d = 1; d < n; d++) {
            values[2 * d - 1] = val;
            values[2 * d] = null;
        }
        return values;
    }

    private static void check(int caseNum, Integer[] values, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(build(values));
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

        check(1, new Integer[]{3, 2, 3, null, 3, null, 1}, 7, fail, total);
        check(2, new Integer[]{3, 4, 5, 1, 3, null, 1}, 9, fail, total);
        check(3, new Integer[]{}, 0, fail, total);
        check(4, new Integer[]{5}, 5, fail, total);
        check(5, new Integer[]{0, 0, 0}, 0, fail, total);
        check(6, new Integer[]{2, 1, 3}, 4, fail, total);
        check(7, new Integer[]{10, 1, 1}, 10, fail, total);
        check(8, new Integer[]{4, 1, null, 2, null, 3}, 7, fail, total);
        check(9, new Integer[]{3, null, 4, null, 3}, 6, fail, total);
        check(10, new Integer[]{2, 10, 1, 1, 1, 5, 5}, 20, fail, total);
        check(11, complete(15, 1), 10, fail, total);
        check(12, leftChain(1000, 1), 500, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
