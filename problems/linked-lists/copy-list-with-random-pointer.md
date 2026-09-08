# Copy List with Random Pointer
`ref: LC 138` · Difficulty: Medium · Pattern: weave-and-split interleaving to reach O(1) extra space, with a HashMap of old-to-new nodes as the simple baseline

## Problem

Given the head of a singly linked list in which every node carries an ordinary `next` pointer *and* an additional `random` pointer that may point to any node anywhere in the list (including itself) or to nothing at all, construct a completely independent deep copy of the entire structure and return the head of the copy. "Independent" means every single node object in the copy must be a brand-new object — none of the copy's nodes may be the same object as any node in the original — while every `next` and `random` relationship in the copy must mirror the corresponding relationship in the original exactly, position for position.

Input: `head`, the first node of a list whose nodes each hold `val`, `next`, and `random`.
Output: the head of a new list with the identical `val`/`next`/`random` structure, built entirely out of new node objects.

## Constraints

- The list holds between 0 and `1000` nodes.
- `random` may point to any node in the list, including a node before it, after it, or itself, or may be `null`.
- Node values fit in an ordinary 32-bit int and may repeat across different nodes.
- The follow-up constraint that shapes the intended solution: do the copy using O(1) extra space beyond the output itself — no auxiliary map keyed by node.

## Worked examples

1. `vals=[7,13,11,10,1]`, random targets `[null, node0, node4, node2, node0]` → the copy has the same five values in the same order, and its random pointers land on the *copy's* nodes at the same positions (index 1's random points to the copy of index 0, not the original index 0).
2. `vals=[1]`, random targets `[0]` (the only node points to itself) → the copy is a single node whose own random pointer points to itself — the copy's self-reference, never back to the original node.
3. `vals=[1,2]`, random targets `[1,1]` (both nodes point to the second node) → both copied nodes' random pointers must point to the *same* copied second node — not two different copies of it, since there is only one node at index 1 to begin with.
4. `vals=[]` (empty list) → the copy is also empty (`null` head); nothing to interleave or map.

## Edge cases checklist

- Empty list — must return `null` without touching anything.
- Single node whose random pointer is `null`.
- Single node whose random pointer points to itself.
- Every node's random pointer is `null` (no random structure at all, just a plain list to copy).
- A node's random pointer points backward to an earlier node in the list.
- A node's random pointer points forward to a later node in the list.
- Multiple nodes' random pointers converge on the same target node — the copy must preserve that convergence (both copies point to the *same* copied target, not to separate copies of it).
- Duplicate values across different nodes — comparing copies by value alone is not sufficient to verify correctness; verification must track structure by position and confirm no node object is shared with the original.

## Approach

### Brute force

Use a `HashMap<Node, Node>` to map each original node to its freshly created copy. First pass: walk the original list once, creating one new node (with just the value copied over, `next` and `random` left blank) per original node, and record `original -> copy` in the map. Second pass: walk the original list again, and for each original node set its copy's `next` to `map.get(original.next)` and its copy's `random` to `map.get(original.random)` (both `get` calls naturally return `null` if the original pointer was `null`, since a `null` key is never present in the map). Return `map.get(head)`. This is correct and O(L) time, but it uses O(L) extra space for the map — proportional to the list length, which the optimal approach avoids entirely.

### Optimal

Interleave copies directly into the original list, use that interleaving to resolve `random` pointers in a single additional pass, then split the two lists back apart — no map needed.

