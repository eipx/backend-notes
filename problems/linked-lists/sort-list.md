# Sort List
`ref: LC 148` · Difficulty: Medium · Pattern: bottom-up merge sort with iterative width-doubling for O(1) extra space, top-down recursive slow/fast split as the simpler baseline

## Problem

Given the head of a singly linked list holding arbitrary integer values in no particular order, rearrange the existing nodes (no new nodes, no copying values into a fresh structure) so that the list reads in ascending order, and return the new head.

Input: `head`, the first node of an unsorted singly linked list.
Output: the head of the same set of nodes, relinked into ascending order.

## Constraints

- The list holds between 0 and `5 * 10^4` nodes.
- Node values are ordinary 32-bit ints and may repeat freely.
- The follow-up constraint that shapes the intended solution: sort it in O(n log n) time *and* O(1) extra memory — no auxiliary array of values, and no recursion whose call-stack depth grows with the list length (a natural recursive divide-and-conquer exists and is a fine baseline, but it costs O(log n) stack space, which the optimal approach avoids).

## Worked examples

1. `head=[4,2,1,3]` → `[1,2,3,4]`. A small, fully out-of-order list sorts into strict ascending order.
2. `head=[5,4,3,2,1]` → `[1,2,3,4,5]`. A list already sorted in *descending* order is the worst case for some approaches (every comparison during a naive insertion would move an element all the way to the front) but is handled uniformly by merge sort regardless of the input's initial order.
3. `head=[3,3,3]` → `[3,3,3]`. All values equal — the list is already sorted, and the algorithm must not disturb node identity or duplicate/drop any node while doing unnecessary work.
4. `head=[]` → `[]`. Nothing to sort; must not dereference a null head.

## Edge cases checklist

- Empty list — return `null`/empty immediately.
- Single node — trivially already sorted; must not be split or merged unnecessarily (and must not crash trying to find "the middle" of a 1-node list).
- Two nodes already in order, and two nodes out of order (a minimal swap case).
- Already fully sorted ascending input — merge sort must still work as a correctness baseline even though no reordering is needed.
- Fully reverse-sorted input — the maximal-disorder case.
- All values identical.
- Negative values mixed with positive ones and zero.
- Values with duplicates scattered irregularly (not just "all equal") — stability is not required by this problem, but no node may be lost or duplicated.
- An odd-length list and an even-length list, since a bottom-up merge sort's "leftover" handling at the tail of each pass differs slightly between the two.

## Approach

### Brute force

Use the direct recursive translation of merge sort onto a linked list: find the middle of the list using the classic slow/fast pointer split (slow moves one node per step, fast moves two; when fast reaches the end, slow is at the middle), physically sever the list into two halves there, recursively sort each half, and merge the two now-sorted halves back together with a standard two-pointer linked-list merge. This is correct, O(n log n) time, and considerably simpler to write and reason about than the bottom-up version below — but each level of recursion adds a stack frame, so its extra space is O(log n) (the recursion depth), not the O(1) the optimal approach achieves.

### Optimal

Sort the list bottom-up, entirely iteratively, by merging progressively larger already-sorted runs — exactly the array-based iterative merge sort idea, adapted to a linked list with two small helper operations:

- `split(node, n)`: walk `n - 1` steps forward from `node`, then cut the list there — the node's own chain is truncated to exactly `n` nodes, and the remainder of the original chain (whatever came after the cut) is returned so it can be processed next. If fewer than `n` nodes exist from `node` onward, nothing is cut (there is nothing to separate) and `null` is returned.
- `merge(l1, l2, tail)`: merge two already-isolated sorted runs the usual way, but instead of building a brand-new list, append the merged sequence directly onto whatever list already ends at `tail`, and return the new final node so the next merge knows where to keep appending.

The main loop first counts the list length `n` once. Then, for `width = 1, 2, 4, 8, ...` (doubling each pass) while `width < n`: walk across the entire list, and in each pass, repeatedly take the next `width`-sized run and the `width`-sized run after it, split them off from the remainder of the list, merge those two runs together, and append the merged result onto the output built so far. After `ceil(log2(n))` passes, `width` meets or exceeds `n`, meaning the very first "run" of any pass already spans the entire list — at that point the list is fully sorted and the outer loop naturally stops.

**Key invariant:** at the start of every pass (for a given `width`), the *entire* list is already correctly sorted within every consecutive block of `width` nodes, even though it is not yet sorted overall. Each pass takes adjacent pairs of these `width`-sorted blocks and merges each pair into a single `2*width`-sorted block, which is exactly the invariant needed for the next pass at `2*width`.

