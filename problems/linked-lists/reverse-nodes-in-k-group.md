# Reverse Nodes in k-Group
`ref: LC 25` · Difficulty: Hard · Pattern: in-place group reversal with a dummy head, leaving a short trailing group untouched

## Problem

Given the head of a singly linked list and an integer `k`, reverse the nodes of the list `k` at a time and return the new head. Split the list into consecutive groups of `k` nodes starting from the front; reverse the node order within each full group, but if the number of nodes remaining at the end is fewer than `k`, leave that final short group exactly as it was — unreversed. Only the arrangement of the existing nodes may change (their `next` pointers may be rewired); no new nodes may be created and no node's value may be copied into a different node to fake a reversal.

Input: `head` (the first node of a singly linked list) and an integer `k`.
Output: the head of the list with every full group of `k` consecutive nodes reversed in place, in original list order otherwise.

## Constraints

- The list holds between 0 and `5000` nodes.
- `1 <= k <=` the number of nodes in the list is the usual framing, but this page also exercises `k` strictly greater than the list length to confirm the "too few nodes, leave as-is" behavior holds even for the very first group.
- Node values are ordinary 32-bit ints and may repeat freely; reversal is purely structural.
- The follow-up constraint that shapes the intended solution: solve it using only O(1) extra memory beyond the list itself — no auxiliary array of values and no recursion whose stack depth grows with the list length (an easy recursive formulation exists, but it is a baseline here, not the target).

## Worked examples

1. `head=[1,2,3,4,5]`, `k=2` → `[2,1,4,3,5]`. The list splits into groups `[1,2]`, `[3,4]`, and a trailing `[5]` that is shorter than `k=2`. The two full groups each reverse (`[1,2]→[2,1]`, `[3,4]→[4,3]`), and the leftover `[5]` stays put.
2. `head=[1,2,3,4,5]`, `k=3` → `[3,2,1,4,5]`. One full group of 3 (`[1,2,3]→[3,2,1]`) followed by a trailing group of 2 nodes (`[4,5]`), which is shorter than `k=3` and is therefore left untouched.
3. `head=[1,2,3]`, `k=4` → `[1,2,3]`. There are only 3 nodes and `k=4` — since there is no full group of 4 anywhere in the list, nothing is reversed at all, and the list comes back unchanged.
4. `head=[1,2,3,4,5,6,7]`, `k=3` → `[3,2,1,6,5,4,7]`. Two full groups of 3 (`[1,2,3]` and `[4,5,6]`) each reverse, and the final single node `[7]` — shorter than `k=3` — is left in place.

## Edge cases checklist

- Empty list — must return `null`/empty without touching anything.
- Single node with `k=1` — a "group" of size 1 has nothing to reverse; the list is returned unchanged.
- `k=1` on any list — every group has exactly one node, so no pair ever swaps and the whole list is a no-op regardless of length.
- `k` exactly equal to the list length — the entire list is one full group and reverses completely.
- `k` strictly greater than the list length — no full group exists anywhere, so the list is returned completely unreversed.
- List length not a multiple of `k` — the trailing short group must be detected and left alone, not partially reversed or padded.
- All node values equal — reversal must still physically reorder the nodes (identity matters for correctness even though the output looks unchanged by value).
- A list long enough to need more than two full groups, to confirm the group-to-group relinking (not just the reversal within one group) is correct.

## Approach

### Brute force

Copy every node's value into a plain array (or `List<Integer>`), then reverse each full chunk of `k` consecutive values in that array in place (leaving a final short chunk untouched), and finally rebuild a brand-new linked list from the resulting sequence of values, or write the reversed values back into a second full pass over the original nodes. This is correct and O(L) time, but it uses O(L) extra space for the array — it never manipulates the existing nodes' `next` pointers directly, so it is a step below what the O(1)-space requirement actually asks for, and (as a subtler cost) rebuilding new nodes rather than reusing the originals means any external reference into the original list becomes stale.

### Optimal

Walk the list one group at a time using a `groupPrev` pointer (starting at a dummy node placed before `head`, so "the node before the current group" always exists, even for the very first group). For each group: walk `k` steps forward from `groupPrev` to find the group's k-th node. If fewer than `k` nodes remain (the walk falls off the end), stop entirely — that trailing short group is left untouched. Otherwise, reverse exactly those `k` nodes in place using the standard three-pointer in-place reversal, then reconnect: the node before the group now points to the new head of the reversed group (the old k-th node), and the old head of the group (now the reversed group's tail) is advanced to become the new `groupPrev` for the next iteration.

