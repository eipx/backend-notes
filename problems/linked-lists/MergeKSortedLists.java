// Merge k Sorted Lists
// ref: LC 23
// Given an array of k singly linked lists, each already sorted ascending,
// merge them into one sorted linked list and return its head.
// Required complexity: O(N log k) time (N = total nodes across all lists),
// O(k) extra space for the heap.
// Study page: ../merge-k-sorted-lists.md
// Run: javac --release 8 MergeKSortedLists.java && java MergeKSortedLists

import java.util.*;

public class MergeKSortedLists {

    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public static ListNode solve(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }
        // Min-heap ordered by node value; holds at most one node per non-empty
        // list at any time (that list's current smallest un-emitted node).
        PriorityQueue<ListNode> heap = new PriorityQueue<ListNode>(Math.max(1, lists.length),
            new Comparator<ListNode>() {
                public int compare(ListNode a, ListNode b) {
                    return Integer.compare(a.val, b.val);
                }
            });
        for (ListNode node : lists) {
            if (node != null) {
                heap.add(node);
            }
        }
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (!heap.isEmpty()) {
            ListNode smallest = heap.poll();
            tail.next = smallest;
            tail = tail.next;
            if (smallest.next != null) {
                heap.add(smallest.next);
            }
        }
        tail.next = null; // defensive: the last emitted node's stale next must not leak out
        return dummy.next;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {new int[][]{{1,4,5},{1,3,4},{2,6}}, intArr(1,1,2,3,4,4,5,6)},
            {new int[][]{}, intArr()},
            {new int[][]{{}}, intArr()},
            {new int[][]{{},{}}, intArr()},
            {new int[][]{{1,2,3}}, intArr(1,2,3)},
            {new int[][]{{5},{1},{3}}, intArr(1,3,5)},
            {new int[][]{{},{1,2,3}}, intArr(1,2,3)},
            {new int[][]{{1,1,1},{1,1}}, intArr(1,1,1,1,1)},
            {new int[][]{{-3,-1,0},{2,4},{-2}}, intArr(-3,-2,-1,0,2,4)},
            {new int[][]{{1,5,9},{2,6,10},{3,7,11},{4,8,12}}, intArr(1,2,3,4,5,6,7,8,9,10,11,12)}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[][] listsVals = (int[][]) cases[i][0];
            int[] expected = (int[]) cases[i][1];
            try {
                ListNode[] lists = new ListNode[listsVals.length];
                for (int j = 0; j < listsVals.length; j++) {
                    lists[j] = buildList(listsVals[j]);
                }
                ListNode result = solve(lists);
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