Proof sketch: before the first pass, `width = 1`, and every block of 1 node is trivially "sorted" (a single element is always in order), establishing the base case. Assume before some pass that every consecutive block of `width` nodes is internally sorted. That pass repeatedly takes the next two adjacent `width`-blocks (call them `A` and `B`, both individually sorted by the inductive hypothesis) and merges them with the standard two-pointer merge — which, given two already-sorted inputs, always produces a single sorted output containing exactly their combined elements. The result is that every consecutive `2*width`-block is now internally sorted (each one being exactly the merge of the two `width`-blocks that used to occupy that same span), which is the inductive hypothesis for the next pass at `2*width`. This continues doubling until `width >= n`, at which point the single remaining "block" is the entire list, sorted in full — the loop condition `width < n` stops exactly at the pass after which this becomes true.

### Step-by-step trace

Trace on `head=[4,2,1,3]`, expected `[1,2,3,4]`. `n = 4`.

| width | blocks paired this pass | merge result | list after this pass |
|---|---|---|---|
| 1 | `[4]` and `[2]` | `[2,4]` | (first pair merged) |
| 1 (cont.) | `[1]` and `[3]` | `[1,3]` | full list after width=1 pass: `[2,4,1,3]` |
| 2 | `[2,4]` and `[1,3]` | `[1,2,3,4]` | full list after width=2 pass: `[1,2,3,4]` |
| — | `width` is now 4, and `4 < n(=4)` is false | loop stops | final: `[1,2,3,4]` |

Final list: `1 -> 2 -> 3 -> 4`, matching the expected output, reached in exactly `ceil(log2(4)) = 2` passes.

## Java 8 solution

