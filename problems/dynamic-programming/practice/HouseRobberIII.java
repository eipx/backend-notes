// House Robber III
// ref: LC 337
// The houses form a binary tree; each node holds some money. Two houses joined
// by a parent-child link cannot both be robbed. Given the root, return the
// maximum money that can be robbed.
// Input: the root of a tree with 1 to 10000 nodes, each value in 0..10000.
// Output: the largest total as an int.
// Example 1: tree [3,2,3,null,3,null,1] gives 7 (the root 3, the 3 under the
//            2, and the 1).
// Example 2: tree [3,4,5,1,3,null,1] gives 9 (the 4 and the 5).
// Trees are written in level order, with null for a missing child. The runner
// also tries the empty tree, which should give 0.
// Required: O(n) time, O(h) space (h is the height of the tree)
// Study page: ../house-robber-iii.md
// Run: javac --release 8 HouseRobberIII.java && java HouseRobberIII

import java.util.*;

class Solution {
    public int rob(HouseRobberIII.TreeNode root) {
        // TODO: implement
        return 0;
    }
}

public class HouseRobberIII {

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
        try {
            int got = new Solution().rob(build(values));
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
