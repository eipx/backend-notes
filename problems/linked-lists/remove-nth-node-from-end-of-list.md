# Remove Nth Node From End of List
`ref: LC 19` · Difficulty: Medium · Pattern: two pointers with a fixed gap of n, dummy head to erase the head-removal special case

## Problem

Given the head of a singly linked list, remove the single node that sits `n` positions before the end of the list, where the last node itself counts as position 1 from the end, and return the head of the resulting list. The list is defined only by `next` links — no random pointers, no cycles. It is guaranteed that `n` is between 1 and the number of nodes in the list, inclusive, so the target always names a real node, never past either end. If the removed node happens to be the current head, the returned head must be the node that follows it (or `null`, if the list had exactly one node).

Input: `head` (the first node of a singly linked list) and an integer `n`.
Output: the head of the list after the target node has been unlinked; the removed node's own `next` field afterward is irrelevant.

## Constraints

- The list holds up to `3 * 10^4` nodes.
- `1 <= n <=` the number of nodes in the list — `n` is always in range, so there is no need to defend against "remove more nodes than exist."
- Node values are ordinary 32-bit ints; nothing here is summed or multiplied, so there is no overflow concern.
- The constraint that shapes the intended solution: do it in one pass. A two-pass solution (count the length, then walk to the removal point) is easy and correct, but a single combined traversal is the target.

## Worked examples

1. `head=[1,2,3,4,5]`, `n=2` → `[1,2,3,5]`. Counting from the tail, node `4` is 2nd from the end; removing it leaves `1,2,3,5`.
2. `head=[1]`, `n=1` → `[]`. The only node is also the 1st (and last) from the end; removing it empties the list, and the returned head must be `null`, not a dangling reference to the removed node.
3. `head=[1,2,3]`, `n=3` → `[2,3]`. Here `n` equals the list length, which makes the target the head itself — the removal has to work correctly even though there is no real node "before" the target to unlink from (a dummy predecessor standing in for that missing node is exactly why the dummy-head idiom exists).
4. `head=[10,20,30,40,50,60,70]`, `n=4` → `[10,20,30,50,60,70]`. Counting from the tail: `70` (1st), `60` (2nd), `50` (3rd), `40` (4th) — so `40` is the one removed, leaving everything on both sides untouched.

## Edge cases checklist

- Single node, `n=1` — removing the only node must yield an empty list (`null` head), not a list still containing a stale node.
- `n` equal to the list length — the node being removed is the head; the new head must become the old second node.
- `n=1` on a longer list — removes the tail; the new tail's `next` must be `null` (it already is, but a rebuilt-node implementation could get this wrong).
- All node values equal — the algorithm must key off structural position, never value, so duplicate values must never change which physical node gets unlinked.
- Two-node list, removing either the first (`n=2`) or the second (`n=1`).
- Negative and mixed-sign values — removal is purely positional, so sign should never matter, but it is worth confirming nothing accidentally branches on it.
- A larger list where the target sits in the interior, not at either boundary.

## Approach

### Brute force

Traverse the whole list once just to count its length `L`. Since the target is `n` from the end, its 0-indexed position from the head is `L - n`. Traverse again from a dummy node placed before `head`, moving `L - n` steps to land on the node just before the target, then unlink the following node. This is correct and O(L) time, but it touches the list in two separate passes, and it still needs the same dummy-head idiom the optimal approach uses, for the same reason — to avoid a special case when the head itself is the node being removed.

### Optimal

Use two pointers, both starting at a dummy node placed immediately before `head` (so "the node before the head" always exists, even when the head itself must be removed). Advance a `fast` pointer `n + 1` steps ahead of a `slow` pointer. Then advance both one step at a time together until `fast` runs off the end (`fast == null`). At that point `slow` sits exactly at the node before the one that must be removed — found in a single combined pass.

**Key invariant:** once the initial `n + 1`-step head start is done, the gap between `fast` and `slow` stays fixed at exactly `n + 1` nodes for the rest of the run. Since `fast` starts `n + 1` nodes ahead and both pointers move one node per iteration together, that gap never changes — a fixed gap walked forward in lockstep is exactly what lets a single pass locate a position that is measured from the *end* of a list whose length was never explicitly counted.

Proof sketch: let the dummy be node `0` and the real nodes be `1..L`. After the initial loop, `slow` is still at node `0` and `fast` is at node `n + 1` — a gap of `n + 1`. Each iteration of the second loop moves both pointers forward by exactly one node, so the gap of `n + 1` between them is an invariant that never changes throughout that loop. The loop stops exactly when `fast` becomes `null`, i.e., when `fast` has advanced one position past the last real node (position `L + 1`, which does not exist and reads as `null`). Since the gap is invariantly `n + 1`, at that moment `slow` is at position `(L + 1) - (n + 1) = L - n`, which is precisely the node immediately before the node at position `L - n + 1` — and the node at position `L - n + 1` (1-indexed from the head) is exactly the node that sits `n` positions from the end. So `slow.next` is always the correct removal target, and `slow.next = slow.next.next` unlinks it.

### Step-by-step trace

Trace on `head=[1,2,3,4,5]`, `n=2`, expected `[1,2,3,5]`. Nodes are named by value for readability; `D` is the dummy.

| step | fast | slow | action |
|---|---|---|---|
| start | D | D | both at dummy |
| init loop i=0 | 1 | D | fast steps once |
| init loop i=1 | 2 | D | fast steps again |
| init loop i=2 (3 steps total, n+1=3) | 3 | D | fast has now taken 3 steps total |
| main loop | 4 | 1 | both step forward together |
| main loop | 5 | 2 | both step forward together |
| main loop | null | 3 | fast falls off the end; loop stops |
| unlink | — | 3 | `slow.next` (`4`) is replaced by `slow.next.next` (`5`), so node `4` is skipped |

