// Lowest Common Ancestor of a Binary Tree
// ref: LC 236
// Given a binary tree and two different nodes p and q that both belong to it,
// return the node that is the lowest (deepest) one having both p and q somewhere
// in its subtree. A node counts as being in its own subtree, so if p sits above
// q, the answer is p itself.
// Input: the root of a tree with 2 to 100000 nodes whose values are all distinct,
// plus the nodes p and q. The test runner is given the tree as a level-order
// array and two values, and looks up the two nodes by value.
// Output: the lowest common node itself (the runner checks its value).
// Example 1: tree [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1 gives the node
//            with value 3.
// Example 2: same tree, p = 5, q = 4 gives the node with value 5, because 4 is
//            inside the subtree of 5.
// Trees are written in level order, with null for a missing child. The values
// are not arranged as a search tree, so do not rely on their order.
// Required complexity: O(n) time, O(h) space (h is the height of the tree)
// Run: javac --release 8 LowestCommonAncestorOfABinaryTree.java && java LowestCommonAncestorOfABinaryTree

import java.util.*;

class Solution {
    public LowestCommonAncestorOfABinaryTree.TreeNode lowestCommonAncestor(
            LowestCommonAncestorOfABinaryTree.TreeNode root,
            LowestCommonAncestorOfABinaryTree.TreeNode p,
            LowestCommonAncestorOfABinaryTree.TreeNode q) {
        // TODO: implement
        return null;
    }
}

public class LowestCommonAncestorOfABinaryTree {

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

    // Breadth-first search for the node holding the given value.
    private static TreeNode find(TreeNode root, int val) {
        Deque<TreeNode> queue = new ArrayDeque<TreeNode>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();
            if (current.val == val) {
                return current;
            }
            if (current.left != null) {
                queue.add(current.left);
            }
            if (current.right != null) {
                queue.add(current.right);
            }
        }
        return null;
    }

    // Level-order array of a complete tree with n nodes; the node in position i
    // (1-based) holds the value i, so the parent of value v is v / 2.
    private static Integer[] completeCounting(int n) {
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) {
            values[i] = i + 1;
        }
        return values;
    }

    // Level-order array of a chain of n nodes, each the right child of the one
    // before it. The node at depth d (root is depth 1) holds (37 * d) % 1009,
    // so the values are distinct and not in any sorted order.
    private static Integer[] scrambledRightChain(int n) {
        Integer[] values = new Integer[2 * n - 1];
        values[0] = 37 % 1009;
        for (int d = 2; d <= n; d++) {
            values[2 * d - 3] = null;
            values[2 * d - 2] = (37 * d) % 1009;
        }
        return values;
    }

    private static void check(int caseNum, Integer[] values, int pVal, int qVal, int expected,
                              int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            TreeNode root = build(values);
            TreeNode p = find(root, pVal);
            TreeNode q = find(root, qVal);
            TreeNode got = new Solution().lowestCommonAncestor(root, p, q);
            if (got == null) {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got null");
            } else if (got.val == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got.val);
            }
        } catch (Exception | StackOverflowError e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        Integer[] sample = new Integer[]{3, 5, 1, 6, 2, 0, 8, null, null, 7, 4};
        Integer[] perfect15 = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        Integer[] signed = new Integer[]{0, -1, 5, -3, null, 4, 9};

        check(1, sample, 5, 1, 3, fail, total);
        check(2, sample, 5, 4, 5, fail, total);
        check(3, new Integer[]{1, 2}, 1, 2, 1, fail, total);
        check(4, new Integer[]{1, null, 2}, 2, 1, 1, fail, total);
        check(5, perfect15, 8, 9, 4, fail, total);
        check(6, perfect15, 8, 11, 2, fail, total);
        check(7, perfect15, 8, 15, 1, fail, total);
        check(8, new Integer[]{1, 2, null, 3, null, 4, null, 5}, 5, 3, 3, fail, total);
        check(9, new Integer[]{5, null, 3, null, 8, null, 1, null, 9}, 1, 9, 1, fail, total);
        check(10, signed, -3, 4, 0, fail, total);
        check(11, signed, 4, 9, 5, fail, total);
        check(12, completeCounting(100000), 99999, 100000, 1562, fail, total);
        check(13, scrambledRightChain(1000), 676, 2, 2, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
