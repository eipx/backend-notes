// Add Two Numbers
// ref: LC 2
// Two non-negative integers are each stored in a singly linked list: one
// decimal digit per node, least significant digit first (so the list 2,4,3
// is the number 342). Add the two numbers and return the sum as a list in the
// same form. The numbers can be far larger than any built-in integer type, so
// work digit by digit instead of converting a list into a number.
// Input:  l1 and l2, the heads of the two lists (neither is null).
// Output: the head of a list holding the digits of the sum, least significant
//         digit first, no leading zeros (the number 0 is a single node 0).
// Constraints: each list has between 1 and 100 nodes; every node value is a
// digit from 0 to 9; a number has no leading zeros except the number 0 itself.
// Example 1: l1 = [2,4,3] (342) and l2 = [5,6,4] (465) give [7,0,8] (807).
// Example 2: l1 = [9,9,9] (999) and l2 = [1] (1) give [0,0,0,1] (1000).
// Required complexity: O(max(m, n)) time, where m and n are the list lengths.
// Run: javac --release 8 AddTwoNumbers.java && java AddTwoNumbers

import java.util.*;

class Solution {
    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        // TODO: implement
        return null;
    }
}

public class AddTwoNumbers {

    public static void main(String[] args) {
        Object[][] cases = {
            {intArr(2,4,3), intArr(5,6,4), intArr(7,0,8)},
            {intArr(0), intArr(0), intArr(0)},
            {intArr(5), intArr(5), intArr(0,1)},
            {intArr(9,9,9), intArr(1), intArr(0,0,0,1)},
            {intArr(9,9,9,9,9,9,9), intArr(9,9,9,9), intArr(8,9,9,9,0,0,0,1)},
            {intArr(1), intArr(9,9,1), intArr(0,0,2)},
            {intArr(1,8), intArr(0), intArr(1,8)},
            {intArr(0), intArr(7,3,2), intArr(7,3,2)},
            {intArr(0,0,1), intArr(0,0,2), intArr(0,0,3)},
            // the largest long, plus one: the sum no longer fits in a long
            {digits("9223372036854775807"), intArr(1), digits("9223372036854775808")},
            // 27 digits plus 23 digits
            {digits("123456789012345678901234567"), digits("98765432109876543210987"), digits("123555554444455555444445554")},
            // longest allowed lists: 100 nines plus 1, and 100 nines plus 100 nines
            {rep(9,100), intArr(1), concat(rep(0,100), intArr(1))},
            {rep(9,100), rep(9,100), concat(intArr(8), rep(9,99), intArr(1))}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] a = (int[]) cases[i][0];
            int[] b = (int[]) cases[i][1];
            int[] expected = (int[]) cases[i][2];
            try {
                Solution.ListNode result = new Solution().addTwoNumbers(buildList(a), buildList(b));
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

    // the digits of a decimal string, least significant digit first
    private static int[] digits(String s) {
        int[] out = new int[s.length()];
        for (int i = 0; i < out.length; i++) {
            out[i] = s.charAt(s.length() - 1 - i) - '0';
        }
        return out;
    }

    private static int[] rep(int digit, int count) {
        int[] out = new int[count];
        Arrays.fill(out, digit);
        return out;
    }

    private static int[] concat(int[]... parts) {
        int total = 0;
        for (int[] p : parts) {
            total += p.length;
        }
        int[] out = new int[total];
        int pos = 0;
        for (int[] p : parts) {
            System.arraycopy(p, 0, out, pos, p.length);
            pos += p.length;
        }
        return out;
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
        while (cur != null) {
            if (!seen.add(cur)) {
                throw new IllegalStateException("the returned list contains a cycle");
            }
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