Final list: `1 -> 2 -> 3 -> 5`, matching the expected output.

## Java 8 solution

```java
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
```

## Complexity

- Time: O(L) — a single traversal of the list; the initial `n+1`-step head start and the subsequent lockstep walk together touch each node a constant number of times.
- Space: O(1) extra — only a fixed handful of pointers (`dummy`, `fast`, `slow`) regardless of list length; the input list is modified in place, and no auxiliary array or list is built.

## Java 8 pitfalls for this problem

- Forgetting the dummy-head idiom and instead special-casing "is the head itself the node being removed" as its own branch — this doubles the code paths to keep correct and is a common source of an off-by-one bug exactly when `n` equals the list length.
- Advancing `fast` only `n` steps ahead instead of `n + 1` — this lands `slow` *on* the target node instead of on its predecessor, so `slow.next.next` skips one node too many (or throws a `NullPointerException` when the target is the tail).
- Losing the `next` pointer before relinking: `slow.next = slow.next.next` is safe only because Java evaluates the entire right-hand side (`slow.next.next`) before performing the assignment — a rewritten version that assigned to `slow.next` in an earlier separate statement before reading its "next-next" would silently corrupt the list.
- Null-checking `fast` but not `fast.next` (or the reverse) when a two-pointer walk advances one pointer by two nodes per step — not needed in *this* particular solution, since the main loop here only ever advances one step at a time, but it is the single most common bug across the wider two-pointer/fast-slow family, so it is worth naming even where it does not apply.
- Returning `head` instead of `dummy.next` at the end — if the original head was the node removed, the `head` variable still refers to the now-detached node, not the new first node of the list.
- Declaring `ListNode` as a non-static inner class when it must be constructed from a `static` context such as `main` — Java requires a non-static inner class instance to carry a reference to an enclosing instance, which a `static` method does not have; declaring it `static class ListNode` (as done here) avoids the issue entirely.

## Wrong approaches and why they fail

- **Advance `fast` by `n` steps instead of `n + 1`, then loop while `fast.next != null`.** This is a subtly different way to try to land one-before-the-target, and getting the initial offset and the stopping condition to agree with each other is more error-prone than the `n+1`-then-`fast==null` pairing. Counterexample: `[1,2]`, `n=2` (removing the head of a 2-node list) — with an `n`-step head start and a `fast.next==null` stop condition, `fast` starts at the 2nd node, and `fast.next` is already `null`, so the main loop body never runs and `slow` (still at the dummy) ends up used as the "before target" pointer only by coincidence; the same reasoning breaks for `n` equal to length on longer lists unless the stop condition is adjusted in lockstep with the offset, which is easy to get wrong.
- **Special-case "remove the head" as a completely separate return path instead of using a dummy head.** Not wrong in principle, but it splits "remove interior/tail node" and "remove head" into two independently-maintained code paths, doubling the surface area for bugs; the dummy-head idiom exists specifically to collapse both into one path. Counterexample showing the risk: if the head-removal branch is written first and returns before the shared `n`-handling logic runs, a list like `[1,2,3]`, `n=3` can silently take the wrong branch and return the wrong node if the branch's own boundary condition (`n == length`) is computed with an off-by-one.
- **Treat `n` as 0-indexed from the end instead of 1-indexed.** Counterexample: `[1,2,3,4,5]`, `n=2` under a 0-indexed reading would target the very last node (`5`) instead of the 2nd-to-last (`4`), producing `[1,2,3,4]` instead of the correct `[1,2,3,5]` — the problem defines the last node as position 1 from the end, not position 0, and this off-by-one is easy to introduce when translating "a gap of n" into a loop bound.

## Variants

- **Remove the nth node from the *start* instead of the end.** This needs no two-pointer gap at all — walk `n` steps from a dummy head and unlink directly; the two-pointer trick in this problem exists specifically because the end of a singly linked list is not known in advance without either counting first or using a lookahead gap.
- **Return the value of the nth-from-end node without removing it.** Use the same fixed-gap two-pointer walk (gap of exactly `n` this time, both pointers starting at the real `head`), stop once the lookahead pointer runs off the end, and read the trailing pointer's value — no dummy head is needed at all here since nothing is ever unlinked, so there is no "node before the head" edge case to worry about.
- **A doubly linked list version.** With a `prev` pointer available on every node and (often) a maintained tail pointer, the same target can be found by walking backward from the tail `n - 1` steps directly, with no forward lookahead gap needed at all.

## Test cases

| # | input | n | expected | what it tests |
|---|---|---|---|---|
| 1 | `[1,2,3,4,5]` | 2 | `[1,2,3,5]` | classic interior removal |
| 2 | `[1]` | 1 | `[]` | single node removed, list becomes empty |
| 3 | `[1,2]` | 1 | `[1]` | remove the tail of a 2-node list |
| 4 | `[1,2]` | 2 | `[2]` | remove the head of a 2-node list (n equals length) |
| 5 | `[1,2,3]` | 3 | `[2,3]` | n equals length on a 3-node list, head removal |
| 6 | `[1,2,3,4,5]` | 5 | `[2,3,4,5]` | n equals length on a longer list |
| 7 | `[1,1,1,1]` | 2 | `[1,1,1]` | all-equal values, removal must be positional not value-based |
| 8 | `[1,2,3,4,5,6]` | 1 | `[1,2,3,4,5]` | remove the exact tail of a longer list |
| 9 | `[-3,-2,-1,0,1]` | 3 | `[-3,-2,0,1]` | negative and mixed-sign values, interior removal |
| 10 | `[10,20,30,40,50,60,70]` | 4 | `[10,20,30,50,60,70]` | larger list, target well inside the interior |