```java
// Sort List
// ref: LC 148
// Given the head of a singly linked list, sort it into ascending order and
// return the new head.
// Required complexity: O(L log L) time; the optimal approach uses O(1) extra
// space (no recursion, no auxiliary array).
// Study page: ../sort-list.md
// Run: javac --release 8 SortList.java && java SortList

import java.util.*;

public class SortList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public static ListNode solve(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        int n = 0;
        for (ListNode c = head; c != null; c = c.next) {
            n++;
        }
        ListNode dummy = new ListNode(0, head);
        // Bottom-up merge sort: merge runs of size `width`, doubling width each
        // pass, until width covers the whole list. No recursion, O(1) extra space.
        for (int width = 1; width < n; width *= 2) {
            ListNode prevTail = dummy;
            ListNode cur = dummy.next;
            while (cur != null) {
                ListNode left = cur;
                ListNode right = split(left, width);
                cur = split(right, width);
                prevTail = merge(left, right, prevTail);
            }
        }
        return dummy.next;
    }

    // Severs and returns the sublist starting n nodes after head (cutting head's
    // run down to exactly n nodes). Returns null if fewer than n nodes remain,
    // leaving head's run un-truncated (it was already shorter than n).
    private static ListNode split(ListNode head, int n) {
        ListNode cur = head;
        for (int i = 1; i < n && cur != null; i++) {
            cur = cur.next;
        }
        if (cur == null) {
            return null;
        }
        ListNode rest = cur.next;
        cur.next = null;
        return rest;
    }

    // Merges two already-isolated sorted runs, appending the result after tail.
    // Returns the new tail (last node of the merged run) for the caller to chain from.
    private static ListNode merge(ListNode l1, ListNode l2, ListNode tail) {
        ListNode cur = tail;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                cur.next = l1;
                l1 = l1.next;
            } else {
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }
        cur.next = (l1 != null) ? l1 : l2;
        while (cur.next != null) {
            cur = cur.next;
        }
        return cur;
    }

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
                ListNode head = buildList(vals);
                ListNode result = solve(head);
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

- Time: O(n log n) — O(log n) passes (width doubles each time until it reaches or exceeds `n`), and each pass does O(n) total work across all its split/merge operations.
- Space: O(1) extra for the optimal bottom-up approach — a fixed set of pointers regardless of list length, since no recursion and no auxiliary array are used. The top-down recursive baseline is O(log n) extra space for its call stack.

## Java 8 pitfalls for this problem

- Forgetting the dummy-head idiom — without a dummy node before `head`, tracking "the tail of everything merged so far across all passes" needs an awkward special case for the very first merge of the very first pass; `dummy` sidesteps it by always having a valid predecessor to append onto.
- Losing the `next` pointer before relinking inside `split`: the line `ListNode rest = cur.next;` must happen *before* `cur.next = null;` — reversing that order overwrites the only reference to the remainder of the list before it has been saved, permanently losing every node after the cut point.
- Off-by-one in `split`'s loop bound: using `for (int i = 0; i < n; i++)` instead of `for (int i = 1; i < n; i++)` walks one node too far, cutting the run at length `n + 1` instead of `n`, which silently changes which nodes end up in "this pass's" left run versus the next run.
- Null-checking `fast` but not `fast.next` (or vice versa) in the *baseline* top-down approach's slow/fast middle-finding step — a two-pointer split that advances `fast` two nodes at a time must check both `fast != null && fast.next != null` before dereferencing `fast.next.next`, or it throws a `NullPointerException` on an even-length list where `fast` lands exactly on the last node.
- Comparing node values with `==` when they are boxed `Integer` rather than primitive `int` — not a live risk in this specific solution since `ListNode.val` is a primitive `int` throughout, but a version of `merge` rewritten to use `Integer` (say, to allow a `null`-sentinel value) would need `Integer.compare` or `.equals()`/`.compareTo()`, not `==`, to compare values outside the `-128..127` boxed-cache range safely.
- Declaring `ListNode` as a non-static inner class when it must be constructed from a `static` context such as `main` — Java requires a non-static inner class instance to carry a reference to an enclosing instance, which a `static` context does not have; declaring it `static class ListNode` (as done here) avoids the issue.

## Wrong approaches and why they fail

- **Sort by repeatedly finding the minimum remaining node and moving it to the front of the output (selection sort adapted to a linked list).** This is correct, but it is O(n^2) time — for each of the `n` output positions, it scans the remaining unsorted portion (O(n)) to find the minimum — which fails the O(n log n) requirement entirely for large lists, even though it uses no extra memory at all.
- **Copy every value into an array, sort the array with `Arrays.sort`, and write the sorted values back into the existing nodes in order.** This meets the time bound (O(n log n)) but not the O(1) extra-space bar, since the array itself is O(n) extra space; it is also a fundamentally different operation from "rearranging nodes" — it overwrites `val` fields on the *existing* node objects rather than relinking `next` pointers, which happens to produce a correct-looking result here only because nothing else in this problem depends on node identity (unlike, say, the deep-copy problem elsewhere in this group, where identity does matter).
- **Use the bottom-up merge approach, but forget to advance `cur` correctly between iterations of the inner while-loop within a single pass** (for example, reusing `left` or `right` instead of the freshly split remainder as the next `cur`). Counterexample: on `head=[4,2,1,3]` with `width=1`, if `cur` is mistakenly left pointing at `right` (`[1]`, before its own split) instead of the value actually returned by `split(right, width)`, the second pair `([1], [3])` for that pass either gets processed twice or the node `3` is silently dropped from the output entirely, since the pointer bookkeeping that walks across the whole list within one pass depends on always advancing `cur` to whatever remains *after* both runs of the current pair have been isolated.

## Variants

- **Sort a doubly linked list.** The same bottom-up width-doubling structure applies to `next` pointers; `prev` pointers then need a single additional cleanup pass afterward (or can be fixed up during the same merge step) to make each node's `prev` correctly point to its new predecessor.
- **Sort the list using insertion sort instead of merge sort** (a common simpler-but-slower variant asked as a follow-up) — repeatedly remove the next node from the unsorted remainder and insert it into its correct position within an already-sorted prefix being built up; O(n^2) worst case, but O(1) extra space and simple to implement, useful when the input is nearly sorted already.
- **Sort by a custom secondary key when primary values tie** (for example, sort primarily by `val` but break ties by original list position to guarantee stability). The two-pointer `merge` step's tie-breaking rule (`l1.val <= l2.val` favors `l1` on a tie) already makes this particular bottom-up merge sort *stable* as written — nodes with equal values keep their original relative order — so no change is needed for that specific request; a genuinely different secondary key would just change what `merge` compares.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `[4,2,1,3]` | `[1,2,3,4]` | classic small out-of-order list |
| 2 | `[]` | `[]` | empty list |
| 3 | `[1]` | `[1]` | single node |
| 4 | `[2,1]` | `[1,2]` | smallest possible swap |
| 5 | `[1,2,3,4,5]` | `[1,2,3,4,5]` | already sorted ascending |
| 6 | `[5,4,3,2,1]` | `[1,2,3,4,5]` | fully reverse sorted |
| 7 | `[3,3,3]` | `[3,3,3]` | all values equal |
| 8 | `[-1,5,3,4,0]` | `[-1,0,3,4,5]` | negative and positive values mixed |
| 9 | `[1,3,2,3,1]` | `[1,1,2,3,3]` | scattered duplicates, not all-equal |
| 10 | `[9,1,8,2,7,3,6,4,5]` | `[1,2,3,4,5,6,7,8,9]` | larger odd-length list, multiple merge passes |
