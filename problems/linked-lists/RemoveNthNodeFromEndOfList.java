// Remove Nth Node From End of List
// ref: LC 19
// Given the head of a singly linked list, remove the node that sits n
// positions before the end of the list (n counted with the last node as 1),
// and return the (possibly new) head of the resulting list.
// Required complexity: O(L) time (L = list length), O(1) extra space, single pass.
// Study page: ../remove-nth-node-from-end-of-list.md
// Run: javac --release 8 RemoveNthNodeFromEndOfList.java && java RemoveNthNodeFromEndOfList

import java.util.*;

public class RemoveNthNodeFromEndOfList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public static ListNode solve(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode fast = dummy;
        ListNode slow = dummy;
        // Push fast n+1 steps ahead of slow (starting both at dummy) so that
        // when fast falls off the end, slow sits just before the node to remove.
        for (int i = 0; i < n + 1; i++) {
            fast = fast.next; // guaranteed non-null when 1 <= n <= length, per constraints
        }
        while (fast != null) {
            fast = fast.next;
            slow = slow.next;
        }
        slow.next = slow.next.next;
        return dummy.next;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {intArr(1,2,3,4,5), 2, intArr(1,2,3,5)},
            {intArr(1), 1, intArr()},
            {intArr(1,2), 1, intArr(1)},
            {intArr(1,2), 2, intArr(2)},
            {intArr(1,2,3), 3, intArr(2,3)},
            {intArr(1,2,3,4,5), 5, intArr(2,3,4,5)},
            {intArr(1,1,1,1), 2, intArr(1,1,1)},
            {intArr(1,2,3,4,5,6), 1, intArr(1,2,3,4,5)},
            {intArr(-3,-2,-1,0,1), 3, intArr(-3,-2,0,1)},
            {intArr(10,20,30,40,50,60,70), 4, intArr(10,20,30,50,60,70)}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] vals = (int[]) cases[i][0];
            int n = (Integer) cases[i][1];
            int[] expected = (int[]) cases[i][2];
            try {
                ListNode head = buildList(vals);
                ListNode result = solve(head, n);
                List<Integer> got = toList(result);
                if (Arrays.equals(expected, toIntArray(got))) {
                    System.out.println("PASS");
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected) + " got " + got);
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected) + " got exception " + e);
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

    private static ListNode buildList(int[] vals) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int v : vals) {
            cur.next = new ListNode(v);
            cur = cur.next;
        }
        return dummy.next;
    }

    // Cycle-safe: stops if a node is revisited by identity, instead of looping forever on a buggy list.
    private static List<Integer> toList(ListNode head) {
        List<Integer> out = new ArrayList<Integer>();
        Set<ListNode> seen = Collections.newSetFromMap(new IdentityHashMap<ListNode, Boolean>());
        ListNode cur = head;
        while (cur != null && seen.add(cur)) {
            out.add(cur.val);
            cur = cur.next;
        }
        return out;
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] out = new int[list.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = list.get(i);
        }
        return out;
    }
}
