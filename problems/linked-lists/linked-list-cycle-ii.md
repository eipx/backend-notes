# Linked List Cycle II
`ref: LC 142` · Difficulty: Medium · Pattern: Floyd's tortoise and hare, then a second lockstep walk from the head to locate the entry

## Problem

Given the head of a singly linked list, determine whether the list loops back on itself — some node's `next` pointer, followed far enough, eventually leads back to a node already visited — and if it does, return the exact node where that loop begins. If the list terminates normally at `null`, return `null`. The list may not be modified, and the intended solution should not depend on a structure whose memory usage grows with the number of nodes visited.

Input: `head`, the first node of a singly linked list that may or may not contain a cycle.
Output: the node object where the cycle starts (by reference — comparing node identity, not value), or `null` if there is no cycle.

## Constraints

- The list holds between 0 and `10^4` nodes.
- Node values are ordinary ints and may repeat anywhere in the list, including inside or outside a cycle — so nothing about detection may depend on values, only on `next` structure and node identity.
- If a cycle exists, its entry point can be any node in the list, including the head itself (the whole list is one cycle) or the very last node (a single node feeding back into itself).
- The follow-up constraint that defines the optimal approach: solve it using O(1) extra memory. A solution that remembers every visited node in a growing structure is correct but does not meet this bar.

## Worked examples

1. `vals=[3,2,0,-4]`, tail's `next` points to index `1` (value `2`) → the cycle entry is the node with value `2`. Walking the list: `3 -> 2 -> 0 -> -4 -> (back to 2)`. The loop revisits `2, 0, -4, 2, 0, -4, ...` forever; `2` is the first node that gets revisited, so it is the entry.
2. `vals=[1,2]`, tail's `next` points to index `0` → the entire list is the cycle, and the entry is the head itself (value `1`).
3. `vals=[1,2,3,4,5]`, tail's `next` is `null` (no connection at all) → no cycle exists; the answer is `null`, even though the list itself is perfectly ordinary.
4. `vals=[1,2,3,4,5]`, tail's `next` points to index `4` (itself) → a single node self-loop at the very end; the entry is the last node (value `5`), and everything before it (`1,2,3,4`) is a plain non-cyclic "tail" leading into a 1-node cycle.

## Edge cases checklist

- Empty list (`head == null`) — must report no cycle without dereferencing anything.
- Single node with no cycle — `next` is `null`, must report no cycle.
- Single node whose own `next` points to itself — a cycle of length 1, and the entry is that same node.
- Cycle at the head — the tail connects back to the very first node, so the entire list is inside the cycle with no non-cyclic "tail" leading into it.
- Cycle at the tail — the last node loops back to itself (cycle length 1) after an otherwise-ordinary non-cyclic run of nodes.
- No cycle at all, on both a short and a longer list.
- Cycle entry somewhere in the middle, with a non-trivial cycle length greater than 1.
- Node values that repeat across the list (including a value appearing both inside and outside the cycle) — detection must never be confused by equal values, since it depends only on structural identity.

## Approach

### Brute force

Walk the list one node at a time, keeping every visited node in a `HashSet` keyed by node identity (never by value, since values can repeat legitimately). The first node that is already present in the set is the cycle's entry point — it is the first node reached a second time, which by definition is exactly where the loop closes. If the walk instead reaches `null`, there is no cycle. This is correct and O(L) time, but its space is O(L) in the worst case (a cycle-free list, or a very long non-cyclic run before a small cycle, still needs to remember almost every node visited).

### Optimal

Floyd's tortoise-and-hare technique, done in two phases with no extra memory:

**Phase 1 — detect.** Start two pointers at `head`: `slow` advances one node per step, `fast` advances two nodes per step. If `fast` (or `fast.next`) ever becomes `null`, the list has no cycle. If instead `slow` and `fast` ever point to the same node, a cycle exists and they have met somewhere inside it.

