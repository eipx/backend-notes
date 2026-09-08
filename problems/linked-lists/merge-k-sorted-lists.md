# Merge k Sorted Lists
`ref: LC 23` · Difficulty: Hard · Pattern: min-heap over the current heads of k sorted lists, with pairwise divide-and-conquer as the heap-free alternative

## Problem

Given an array of `k` singly linked lists, each already sorted in ascending order, merge all of them into a single sorted linked list and return its head. Any list in the input array may be empty (a `null` entry); the merged result must contain every node from every input list exactly once, in overall ascending order, using the existing node objects rather than manufacturing new ones.

Input: `lists`, an array of `k` list heads (any entry may be `null` to represent an empty list).
Output: the head of one fully merged, ascending, singly linked list.

## Constraints

- `k` (the number of lists) ranges from 0 to `10^4`.
- The total number of nodes across all lists combined is up to `10^4`.
- Each individual input list is already sorted ascending; nothing about the merge should re-sort within a list, only interleave across lists.
- Node values fit in a 32-bit signed int.
- With `k` lists and `N` total nodes, an approach that is O(N \cdot k) (for example, repeatedly scanning all `k` current heads to find the smallest one) is correct but not the target; the intended complexity is O(N log k), which points at a heap sized by `k`, not by `N`.

## Worked examples

1. `lists=[[1,4,5],[1,3,4],[2,6]]` → `[1,1,2,3,4,4,5,6]`. Three sorted lists interleave: comparing the current fronts `1, 1, 2` picks a `1` first (either list works, both are valid ties), then the remaining fronts keep getting compared and the smallest is emitted each time until all three lists are exhausted.
2. `lists=[]` (no lists at all) → `[]`. There is nothing to merge, so the result is the empty list.
3. `lists=[[],[]]` (two lists, both empty) → `[]`. Having `k=2` "lists" contributes no nodes at all if every one of them is empty.
4. `lists=[[5],[1],[3]]` → `[1,3,5]`. Three lists that each hold exactly one node merge into a 3-node list ordered by those single values.

## Edge cases checklist

- `lists` itself is an empty array (`k=0`) — no lists to merge, result is empty.
- `lists` contains only empty (`null`) entries — same result as `k=0` in practice, but exercised through non-trivial `k`.
- A single list in the array — the "merge" is really just returning that one list unchanged.
- A mix of empty and non-empty lists in the same call.
- All lists contain exactly one node each (`k` singleton lists).
- Duplicate values across different lists (and within the same list) — ties must not drop any node, and any consistent tie-breaking is acceptable as long as ascending order is preserved.
- Negative values mixed with positive ones across different lists.
- A larger `k` (more than 2 or 3 lists) with uneven list lengths, to confirm the heap correctly keeps advancing whichever list still has the smallest remaining head, not just alternating between a fixed couple of lists.

## Approach

### Brute force

Walk every list, copying every node's value into one flat array (or `List<Integer>`), ignoring the fact that each individual list arrives pre-sorted. Sort that flat array from scratch, then build a brand-new linked list from the sorted values. This is correct — O(N log N) time where `N` is the total node count — but it throws away the useful information that each input list was already sorted; the resulting time bound (`N log N`) is generally worse than the heap approach's `N log k` whenever `k` is much smaller than `N`, which is the common case (few lists, each fairly long).

### Optimal

