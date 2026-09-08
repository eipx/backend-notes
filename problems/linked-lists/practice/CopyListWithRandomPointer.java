// Copy List with Random Pointer
// ref: LC 138
// Given the head of a singly linked list where each node additionally carries
// a "random" pointer that may point to any node in the list (or to null),
// build a completely independent deep copy of the list and return its head.
// No node in the copy may be the same object as any node in the original.
// Required complexity: O(L) time; the optimal approach uses O(1) extra space.
// Study page: ../../copy-list-with-random-pointer.md
// Run: javac --release 8 CopyListWithRandomPointer.java && java CopyListWithRandomPointer

import java.util.*;

class Solution {
    static class Node {
        int val;
        Node next;
        Node random;
        Node(int val) { this.val = val; }
    }

    public Node copyRandomList(Node head) {
        // TODO: implement
        return null;
    }
}

public class CopyListWithRandomPointer {

    public static void main(String[] args) {
        // Each case: values, and randomIdx[i] = index the i-th node's random
        // points to, or -1 for null.
        Object[][] cases = {
            {intArr(7,13,11,10,1), intArr(-1,0,4,2,0)},
            {intArr(1,2), intArr(1,1)},
            {intArr(3,3,3), intArr(-1,-1,-1)},
            {intArr(1), intArr(0)},
            {intArr(1), intArr(-1)},
            {intArr(), intArr()},
            {intArr(1,2,3,4), intArr(3,2,1,0)},
            {intArr(5,6), intArr(-1,0)},
            {intArr(10,20,30), intArr(1,2,0)},
            {intArr(4,4,4,4), intArr(2,3,0,1)}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] vals = (int[]) cases[i][0];
            int[] randomIdx = (int[]) cases[i][1];
            try {
                Solution.Node origHead = build(vals, randomIdx);
                Solution.Node copyHead = new Solution().copyRandomList(origHead);
                String failReason = null;
                if (!matchesSpec(origHead, vals, randomIdx)) {
                    failReason = "original list mutated";
                } else if (!matchesSpec(copyHead, vals, randomIdx)) {
                    failReason = "copy structure mismatch";
                } else if (!isIndependentCopy(origHead, copyHead)) {
                    failReason = "copy shares a node with the original";
                }
                if (failReason == null) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected valid independent deep copy got " + failReason);
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected valid independent deep copy got exception " + e);
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

    private static Solution.Node build(int[] vals, int[] randomIdx) {
        Solution.Node[] nodes = new Solution.Node[vals.length];
        for (int i = 0; i < vals.length; i++) {
            nodes[i] = new Solution.Node(vals[i]);
        }
        for (int i = 0; i < vals.length - 1; i++) {
            nodes[i].next = nodes[i + 1];
        }
        for (int i = 0; i < vals.length; i++) {
            nodes[i].random = (randomIdx[i] >= 0) ? nodes[randomIdx[i]] : null;
        }
        return vals.length > 0 ? nodes[0] : null;
    }

    private static List<Solution.Node> toNodeList(Solution.Node head) {
        List<Solution.Node> out = new ArrayList<Solution.Node>();
        Set<Solution.Node> seen = Collections.newSetFromMap(new IdentityHashMap<Solution.Node, Boolean>());
        Solution.Node cur = head;
        while (cur != null && seen.add(cur)) {
            out.add(cur);
            cur = cur.next;
        }
        return out;
    }

    private static int indexOfIdentity(List<Solution.Node> nodes, Solution.Node target) {
        if (target == null) {
            return -1;
        }
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) == target) {
                return i;
            }
        }
        return -1;
    }

    private static boolean matchesSpec(Solution.Node head, int[] vals, int[] randomIdx) {
        List<Solution.Node> nodes = toNodeList(head);
        if (nodes.size() != vals.length) {
            return false;
        }
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).val != vals[i]) {
                return false;
            }
            if (indexOfIdentity(nodes, nodes.get(i).random) != randomIdx[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isIndependentCopy(Solution.Node origHead, Solution.Node copyHead) {
        List<Solution.Node> origNodes = toNodeList(origHead);
        for (Solution.Node c = copyHead; c != null; c = c.next) {
            if (indexOfIdentity(origNodes, c) != -1) {
                return false;
            }
        }
        return true;
    }
}