**Phase 2 — locate the entry.** Once `slow` and `fast` have met, leave `slow` where it is and start a brand new pointer `ptr` back at `head`. Advance both `ptr` and `slow` one node at a time, in lockstep. The node where they next meet is exactly the cycle's entry point.

**Key invariant:** let `L` be the number of nodes from `head` to the cycle's entry (0 if the head is already inside the cycle), `C` the cycle's length, and `k` the distance from the entry to the meeting point found in phase 1, measured forward along the direction of travel (`0 <= k < C`). The invariant that makes phase 2 work is: `L` and `C - k` are congruent modulo `C` — that is, walking `L` steps from the head lands on the same node as walking `C - k` steps (plus any whole number of extra laps) from the phase-1 meeting point. Both walks therefore reach the cycle entry after the same number of steps, which is exactly why advancing `ptr` (from the head) and `slow` (from the meeting point) together, one step at a time, brings them together precisely at the entry.

Proof sketch: when `slow` and `fast` first meet, `slow` has traveled `L + k` steps (it entered the cycle after `L` steps, then moved `k` more steps around the cycle to reach the meeting point). `fast` moves twice as fast, so it has traveled `2(L + k)` steps in the same time. Because `fast` can only ever catch `slow` from behind within the cycle, the extra distance `fast` covered — `2(L+k) - (L+k) = L + k` — must be a whole number of complete laps around the cycle: `L + k = i \cdot C` for some positive integer `i`. Rearranging: `L = i \cdot C - k = (i - 1) \cdot C + (C - k)`. Now walk `ptr` from the head for `L` steps: it reaches the cycle entry (that is what `L` means by definition). Walk `slow` from the meeting point for the same `L` steps: since `L = (i-1) \cdot C + (C - k)`, the first `C - k` of those steps bring `slow` from the meeting point forward to the entry (a distance of `C - k` around the cycle from a point that is `k` past the entry), and the remaining `(i-1) \cdot C` steps are exactly `(i-1)` full laps of the cycle, landing `slow` back on the entry again. Both pointers therefore arrive at the entry after exactly `L` steps of simultaneous single-stepping, which is precisely the stopping condition `ptr != slow` checked in the code.

### Step-by-step trace

Trace on `vals=[3,2,0,-4]` with the tail (`-4`) pointing back to index `1` (value `2`), expected entry `2`.

Here `L = 1` (one node, `3`, before the cycle), `C = 3` (the cycle is `2 -> 0 -> -4 -> 2`).

| phase | slow | fast | note |
|---|---|---|---|
| start | 3 | 3 | both at head |
| step 1 | 2 | 0 | slow +1, fast +2 |
| step 2 | 0 | 2 | slow +1 (wraps once), fast +2 (wraps once) |
| step 3 | -4 | -4 | slow == fast — meeting point found, phase 1 ends |
| phase 2 reset | ptr=3, slow=-4 | — | ptr restarts at head; slow stays at the meeting point |
| phase 2 step 1 | ptr=2, slow=2 | — | both advance one step; ptr == slow — entry found |

Result: entry node is `2`, matching the expected output. (Here `k` — the distance from the entry `2` to the meeting point `-4` — is `2`; `C - k = 1`, matching the single step phase 2 needed, consistent with `L = 1`.)

## Java 8 solution

