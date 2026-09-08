// Reverse Nodes in k-Group
// ref: LC 25
// Given the head of a singly linked list, reverse the nodes k at a time and
// return the new head. If the number of nodes left at the end is fewer than
// k, leave that final short group exactly as it is (unreversed).
// Required complexity: O(L) time, O(1) extra space (pointer relinking only,
// no new nodes and no auxiliary array).
// Study page: ../../reverse-nodes-in-k-group.md
// Run: javac --release 8 ReverseNodesInKGroup.java && java ReverseNodesInKGroup

import java.util.*;

class Solution {
    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode reverseKGroup(ListNode head, int k) {
        // TODO: implement
        return null;
    }
}

public class ReverseNodesInKGroup {

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
                Solution.ListNode head = buildList(vals);
                Solution.ListNode result = new Solution().reverseKGroup(head, k);
                List<Integer> got = toList(result);
                if (Arrays.equals(expected, toIntArray(got))) {
                    System.out.println("PASS case " + (i + 1));
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

    private static Solution.ListNode buildList(int[] vals) {
        Solution.ListNode dummy = new Solution.ListNode(0);
        Solution.ListNode cur = dummy;
        for (int v : vals) {
            cur.next = new Solution.ListNode(v);
            cur = cur.next;
        }
        return dummy.next;
    }

    private static List<Integer> toList(Solution.ListNode head) {
        List<Integer> out = new ArrayList<Integer>();
        Set<Solution.ListNode> seen = Collections.newSetFromMap(new IdentityHashMap<Solution.ListNode, Boolean>());
        Solution.ListNode cur = head;
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