Maintain a min-heap (`PriorityQueue`) that holds, at any moment, at most one node per still-non-empty input list — specifically, each list's current smallest not-yet-emitted node (which, because each list is individually sorted, is always that list's own head at that point). Seed the heap with the head of every non-empty list. Then repeatedly: poll the smallest node from the heap, append it to the output, and if that node had a successor within its own list, push that successor onto the heap (it is now that list's new smallest remaining node). Stop when the heap is empty.

**Key invariant:** at every point during the merge, the heap contains exactly one node from each list that still has unemitted nodes, and that node is always the smallest remaining node of its own list. Consequently, the global minimum among *all* remaining nodes across every list must be the minimum of the (at most `k`) nodes currently sitting in the heap — so polling the heap's root always yields the correct next node of the fully merged output.

Proof sketch: by induction on the number of nodes emitted so far. Base case: before any node is emitted, the heap holds exactly the head of each non-empty list — trivially each list's smallest remaining node, since nothing from any list has been emitted yet. Inductive step: assume the invariant holds before emitting the `(i+1)`-th node. The heap's root is the minimum among the current per-list minimums, which — since every other node in every list is either already emitted or comes later in its own list's sorted order than that list's current minimum — is also the minimum among *all* remaining nodes everywhere. Emitting it is therefore correct. After emitting it, its own successor (if any) is pushed as the new minimum for that same list (all of that list's other remaining nodes are still ahead of it in sorted order, by the precondition that each input list was sorted to begin with), and every other list's entry in the heap is untouched and still correctly its own minimum. The invariant is restored, completing the induction. The process terminates when the heap empties, which happens exactly when every list has been fully drained.

### Step-by-step trace

Trace on `lists=[[1,4,5],[1,3,4],[2,6]]`, expected `[1,1,2,3,4,4,5,6]`. Write each heap entry as `value(list)`.

| step | heap before poll | polled | pushed (that node's successor) | output so far |
|---|---|---|---|---|
| seed | — | — | 1(A), 1(B), 2(C) | `[]` |
| 1 | {1(A), 1(B), 2(C)} | 1(A) | 4(A) | `[1]` |
| 2 | {1(B), 2(C), 4(A)} | 1(B) | 3(B) | `[1,1]` |
| 3 | {2(C), 3(B), 4(A)} | 2(C) | 6(C) | `[1,1,2]` |
| 4 | {3(B), 4(A), 6(C)} | 3(B) | 4(B) | `[1,1,2,3]` |
| 5 | {4(A), 4(B), 6(C)} | 4(A) | 5(A) | `[1,1,2,3,4]` |
| 6 | {4(B), 5(A), 6(C)} | 4(B) | (B exhausted, nothing pushed) | `[1,1,2,3,4,4]` |
| 7 | {5(A), 6(C)} | 5(A) | (A exhausted, nothing pushed) | `[1,1,2,3,4,4,5]` |
| 8 | {6(C)} | 6(C) | (C exhausted, nothing pushed) | `[1,1,2,3,4,4,5,6]` |
| 9 | {} | — | heap empty, stop | `[1,1,2,3,4,4,5,6]` |

Final output matches the expected `[1,1,2,3,4,4,5,6]`.

## Java 8 solution

```java
// Merge k Sorted Lists
// ref: LC 23
// Given an array of k singly linked lists, each already sorted ascending,
// merge them into one sorted linked list and return its head.
// Required complexity: O(N log k) time (N = total nodes across all lists),
// O(k) extra space for the heap.
// Study page: ../merge-k-sorted-lists.md
// Run: javac --release 8 MergeKSortedLists.java && java MergeKSortedLists

import java.util.*;

public class MergeKSortedLists {

    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public static ListNode solve(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }
        // Min-heap ordered by node value; holds at most one node per non-empty
        // list at any time (that list's current smallest un-emitted node).
        PriorityQueue<ListNode> heap = new PriorityQueue<ListNode>(Math.max(1, lists.length),
            new Comparator<ListNode>() {
                public int compare(ListNode a, ListNode b) {
                    return Integer.compare(a.val, b.val);
                }
            });
        for (ListNode node : lists) {
            if (node != null) {
                heap.add(node);
            }
        }
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (!heap.isEmpty()) {
            ListNode smallest = heap.poll();
            tail.next = smallest;
            tail = tail.next;
            if (smallest.next != null) {
                heap.add(smallest.next);
            }
        }
        tail.next = null; // defensive: the last emitted node's stale next must not leak out
        return dummy.next;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {new int[][]{{1,4,5},{1,3,4},{2,6}}, intArr(1,1,2,3,4,4,5,6)},
            {new int[][]{}, intArr()},
            {new int[][]{{}}, intArr()},
            {new int[][]{{},{}}, intArr()},
            {new int[][]{{1,2,3}}, intArr(1,2,3)},
            {new int[][]{{5},{1},{3}}, intArr(1,3,5)},
            {new int[][]{{},{1,2,3}}, intArr(1,2,3)},
            {new int[][]{{1,1,1},{1,1}}, intArr(1,1,1,1,1)},
            {new int[][]{{-3,-1,0},{2,4},{-2}}, intArr(-3,-2,-1,0,2,4)},
            {new int[][]{{1,5,9},{2,6,10},{3,7,11},{4,8,12}}, intArr(1,2,3,4,5,6,7,8,9,10,11,12)}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[][] listsVals = (int[][]) cases[i][0];
            int[] expected = (int[]) cases[i][1];
            try {
                ListNode[] lists = new ListNode[listsVals.length];
                for (int j = 0; j < listsVals.length; j++) {
                    lists[j] = buildList(listsVals[j]);
                }
                ListNode result = solve(lists);
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

- Time: O(N log k) — the heap never holds more than `k` entries at once, so each of the `N` total poll/push operations costs O(log k).
- Space: O(k) extra for the heap itself, plus O(1) additional pointers; the output list reuses the existing nodes rather than allocating new ones, so it is not counted as extra space.

## Java 8 pitfalls for this problem

- Writing the comparator as `return a.val - b.val;` instead of `Integer.compare(a.val, b.val)` — subtraction of two `int`s can overflow near `Integer.MIN_VALUE`/`MAX_VALUE` and silently flip the sign of the comparison; `Integer.compare` has no such failure mode.
- Comparing `ListNode` objects with `==` inside the comparator (or anywhere else) when the intent is to compare their `.val` fields — `==` on two distinct node objects is always `false` regardless of value, and this is a different bug from the usual boxed-`Integer` `==` trap (that trap is about comparing boxed `Integer` *values*, not about comparing two different objects that happen to hold equal `int` fields); either way, the comparator here correctly reaches into `.val` rather than comparing node references.
- Writing `new PriorityQueue<>(...)` with a diamond on an anonymous `Comparator` class — Java 8 does not support the diamond operator on anonymous inner classes (that support was added in Java 9); the comparator here is declared with the explicit type argument, `new Comparator<ListNode>() { ... }`, which is required syntax on Java 8.
- Forgetting to null-check each entry of `lists` before adding it to the heap — the array may contain `null` for an empty list, and pushing a `null` node reference into a `PriorityQueue<ListNode>` either throws a `NullPointerException` immediately or corrupts the heap's internal comparisons the next time it tries to compare that `null` against something else.
- Sizing `new PriorityQueue<ListNode>(lists.length)` when `lists.length` is `0` — the `PriorityQueue(int initialCapacity)` constructor throws `IllegalArgumentException` for a non-positive capacity, which is why the initial capacity here is guarded with `Math.max(1, lists.length)`.
- Declaring `ListNode` as a non-static inner class when it must be constructed from a `static` context such as `main` — Java requires a non-static inner class instance to carry a reference to an enclosing instance, which a `static` context does not have; declaring it `static class ListNode` (as done here) avoids the issue.

## Wrong approaches and why they fail

- **Repeatedly scan all `k` current list heads on every step to find the overall minimum, without a heap.** This is correct, but it is O(N \cdot k) time instead of O(N log k) — for `k` close to its upper bound (`10^4`) and a similarly large total node count, this can be roughly `k` times slower than the heap approach, which is exactly the gap the heap is designed to close.
- **Merge the lists two at a time, sequentially, by folding the running merged result with each next list one by one (`merge(merge(merge(l1, l2), l3), l4), ...`).** This produces a correct result, but its total work is worse than pairwise divide-and-conquer: the running merged list keeps growing, so each successive two-list merge does more work than necessary — the total cost degrades toward O(N \cdot k) in the worst case (imagine merging one huge already-combined list against many small lists one at a time) rather than the O(N log k) achieved by pairing lists of similar size together (see Variants below).
- **Concatenate all the lists first (ignoring their individual sortedness) and then run a generic O(N log N) sort on the combined list.** Not wrong in the sense of an incorrect answer, but it throws away the fact that each of the `k` input lists was already sorted — the heap approach exploits that fact to reach O(N log k) instead, which is asymptotically better whenever `k \ll N`.

## Variants

- **Divide-and-conquer pairwise merge, avoiding a heap entirely.** Instead of a `PriorityQueue`, repeatedly merge the lists in disjoint pairs (list 1 with list 2, list 3 with list 4, ...) using an ordinary two-list merge, producing `k/2` merged lists; repeat this pairing-and-merging process on the resulting lists until only one list remains. Each "round" of pairwise merging does O(N) total work across all pairs, and there are O(log k) rounds (the list count halves each round), giving the same O(N log k) time bound as the heap approach without needing any heap data structure at all.
- **Merge k sorted *arrays* instead of k sorted linked lists.** The exact same min-heap idea applies, holding `(value, listIndex, elementIndex)` triples instead of node references, and advancing `elementIndex` within a list instead of following a `next` pointer — the heap invariant and its proof are unchanged.
- **Find only the smallest `m` elements across all k sorted lists, without merging everything.** Run the identical heap-based merge, but stop after emitting `m` nodes instead of draining the heap completely — the same invariant guarantees the first `m` nodes emitted are exactly the `m` smallest across all lists.

## Test cases

| # | input lists | expected | what it tests |
|---|---|---|---|
| 1 | `[[1,4,5],[1,3,4],[2,6]]` | `[1,1,2,3,4,4,5,6]` | classic interleave across three lists with a tie |
| 2 | `[]` | `[]` | no lists at all |
| 3 | `[[]]` | `[]` | one empty list |
| 4 | `[[],[]]` | `[]` | multiple empty lists |
| 5 | `[[1,2,3]]` | `[1,2,3]` | single list, already-correct answer is itself |
| 6 | `[[5],[1],[3]]` | `[1,3,5]` | three singleton lists |
| 7 | `[[],[1,2,3]]` | `[1,2,3]` | mix of empty and non-empty lists |
| 8 | `[[1,1,1],[1,1]]` | `[1,1,1,1,1]` | all-equal values across lists |
| 9 | `[[-3,-1,0],[2,4],[-2]]` | `[-3,-2,-1,0,2,4]` | negative and positive values mixed across lists |
| 10 | `[[1,5,9],[2,6,10],[3,7,11],[4,8,12]]` | `[1,2,3,4,5,6,7,8,9,10,11,12]` | larger k=4 with evenly interleaved values |