```java
// Linked List Cycle II
// ref: LC 142
// Given the head of a singly linked list that may contain a cycle, return the
// node where the cycle begins, or null if the list has no cycle. Do not modify
// the list, and do not use any structure whose size grows with the list except
// where explicitly noted as the baseline approach.
// Required complexity: O(L) time, O(1) extra space for the optimal approach.
// Study page: ../linked-list-cycle-ii.md
// Run: javac --release 8 LinkedListCycleII.java && java LinkedListCycleII

import java.util.*;

public class LinkedListCycleII {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    public static ListNode solve(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        // Phase 1: advance slow by 1, fast by 2, until they meet inside the
        // cycle, or fast falls off the end (no cycle exists).
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                // Phase 2: reset one pointer to head; both now advance by 1
                // step at a time and meet exactly at the cycle's entry node.
                ListNode ptr = head;
                while (ptr != slow) {
                    ptr = ptr.next;
                    slow = slow.next;
                }
                return ptr;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        // Each case: node values, and the index the last node's next should point
        // to (-1 for no cycle). Expected is the index of the entry node (-1 = null).
        Object[][] cases = {
            {intArr(3,2,0,-4), 1, 1},
            {intArr(1,2), 0, 0},
            {intArr(1), 0, 0},
            {intArr(1), -1, -1},
            {intArr(), -1, -1},
            {intArr(1,2,3,4,5), -1, -1},
            {intArr(1,2,3,4,5), 4, 4},
            {intArr(1,2,3,4,5), 0, 0},
            {intArr(1,2,3), 2, 2},
            {intArr(5,-1,3,7,2,9), 3, 3}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] vals = (int[]) cases[i][0];
            int pos = (Integer) cases[i][1];
            int expectedIdx = (Integer) cases[i][2];
            try {
                ListNode[] nodes = buildNodes(vals);
                if (pos >= 0) {
                    nodes[nodes.length - 1].next = nodes[pos];
                }
                ListNode head = nodes.length > 0 ? nodes[0] : null;
                ListNode result = solve(head);
                int gotIdx = indexOfIdentity(nodes, result);
                if (gotIdx == expectedIdx) {
                    System.out.println("PASS");
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected index " + expectedIdx + " got index " + gotIdx);
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected index " + expectedIdx + " got exception " + e);
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

    private static ListNode[] buildNodes(int[] vals) {
        ListNode[] nodes = new ListNode[vals.length];
        for (int i = 0; i < vals.length; i++) {
            nodes[i] = new ListNode(vals[i]);
        }
        for (int i = 0; i < vals.length - 1; i++) {
            nodes[i].next = nodes[i + 1];
        }
        return nodes;
    }

    // Identity-based lookup (never value-based): returns -1 for null or "not found".
    private static int indexOfIdentity(ListNode[] nodes, ListNode target) {
        if (target == null) {
            return -1;
        }
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == target) {
                return i;
            }
        }
        return -1;
    }
}
```

## Complexity

- Time: O(L) — phase 1 takes at most O(L) steps for `slow` (and `fast` covers at most twice that before either meeting `slow` or falling off the end), and phase 2 takes at most O(L) more steps; both phases together stay linear in the list length.
- Space: O(1) extra for the optimal approach — only three pointer variables (`slow`, `fast`, `ptr`) regardless of list length. The brute-force baseline is O(L) space for its visited-node set.

## Java 8 pitfalls for this problem

- Checking only `fast != null` and forgetting `fast.next != null` before writing `fast.next.next` — this throws a `NullPointerException` the moment `fast` lands on the last node of a non-cyclic list, since `fast.next` is `null` there and `.next` cannot be called on it.
- Comparing nodes with `.equals()` (or worse, comparing `.val` fields) instead of `==` — two distinct nodes can legitimately hold equal values, so value-based comparison can report a false "meeting" long before the pointers are actually on the same physical node; identity comparison (`==`) is required throughout.
- Resetting the *wrong* pointer in phase 2 — the pointer left at the meeting point must keep advancing one step at a time together with a new pointer from the head; accidentally continuing to double-step the old `fast` pointer breaks the distance relationship the proof depends on.
- Using a `HashSet<ListNode>` for detection (the brute-force baseline) but accidentally relying on `ListNode`'s default `hashCode()`/`equals()` — this is actually safe by default since neither is overridden here (identity semantics are Java's default for objects without a custom `equals`), but overriding `equals()` on `ListNode` for some unrelated reason later would silently break this detection method by making unequal nodes with equal values collide.
- Declaring `ListNode` as a non-static inner class when it must be constructed from a `static` context such as `main` or a `static` helper — Java requires a non-static inner class instance to carry a reference to an enclosing instance, which a `static` context does not have; declaring it `static class ListNode` (as done here) avoids the issue.
- Off-by-one in the loop condition, writing `while (fast.next != null)` without also checking `fast != null` first — Java short-circuits `&&`, so the order `fast != null && fast.next != null` matters: reversing the two conditions dereferences a possibly-null `fast` before checking it.

