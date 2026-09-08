// Copy List with Random Pointer
// ref: LC 138
// Given the head of a singly linked list where each node additionally carries
// a "random" pointer that may point to any node in the list (or to null),
// build a completely independent deep copy of the list and return its head.
// No node in the copy may be the same object as any node in the original.
// Required complexity: O(L) time; the optimal approach uses O(1) extra space.
// Study page: ../copy-list-with-random-pointer.md
// Run: javac --release 8 CopyListWithRandomPointer.java && java CopyListWithRandomPointer

import java.util.*;

public class CopyListWithRandomPointer {

    static class Node {
        int val;
        Node next;
        Node random;
        Node(int val) { this.val = val; }
    }

    public static Node solve(Node head) {
        if (head == null) {
            return null;
        }
        // Pass 1: interleave a copy after every original node.
        // orig1 -> copy1 -> orig2 -> copy2 -> ... ; invariant after this pass:
        // for every original node x, x.next == copy(x).
        for (Node orig = head; orig != null; orig = orig.next.next) {
            Node copy = new Node(orig.val);
            copy.next = orig.next;
            orig.next = copy;
        }
        // Pass 2: wire up random pointers using the invariant above.
        for (Node orig = head; orig != null; orig = orig.next.next) {
            Node copy = orig.next;
            copy.random = (orig.random != null) ? orig.random.next : null;
        }
        // Pass 3: de-interleave — restore the original list and extract the copy list.
        Node copyHead = head.next;
        for (Node orig = head; orig != null; orig = orig.next) {
            Node copy = orig.next;
            orig.next = copy.next;
            copy.next = (copy.next != null) ? copy.next.next : null;
        }
        return copyHead;
    }

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
                Node origHead = build(vals, randomIdx);
                Node copyHead = solve(origHead);
                String failReason = null;
                if (!matchesSpec(origHead, vals, randomIdx)) {
                    failReason = "original list mutated";
                } else if (!matchesSpec(copyHead, vals, randomIdx)) {
                    failReason = "copy structure mismatch";
                } else if (!isIndependentCopy(origHead, copyHead)) {
                    failReason = "copy shares a node with the original";
                }
                if (failReason == null) {
                    System.out.println("PASS");
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

    private static Node build(int[] vals, int[] randomIdx) {
        Node[] nodes = new Node[vals.length];
        for (int i = 0; i < vals.length; i++) {
            nodes[i] = new Node(vals[i]);
        }
        for (int i = 0; i < vals.length - 1; i++) {
            nodes[i].next = nodes[i + 1];
        }
        for (int i = 0; i < vals.length; i++) {
            nodes[i].random = (randomIdx[i] >= 0) ? nodes[randomIdx[i]] : null;
        }
        return vals.length > 0 ? nodes[0] : null;
    }

    private static List<Node> toNodeList(Node head) {
        List<Node> out = new ArrayList<Node>();
        Set<Node> seen = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
        Node cur = head;
        while (cur != null && seen.add(cur)) {
            out.add(cur);
            cur = cur.next;
        }
        return out;
    }

    private static int indexOfIdentity(List<Node> nodes, Node target) {
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

    // Confirms head's chain matches the given vals/randomIdx spec exactly.
    private static boolean matchesSpec(Node head, int[] vals, int[] randomIdx) {
        List<Node> nodes = toNodeList(head);
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

    // No node of the copy may be the exact same object as any node of the original.
    private static boolean isIndependentCopy(Node origHead, Node copyHead) {
        List<Node> origNodes = toNodeList(origHead);
        for (Node c = copyHead; c != null; c = c.next) {
            if (indexOfIdentity(origNodes, c) != -1) {
                return false;
            }
        }
        return true;
    }
}
