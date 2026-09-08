// Sort List
// ref: LC 148
// Given the head of a singly linked list, sort it into ascending order and
// return the new head.
// Required complexity: O(L log L) time; the optimal approach uses O(1) extra
// space (no recursion, no auxiliary array).
// Study page: ../sort-list.md
// Run: javac --release 8 SortList.java && java SortList

import java.util.*;

public class SortList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public static ListNode solve(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        int n = 0;
        for (ListNode c = head; c != null; c = c.next) {
            n++;
        }
        ListNode dummy = new ListNode(0, head);
        // Bottom-up merge sort: merge runs of size `width`, doubling width each
        // pass, until width covers the whole list. No recursion, O(1) extra space.
        for (int width = 1; width < n; width *= 2) {
            ListNode prevTail = dummy;
            ListNode cur = dummy.next;
            while (cur != null) {
                ListNode left = cur;
                ListNode right = split(left, width);
                cur = split(right, width);
                prevTail = merge(left, right, prevTail);
            }
        }
        return dummy.next;
    }

    // Severs and returns the sublist starting n nodes after head (cutting head's
    // run down to exactly n nodes). Returns null if fewer than n nodes remain,
    // leaving head's run un-truncated (it was already shorter than n).
    private static ListNode split(ListNode head, int n) {
        ListNode cur = head;
        for (int i = 1; i < n && cur != null; i++) {
            cur = cur.next;
        }
        if (cur == null) {
            return null;
        }
        ListNode rest = cur.next;
        cur.next = null;
        return rest;
    }

    // Merges two already-isolated sorted runs, appending the result after tail.
    // Returns the new tail (last node of the merged run) for the caller to chain from.
    private static ListNode merge(ListNode l1, ListNode l2, ListNode tail) {
        ListNode cur = tail;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                cur.next = l1;
                l1 = l1.next;
            } else {
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }
        cur.next = (l1 != null) ? l1 : l2;
        while (cur.next != null) {
            cur = cur.next;
        }
        return cur;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {intArr(4,2,1,3), intArr(1,2,3,4)},
            {intArr(), intArr()},
            {intArr(1), intArr(1)},
            {intArr(2,1), intArr(1,2)},
            {intArr(1,2,3,4,5), intArr(1,2,3,4,5)},
            {intArr(5,4,3,2,1), intArr(1,2,3,4,5)},
            {intArr(3,3,3), intArr(3,3,3)},
            {intArr(-1,5,3,4,0), intArr(-1,0,3,4,5)},
            {intArr(1,3,2,3,1), intArr(1,1,2,3,3)},
            {intArr(9,1,8,2,7,3,6,4,5), intArr(1,2,3,4,5,6,7,8,9)}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] vals = (int[]) cases[i][0];
            int[] expected = (int[]) cases[i][1];
            try {
                ListNode head = buildList(vals);
                ListNode result = solve(head);
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