## Wrong approaches and why they fail

- **Use only a single slow pointer that walks the list, comparing every node's `next` field to `head` to detect a cycle.** This only correctly detects the specific case where the cycle loops back all the way to the head; it fails whenever the cycle entry is any other node. Counterexample: `vals=[3,2,0,-4]` with the cycle entry at index `1` (value `2`, not the head `3`) — this approach would walk forever waiting for some node's `next` to equal the head node, which never happens, since the head is not part of the cycle at all.
- **Detect the cycle with fast/slow correctly, but then try to compute the entry via arithmetic on `k` and `C` (counting the cycle length and the meeting-point offset) instead of the head-reset lockstep walk.** This can be made to work, but it requires first walking all the way around the cycle once just to measure `C` (an extra full pass around the cycle), and then separately walking `L` from the head — strictly more work and more places to make an off-by-one than simply resetting one pointer to head and letting the invariant proven above do the work directly.
- **Stop phase 1 as soon as `slow == fast` and return that meeting node directly as the answer, skipping phase 2 entirely.** Counterexample: `vals=[3,2,0,-4]` with cycle entry at index `1` — phase 1 meets at node `-4` (as traced above), which is not the entry; returning it directly would report the wrong node whenever the meeting point (which depends on `L`, `C`, and where the pointers happen to collide) is not itself the entry, which is the common case rather than the exception.

## Variants

- **Report only whether a cycle exists (a yes/no answer), not the entry node.** This is a strict subset of this problem — run phase 1 alone and check whether `slow` and `fast` ever collide; phase 2 (and the entire proof it relies on) becomes unnecessary.
- **Report the cycle's length once it is known to exist.** After phase 1 finds a meeting point, keep one pointer fixed there and advance a second pointer starting from the same spot around the cycle, counting steps until it returns to the fixed point — that count is `C`.
- **The same detection idea applied to a "functional graph" (an array where `nums[i]` points to the next index, forming an implicit linked structure) rather than an explicit `ListNode` chain** — a common disguise of this exact problem (e.g., "Find the Duplicate Number") where `slow`/`fast` step through array indices instead of `.next` pointers, but the underlying two-phase Floyd's algorithm and its proof are identical.

## Test cases

| # | vals | cycle pos | expected entry index | what it tests |
|---|---|---|---|---|
| 1 | `[3,2,0,-4]` | 1 | 1 | classic mid-list cycle entry |
| 2 | `[1,2]` | 0 | 0 | cycle at the head, whole list is the cycle |
| 3 | `[1]` | 0 | 0 | single node, self-loop (cycle at head and tail simultaneously) |
| 4 | `[1]` | -1 | -1 (null) | single node, no cycle |
| 5 | `[]` | -1 | -1 (null) | empty list |
| 6 | `[1,2,3,4,5]` | -1 | -1 (null) | longer list, no cycle |
| 7 | `[1,2,3,4,5]` | 4 | 4 | cycle at the tail, single-node self-loop after a non-cyclic run |
| 8 | `[1,2,3,4,5]` | 0 | 0 | cycle at the head on a longer list |
| 9 | `[1,2,3]` | 2 | 2 | cycle at the tail, short list |
| 10 | `[5,-1,3,7,2,9]` | 3 | 3 | longer list, mixed-sign values, mid-list entry |
