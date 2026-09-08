// Linked List Cycle II
// ref: LC 142
// Given the head of a singly linked list that may contain a cycle, return the
// node where the cycle begins, or null if the list has no cycle. Do not modify
// the list, and do not use any structure whose size grows with the list except
// where explicitly noted as the baseline approach.
// Required complexity: O(L) time, O(1) extra space for the optimal approach.
// Study page: ../../linked-list-cycle-ii.md
// Run: javac --release 8 LinkedListCycleII.java && java LinkedListCycleII

import java.util.*;

class Solution {
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    public ListNode detectCycle(ListNode head) {
        // TODO: implement
        return null;
    }
}

public class LinkedListCycleII {

    public static void main(String[] args) {
        Object[][] cases = {
            {intArr(3,2,0,-4), 1, 1},
            {intArr(1,2), 0, 0},
            {intArr(1), 0, 0},
            {intArr(1), -1, -1},
            {intArr(), -1, -1},
            {intArr(1,2,3,4,5), -1, -1},
            {intArr(1,2,3,4,5), 4, 4},
            {intArr(1,2,3,4,5), 0, 0},
            {intArr(1,2,3), 2, 2},
            {intArr(5,-1,3,7,2,9), 3, 3}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] vals = (int[]) cases[i][0];
            int pos = (Integer) cases[i][1];
            int expectedIdx = (Integer) cases[i][2];
            try {
                Solution.ListNode[] nodes = buildNodes(vals);
                if (pos >= 0) {
                    nodes[nodes.length - 1].next = nodes[pos];
                }
                Solution.ListNode head = nodes.length > 0 ? nodes[0] : null;
                Solution.ListNode result = new Solution().detectCycle(head);
                int gotIdx = indexOfIdentity(nodes, result);
                if (gotIdx == expectedIdx) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected index " + expectedIdx + " got index " + gotIdx);
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected index " + expectedIdx + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }

    private static int[] intArr(int... vals) {
        return vals;
    }

    private static Solution.ListNode[] buildNodes(int[] vals) {
        Solution.ListNode[] nodes = new Solution.ListNode[vals.length];
        for (int i = 0; i < vals.length; i++) {
            nodes[i] = new Solution.ListNode(vals[i]);
        }
        for (int i = 0; i < vals.length - 1; i++) {
            nodes[i].next = nodes[i + 1];
        }
        return nodes;
    }

    private static int indexOfIdentity(Solution.ListNode[] nodes, Solution.ListNode target) {
        if (target == null) {
            return -1;
        }
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == target) {
                return i;
            }
        }
        return -1;
    }
}
