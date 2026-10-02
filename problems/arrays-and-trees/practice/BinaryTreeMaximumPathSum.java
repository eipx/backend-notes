// Binary Tree Maximum Path Sum
// ref: LC 124
// A path in a binary tree is a chain of nodes where each adjacent pair is joined
// by a parent-child edge and no node is used twice. The chain may start and end
// at any nodes and does not have to pass through the root. The sum of a path is
// the total of its node values. Given the root of a tree, return the largest sum
// over all non-empty paths.
// Input: the root of a tree with 1 to 30000 nodes, each value in -1000..1000.
// Output: the maximum path sum, as an int.
// Example 1: tree [1,2,3] gives 6 (the path 2, 1, 3).
// Example 2: tree [-10,9,20,null,null,15,7] gives 42 (the path 15, 20, 7; the
//            root is not used).
// Example 3: tree [-3] gives -3 (a path needs at least one node, so the answer
//            can be negative).
// Trees are written in level order, with null for a missing child.
// Required complexity: O(n) time, O(h) space (h is the height of the tree)
// Run: javac --release 8 BinaryTreeMaximumPathSum.java && java BinaryTreeMaximumPathSum

import java.util.*;

class Solution {
    public int maxPathSum(BinaryTreeMaximumPathSum.TreeNode root) {
        // TODO: implement
        return 0;
    }
}

public class BinaryTreeMaximumPathSum {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
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

    // Level-order array of a tree that is a single chain of n nodes, each node the
    // right child of the one before it. The node at depth d (root is depth 0)
    // holds evenVal when d is even and oddVal when d is odd.
    private static Integer[] rightChain(int n, int evenVal, int oddVal) {
        Integer[] values = new Integer[2 * n - 1];
        values[0] = evenVal;
        for (int d = 1; d < n; d++) {
            values[2 * d - 1] = null;
            values[2 * d] = (d % 2 == 0) ? evenVal : oddVal;
        }
        return values;
    }

    private static void check(int caseNum, Integer[] values, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().maxPathSum(build(values));
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception | StackOverflowError e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new Integer[]{1, 2, 3}, 6, fail, total);
        check(2, new Integer[]{-10, 9, 20, null, null, 15, 7}, 42, fail, total);
        check(3, new Integer[]{-3}, -3, fail, total);
        check(4, new Integer[]{-1000}, -1000, fail, total);
        check(5, new Integer[]{-5, -2, -9, -3, -7}, -2, fail, total);
        check(6, new Integer[]{-1, -2, -3}, -1, fail, total);
        check(7, new Integer[]{1, -2, -3, 1, 3, -2, null, -1}, 3, fail, total);
        check(8, new Integer[]{100, 1, null, 10, 10}, 111, fail, total);
        check(9, new Integer[]{1, null, 2, null, 3, null, 4}, 10, fail, total);
        check(10, new Integer[]{1, -5, null, 10}, 10, fail, total);
        check(11, new Integer[]{-1000, 1000, 1000}, 1000, fail, total);
        check(12, rightChain(1000, 5, -1), 2001, fail, total);
        check(13, complete(30000, 1000), 29000, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