**Key invariant:** at the start of processing each group, `groupPrev` always points to the last correctly-placed node so far — either the dummy (before any group has been processed) or the tail of the most recently reversed group — and everything from `groupPrev.next` onward is still in its original, unreversed order. This invariant is what makes it safe to look ahead exactly `k` nodes from `groupPrev` to decide whether a full group exists, and to graft the newly reversed group's ends directly onto `groupPrev` and the untouched remainder without disturbing anything already finalized.

Proof sketch: initially `groupPrev = dummy`, and the entire list from `dummy.next` onward is unprocessed and in original order — the invariant holds trivially. Assume it holds before processing some group: `groupPrev` is correctly placed, and the next `k` nodes (if that many exist) are exactly the next group in original order. The lookahead walk either finds fewer than `k` nodes (in which case the invariant is preserved by simply stopping — nothing after `groupPrev` is touched, so it remains in original order, satisfying "leave the short group as-is") or finds the group's k-th node `kth`. The subsequent reversal only rewires `next` pointers strictly between `groupPrev.next` (old group head) and `kth` (old group tail) — it never touches `groupPrev` itself nor anything after `groupNext = kth.next`, so the remainder of the list past this group is left in original order, exactly matching the "unprocessed suffix" half of the invariant. After relinking `groupPrev.next = kth` and advancing `groupPrev` to the group's old head (now its tail), `groupPrev` is once again correctly placed as "the last correctly-placed node so far," restoring the invariant for the next iteration. By induction, every full group encountered gets reversed exactly once, in order, and the process halts the instant fewer than `k` nodes remain, leaving that remainder untouched — which is exactly the required behavior.

### Step-by-step trace

Trace on `head=[1,2,3,4,5]`, `k=2`, expected `[2,1,4,3,5]`. `D` is the dummy.

| iteration | groupPrev before | kth found | reversal result | groupPrev after |
|---|---|---|---|---|
| 1 | D (before 1) | node 2 (2 steps from D: 1, 2) | segment `1,2` reversed to `2,1`; `D.next` becomes `2` | node `1` (old head, now tail of this group) |
| 2 | node `1` | node 4 (2 steps from `1`: 3, 4) | segment `3,4` reversed to `4,3`; `1.next` becomes `4` | node `3` (old head, now tail of this group) |
| 3 | node `3` | walk from `3`: 1 step reaches `5`, 2nd step reaches `null` — fewer than `k=2` remain | loop breaks, nothing reversed | — |

Final list, read from `D.next`: `2 -> 1 -> 4 -> 3 -> 5`, matching the expected output. Node `5` was never touched because the lookahead at iteration 3 could not find a full group of 2.

## Java 8 solution

```java
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
```

## Complexity

- Time: O(L) — every node is visited a constant number of times: once by the lookahead walk that measures each group, and once by the reversal walk within its own group; groups partition the list, so the total work across all groups is linear.
- Space: O(1) extra for the optimal approach — a fixed set of pointers (`dummy`, `groupPrev`, `kth`, `groupNext`, `prev`, `cur`, `next`) regardless of list length or `k`. The brute-force baseline is O(L) space for its value array.

## Java 8 pitfalls for this problem

