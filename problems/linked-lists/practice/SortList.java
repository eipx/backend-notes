// Sort List
// ref: LC 148
// Given the head of a singly linked list, sort it into ascending order and
// return the new head.
// Required complexity: O(L log L) time; the optimal approach uses O(1) extra
// space (no recursion, no auxiliary array).
// Study page: ../../sort-list.md
// Run: javac --release 8 SortList.java && java SortList

import java.util.*;

class Solution {
    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode sortList(ListNode head) {
        // TODO: implement
        return null;
    }
}

public class SortList {

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
                Solution.ListNode head = buildList(vals);
                Solution.ListNode result = new Solution().sortList(head);
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
