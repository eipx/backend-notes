// Maximum Sum BST in Binary Tree
// ref: LC 1373
// Given a binary tree, find the maximum sum of node values among all subtrees
// that are themselves valid binary search trees. If none has a positive sum,
// the answer is 0 (choosing no subtree at all is always available).
// Required complexity: O(n) time, O(h) space
// Study page: ../maximum-sum-bst-in-binary-tree.md
// Run: javac --release 8 MaximumSumBstInBinaryTree.java && java MaximumSumBstInBinaryTree

import java.util.*;

class Solution {
    public int maxSumBST(MaximumSumBstInBinaryTree.TreeNode root) {
        // TODO: implement
        return 0;
    }
}

public class MaximumSumBstInBinaryTree {

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
            try {
                int actual = new Solution().maxSumBST(root);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual);
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