- Forgetting the dummy-head idiom — without it, reversing the very first group (which becomes the new overall head) needs its own special case to update the caller's notion of "the head," whereas `dummy.next` always reads correctly regardless of which group ended up first.
- Losing the `next` pointer before relinking during the reversal loop — the classic in-place reversal bug is writing `cur.next = prev` before saving `cur.next` into a temporary; the code here saves it first (`ListNode next = cur.next;`) precisely to avoid overwriting the only reference to the rest of the group before it has been read.
- Capturing `newGroupPrev = groupPrev.next` *after* the reversal loop instead of before — this looks suspicious at first glance since `groupPrev.next` seems like it might have been mutated by the loop, but it has not: the reversal loop only ever mutates `cur.next` for nodes strictly inside the group, never `groupPrev.next` itself, so reading it after the loop still correctly yields the group's original head (now its tail); getting this ordering backwards while "fixing" the code is a common self-inflicted bug during a rewrite.
- Miscounting the lookahead: using `for (int i = 0; i <= k; ...)` (off by one) instead of `i < k` walks one node too far and either throws on a short trailing group that should have been left alone, or reverses one node too many.
- Null-checking only `kth` inside the lookahead loop's condition but forgetting the loop can also exit normally with `kth` still non-null but `groupNext` (`kth.next`) legitimately `null` — this is actually fine and expected (it just means the group being reversed is the last group in the list), but a rewrite that assumes `groupNext` is always non-null (say, to read `groupNext.val` for a debug print) would throw on the last group.
- Declaring `ListNode` as a non-static inner class when it must be constructed from a `static` context such as `main` — Java requires a non-static inner class instance to carry a reference to an enclosing instance, which a `static` context does not have; declaring it `static class ListNode` (as done here) avoids the issue.

## Wrong approaches and why they fail

- **Reverse the entire list first, then re-reverse it back in chunks of k from the new end.** This does not correctly identify which chunks were "full" groups in the *original* order, because reversing the whole list first changes which end the trailing short group is measured from. Counterexample: `[1,2,3]`, `k=2` — the correct answer leaves the trailing `[3]` untouched and reverses `[1,2]` into `[2,1,3]`; reversing the whole list first gives `[3,2,1]`, and re-chunking from there in groups of 2 produces a completely different (and wrong) grouping of which original nodes end up adjacent.
- **Recursively reverse each group and splice in the recursive result of the remainder, without first checking that a full group of k exists.** This is a legitimate simpler baseline when the lookahead check is done correctly, but a common mistake is to reverse first and check the group's size only afterward (or not at all) — Counterexample: `[1,2,3]`, `k=4` — reversing whatever nodes happen to be available (only 3, fewer than `k`) before checking the count produces `[3,2,1]` instead of correctly detecting that no full group exists and leaving the list as `[1,2,3]`.
- **Reverse each group by swapping node *values* in place instead of relinking `next` pointers.** This can produce the correct output values, but it does not satisfy "only rearrange existing nodes" — it silently violates the O(1)-extra-structure spirit of the problem when node identity matters to a caller (for example, if some other part of a program held a reference to a specific node object expecting it to keep its original value), and on problems that explicitly forbid value copying, this approach does not actually solve the stated problem even though its printed output looks identical.

## Variants

- **Reverse every group *except* leave the first `k` nodes untouched and reverse the rest** (or some other "skip the first group" variant) — the same walk-and-relink structure applies, just start the loop's first `groupPrev` further into the list instead of at the dummy.
- **Reverse alternating groups of k (reverse group 1, skip group 2, reverse group 3, ...)** — track a boolean toggled after each group, and only perform the reversal step when the toggle says "reverse this one," while still always advancing `groupPrev` past the group either way.
- **A doubly linked list version.** Reversing a group still needs the same walk to find the k-th node and the same relinking, but each node's `prev` pointer must also be corrected to point at its new predecessor — doubling the pointer bookkeeping per node but changing nothing about the group-detection logic.

## Test cases

| # | input | k | expected | what it tests |
|---|---|---|---|---|
| 1 | `[1,2,3,4,5]` | 2 | `[2,1,4,3,5]` | classic case, trailing single node left untouched |
| 2 | `[1,2,3,4,5]` | 3 | `[3,2,1,4,5]` | one full group, trailing group of 2 left untouched |
| 3 | `[]` | 1 | `[]` | empty list |
| 4 | `[1]` | 1 | `[1]` | single node, k=1 |
| 5 | `[1,2,3]` | 1 | `[1,2,3]` | k=1 on a longer list is always a no-op |
| 6 | `[1,2,3]` | 3 | `[3,2,1]` | k equals list length, whole list reverses |
| 7 | `[1,2,3]` | 4 | `[1,2,3]` | k greater than list length, no group exists, no-op |
| 8 | `[7,7,7,7,7]` | 2 | `[7,7,7,7,7]` | all-equal values, reversal is structural even when invisible by value |
| 9 | `[1,2,3,4,5,6,7]` | 3 | `[3,2,1,6,5,4,7]` | two full groups plus a trailing single node |
| 10 | `[1,2]` | 2 | `[2,1]` | smallest case where k equals length |
