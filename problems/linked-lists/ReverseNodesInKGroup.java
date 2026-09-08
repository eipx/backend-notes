// Reverse Nodes in k-Group
// ref: LC 25
// Given the head of a singly linked list, reverse the nodes k at a time and
// return the new head. If the number of nodes left at the end is fewer than
// k, leave that final short group exactly as it is (unreversed).
// Required complexity: O(L) time, O(1) extra space (pointer relinking only,
// no new nodes and no auxiliary array).
// Study page: ../reverse-nodes-in-k-group.md
// Run: javac --release 8 ReverseNodesInKGroup.java && java ReverseNodesInKGroup

import java.util.*;

public class ReverseNodesInKGroup {

    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public static ListNode solve(ListNode head, int k) {
        ListNode dummy = new ListNode(0, head);
        ListNode groupPrev = dummy;
        while (true) {
            // Walk k steps from groupPrev to find the k-th node of the next group.
            ListNode kth = groupPrev;
            for (int i = 0; i < k && kth != null; i++) {
                kth = kth.next;
            }
            if (kth == null) {
                break; // fewer than k nodes remain; leave this tail group untouched
            }
            ListNode groupNext = kth.next;
            // Reverse groupPrev.next .. kth in place, stopping when cur reaches groupNext.
            ListNode prev = groupNext;
            ListNode cur = groupPrev.next;
            while (cur != groupNext) {
                ListNode next = cur.next;
                cur.next = prev;
                prev = cur;
                cur = next;
            }
            ListNode newGroupPrev = groupPrev.next; // old head, now the tail of the reversed group
            groupPrev.next = kth; // kth is now the head of the reversed group
            groupPrev = newGroupPrev;
        }
        return dummy.next;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {intArr(1,2,3,4,5), 2, intArr(2,1,4,3,5)},
            {intArr(1,2,3,4,5), 3, intArr(3,2,1,4,5)},
            {intArr(), 1, intArr()},
            {intArr(1), 1, intArr(1)},
            {intArr(1,2,3), 1, intArr(1,2,3)},
            {intArr(1,2,3), 3, intArr(3,2,1)},
            {intArr(1,2,3), 4, intArr(1,2,3)},
            {intArr(7,7,7,7,7), 2, intArr(7,7,7,7,7)},
            {intArr(1,2,3,4,5,6,7), 3, intArr(3,2,1,6,5,4,7)},
            {intArr(1,2), 2, intArr(2,1)}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] vals = (int[]) cases[i][0];
            int k = (Integer) cases[i][1];
            int[] expected = (int[]) cases[i][2];
            try {
                ListNode head = buildList(vals);
                ListNode result = solve(head, k);
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
