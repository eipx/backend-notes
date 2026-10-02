// Linked List Cycle
// ref: LC 141
// Given the head of a singly linked list, decide whether the list contains a
// cycle: whether following next pointers from the head ever reaches a node
// that was already visited. Return true if it does, false if the chain ends at
// null. The function receives only the head; nothing in the list records where
// a cycle starts. In the tests below, each row gives the node values and the
// index that the last node's next pointer is linked back to (-1 means the last
// node's next stays null).
// Input:  head, the first node of the list (null for an empty list).
// Output: true if some node can be reached twice by following next, else false.
// Constraints: 0 to 10000 nodes; node values are between -100000 and 100000
// and may repeat, so equal values say nothing about two nodes being the same.
// Example 1: values [3,2,0,-4] with the last node linked back to index 1
//            (the node holding 2) give true.
// Example 2: values [1,2] with the last node's next left as null give false.
// Required complexity: O(n) time, O(1) extra space, where n is the node count.
// Run: javac --release 8 LinkedListCycle.java && java LinkedListCycle

import java.util.*;

class Solution {
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    public boolean hasCycle(ListNode head) {
        // TODO: implement
        return false;
    }
}

public class LinkedListCycle {

    public static void main(String[] args) {
        Object[][] cases = {
            {intArr(3,2,0,-4), 1, true},
            {intArr(1,2), 0, true},
            {intArr(1), -1, false},
            {intArr(), -1, false},
            {intArr(1), 0, true},
            {intArr(1,2,3,4,5), 0, true},
            {intArr(1,2,3,4,5,6), 2, true},
            {intArr(1,2,3,4), 3, true},
            {intArr(7,7,7,7,7), -1, false},
            {intArr(1,2), 1, true},
            {intArr(4,4,4,4), -1, false},
            // largest allowed size: all values equal and no cycle
            {rep(7,10000), -1, false},
            // largest allowed size: distinct values, tail linked back to the middle
            {ramp(10000), 5000, true}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] vals = (int[]) cases[i][0];
            int pos = (Integer) cases[i][1];
            boolean expected = (Boolean) cases[i][2];
            try {
                Solution.ListNode[] nodes = buildNodes(vals);
                if (pos >= 0) {
                    nodes[nodes.length - 1].next = nodes[pos];
                }
                Solution.ListNode head = nodes.length > 0 ? nodes[0] : null;
                boolean got = new Solution().hasCycle(head);
                if (got == expected) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + got);
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

    private static int[] intArr(int... vals) {
        return vals;
    }

    private static int[] rep(int value, int count) {
        int[] out = new int[count];
        Arrays.fill(out, value);
        return out;
    }

    // count distinct values, centred on zero
    private static int[] ramp(int count) {
        int[] out = new int[count];
        for (int i = 0; i < count; i++) {
            out[i] = i - count / 2;
        }
        return out;
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
}