**Pass 1 (weave):** walk the original list; for each original node `orig`, create a new node `copy` with the same value, insert it immediately after `orig` (so the list becomes `orig1, copy1, orig2, copy2, ...`), and advance to the next *original* node by following two `next` hops (`orig.next.next`, which is `orig`'s original successor, now sitting one hop past its own copy).

**Pass 2 (wire random):** walk the interleaved list again in the same two-hops-at-a-time pattern; for each original node `orig`, its copy is `orig.next` (guaranteed by pass 1's interleaving), and if `orig.random` is non-null, that random target's *copy* is `orig.random.next` (the copy interleaved immediately after whatever node `orig.random` pointed to) — so `copy.random = (orig.random != null) ? orig.random.next : null`.

**Pass 3 (split):** walk the interleaved list one final time, restoring each original node's `next` to skip over its inserted copy (reconnecting the original list to itself) and simultaneously reconnecting each copy's `next` to the *next* copy (skipping over the next original node) — separating the two now-fully-wired lists back into two independent chains.

**Key invariant:** immediately after pass 1 completes, and for as long as the interleaving remains intact (through pass 2, until pass 3 begins undoing it), the relationship `copy(x) == x.next` holds for every original node `x`. This single invariant is exactly what makes pass 2's one-line formula correct: to find the copy of `orig.random`, it suffices to look at `orig.random.next`, with no map lookup required, because `orig.random` is itself some original node `x`, and `x.next` is by the invariant exactly `copy(x)`.

Proof sketch: process pass 1 left to right. For the first original node `orig1`, before any mutation `orig1.next` is `orig2` (or `null`); the code saves this value, creates `copy1`, sets `copy1.next` to the saved value, and sets `orig1.next = copy1` — so immediately after this step, `orig1.next == copy1`, establishing the invariant for `orig1`. This assignment is never touched again by any later iteration of pass 1, because later iterations only ever write to `orig2.next`, `orig3.next`, and so on (each iteration writes exactly one original node's `next` field, and never revisits an earlier one). By induction over the left-to-right walk, after pass 1 finishes, `orig_i.next == copy_i` holds simultaneously for every `i`. Pass 2 reads these fields (via `orig.random.next`) but never writes to any `.next` field, so the invariant is preserved unchanged throughout pass 2 — which is precisely why pass 2's random-wiring formula, applied at any point during that pass, is always looking at a still-valid `copy(x) == x.next` relationship regardless of which node has been processed so far.

### Step-by-step trace

Trace on `vals=[1,2]`, random targets `[1,1]` (node 0's random points to node 1; node 1's random points to node 1 itself), expected: copy has values `[1,2]`, copy-random-indices `[1,1]` (same shape), and no node shared with the original.

Original before pass 1: `O0(val=1, random=O1) -> O1(val=1, random=O1) -> null`.

| pass | state after this pass |
|---|---|
| weave (pass 1) | `O0 -> C0 -> O1 -> C1 -> null`, where `C0.val=1`, `C1.val=1`; invariant holds: `O0.next==C0`, `O1.next==C1` |
| wire random (pass 2) | for `O0`: `O0.random=O1`, so `C0.random = O1.next = C1`. For `O1`: `O1.random=O1`, so `C1.random = O1.next = C1` (self-reference, correctly copied as the *copy's* self-reference, not the original's) |
| split (pass 3) | restore `O0.next = C0.next = O1` (original list back to `O0 -> O1 -> null`); set `C0.next = C1.next.next... ` — concretely, `C0.next = C0.next.next` where before this line `C0.next` was `O1`, so `C0.next` becomes `O1.next`, which (still, at this exact moment before `O1` itself is processed) is `C1`; result: `C0 -> C1 -> null` |

Final: original list is `O0 -> O1` (unchanged from before the call), copy list is `C0 -> C1` with `C0.random = C1` and `C1.random = C1`, matching the required shape `[1,1]` with no shared node objects.

## Java 8 solution

```java
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
```

## Complexity

- Time: O(L) — three separate passes over the list, each touching every node a constant number of times.
- Space: O(1) extra for the optimal approach — a fixed set of pointers regardless of list length; no map, no array. The brute-force baseline is O(L) space for its `HashMap`.

## Java 8 pitfalls for this problem

- Comparing nodes (or checking "is this node shared with the original") using `.equals()` instead of `==` — the default `Object.equals()` is identity-based unless overridden, so `==` and `.equals()` happen to agree here, but relying on that coincidence rather than using `==` explicitly is fragile if `Node` ever gains a custom `equals()` for an unrelated reason later; identity comparison is what the problem's "no shared node" requirement actually means.
- Reading `orig.next.next` to advance the outer loop *after* pass 1 has already interleaved copies in — this is correct precisely because `orig.next` at that point is the just-inserted copy, and `orig.next.next` is the next original node; doing this same two-hop advance *before* pass 1 has run (or after pass 3 has already split things back apart) would walk to the wrong node entirely.
- Forgetting the null guard on `orig.random` before reading `orig.random.next` in pass 2 — a node with no random target has `orig.random == null`, and calling `.next` on that null reference throws a `NullPointerException`; the ternary `(orig.random != null) ? orig.random.next : null` is required, not optional.
- Losing the original list's structure by writing pass 3's restoration in the wrong order — `orig.next` must be set to `copy.next` (the next original node) using the *old* `copy.next` value, so `copy.next` must be read (and used to compute the copy list's next pointer) before or independently of overwriting `orig.next`; interleaving these two assignments in the wrong sequence within the loop body can silently skip a node.
- Declaring `Node` as a non-static inner class when it must be constructed from a `static` context such as `main` — Java requires a non-static inner class instance to carry a reference to an enclosing instance, which a `static` context does not have; declaring it `static class Node` (as done here) avoids the issue.
- Using a `HashMap<Node, Node>` (the baseline) but relying on `Node`'s default `hashCode()` — this is actually fine by default (identity hash code), but it is worth being explicit that the map's correctness here depends on `Node` never having a custom `equals()`/`hashCode()` pair added later that would make two different nodes with equal values collide as map keys.

## Wrong approaches and why they fail

- **Copy `next` pointers correctly in one pass, but set each copy's `random` by copying the reference from the original node directly (`copy.random = orig.random`) instead of mapping it to the corresponding copy.** Counterexample: any list where some node's random is non-null, e.g. `vals=[1,2]`, random `[1,1]` — this produces a copy whose random pointers point *into the original list*, not into the copy, which violates the "completely independent" requirement even though every value and every `next` link looks correct at a glance.
- **Use the `HashMap` approach but build it in a single pass, setting `random` before that random target's own copy has necessarily been created yet.** If the map is populated and both `next` and `random` are wired in the very same left-to-right pass, a node whose random pointer targets a *later* node (not yet visited, so not yet in the map) gets `map.get(...)` returning `null` for that random link — Counterexample: `vals=[1,2]`, node 0's random pointing at node 1 (a forward reference) — a single-pass version processing node 0 before node 1 exists in the map would incorrectly leave node 0's copy's random as `null`. The standard fix (used in the baseline described above) is two full passes: one to create every node first, a second to wire both `next` and `random` once every node is guaranteed to already exist in the map.
- **Modify the original list's node values or wiring permanently instead of restoring it in a final pass.** The interleaving trick's pass 1 does temporarily change the original list's shape, which is exactly why pass 3 exists — Counterexample: stopping after pass 2 (skipping the de-interleave step entirely) leaves the original list interleaved with copy nodes permanently, silently corrupting the caller's original list even if the *returned* copy head happens to look correct in isolation.

## Variants

- **The same random-pointer-copy idea on a doubly linked list (nodes also carry `prev`).** The weave/wire/split approach generalizes, but pass 3's de-interleaving must additionally restore each node's `prev` pointer on both the original and copy chains, roughly doubling the pointer bookkeeping per node without changing the core three-pass structure.
- **Copy a general graph (not restricted to a linear list plus one extra pointer) where each node has an arbitrary list of neighbors instead of just `next`/`random`.** The O(1)-space weave trick is specific to the fact that this problem's structure is a simple chain with one extra pointer per node; a general graph copy (e.g., "Clone Graph") falls back to the `HashMap<Node, Node>` baseline approach combined with a BFS or DFS traversal, since there is no single natural "linear interleaving" for an arbitrary graph shape.
- **Copy the list but only for nodes whose random pointer is non-null, leaving the rest as shared references to save memory on the copy.** This is a real memory/behavior trade-off some designs choose deliberately, but it violates this exact problem's requirement that the copy contain zero shared nodes with the original.

## Test cases

| # | vals | random indices (-1 = null) | expected | what it tests |
|---|---|---|---|---|
| 1 | `[7,13,11,10,1]` | `[-1,0,4,2,0]` | valid independent deep copy | classic mixed forward/backward/null random targets |
| 2 | `[1,2]` | `[1,1]` | valid independent deep copy | both nodes' random converge on the same target node |
| 3 | `[3,3,3]` | `[-1,-1,-1]` | valid independent deep copy | all random pointers null, duplicate values |
| 4 | `[1]` | `[0]` | valid independent deep copy | single node, self-referential random |
| 5 | `[1]` | `[-1]` | valid independent deep copy | single node, null random |
| 6 | `[]` | `[]` | valid independent deep copy (empty) | empty list |
| 7 | `[1,2,3,4]` | `[3,2,1,0]` | valid independent deep copy | mirrored/mutual random references |
| 8 | `[5,6]` | `[-1,0]` | valid independent deep copy | second node's random points back to the first |
| 9 | `[10,20,30]` | `[1,2,0]` | valid independent deep copy | random pointers form a cycle distinct from the (acyclic) next chain |
| 10 | `[4,4,4,4]` | `[2,3,0,1]` | valid independent deep copy | duplicate values with distinct random wiring by position |
