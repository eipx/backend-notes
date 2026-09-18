import java.util.ArrayDeque;
import java.util.Deque;

public class MaximumSumBstInBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    // Post-order result for one subtree: whether it's a valid BST, and (if so)
    // its min value, max value, and sum -- everything a parent needs in O(1).
    private static class Info {
        boolean isBst;
        long min;
        long max;
        long sum;
        Info(boolean isBst, long min, long max, long sum) {
            this.isBst = isBst;
            this.min = min;
            this.max = max;
            this.sum = sum;
        }
    }

    public static int solve(TreeNode root) {
        long[] best = new long[]{0}; // floor: an empty "no subtree chosen" selection always sums to 0
        postorder(root, best);
        return (int) best[0];
    }

    private static Info postorder(TreeNode node, long[] best) {
        if (node == null) {
            return new Info(true, Long.MAX_VALUE, Long.MIN_VALUE, 0);
        }
        Info left = postorder(node.left, best);
        Info right = postorder(node.right, best);
        if (left.isBst && right.isBst && node.val > left.max && node.val < right.min) {
            long min = Math.min(node.val, left.min);
            long max = Math.max(node.val, right.max);
            long sum = left.sum + right.sum + node.val;
            best[0] = Math.max(best[0], sum);
            return new Info(true, min, max, sum);
        }
        return new Info(false, 0, 0, 0);
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

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new Integer[]{2,1,3}, 6 },
            { new Integer[]{1,4,3,2,4,2,5}, 10 },
            { new Integer[]{-1,-2,-3}, 0 },
            { new Integer[]{5}, 5 },
            { new Integer[]{1,null,2,null,3}, 6 },
            { new Integer[]{3,2,null,1}, 6 },
            { new Integer[]{5,1,4,null,null,3,6}, 13 },
            { new Integer[]{4,2,4}, 4 },
            { new Integer[]{10,5,15,3,7,12,20,1,4,6,8,11,13,18,25}, 158 },
            { new Integer[]{1,null,3,2}, 6 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            Integer[] values = (Integer[]) cases[i][0];
            int expected = (Integer) cases[i][1];
            TreeNode root = build(values);
            int actual = solve(root);
            if (Integer.compare(actual, expected) == 0) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
