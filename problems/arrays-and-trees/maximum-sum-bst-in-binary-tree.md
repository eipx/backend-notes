# Maximum Sum BST in Binary Tree
`ref: LC 1373` · Difficulty: Hard · Pattern: post-order returning (isBst, min, max, sum)

## Problem
Given the root of a binary tree, consider every subtree in it (a subtree rooted at any node, including all of that node's descendants). Among the subtrees that happen to also be valid binary search trees, find the one with the largest sum of node values, and report that sum. A single leaf node is always a valid BST by itself. If every value in the tree is negative, no positive-sum BST subtree exists; in that case, choosing no subtree at all is always an available option, so the reported answer is `0`.

## Constraints
- The tree has between `1` and `4 * 10^4` nodes.
- Node values are integers, `-4 * 10^4 <= val <= 4 * 10^4`.
- "Valid binary search tree" means: for every node in the subtree, every value in its left subtree is strictly less than the node's own value, and every value in its right subtree is strictly greater -- equal values anywhere in the subtree disqualify it.

## Worked examples
1. Tree `[2,1,3]` (root 2, left 1, right 3) -> `6`. The whole tree is already a valid BST (`1 < 2 < 3`), so its own total sum, `2+1+3=6`, is the answer -- no other subtree can beat the whole tree once the whole tree already qualifies.
2. Tree `[1,4,3,2,4,2,5]` (root 1; left child 4 with children 2 and 4; right child 3 with children 2 and 5) -> `10`. The whole tree is not a BST (the left child, `4`, is not less than the root, `1`). The subtree rooted at the right child (`3`, with children `2` and `5`) is a valid BST (`2 < 3 < 5`) summing to `10`. The subtree rooted at the left child (`4`, with children `2` and `4`) is *not* a valid BST, since its right child is also `4` -- equal values are never allowed. No other subtree beats `10`.
3. Tree `[-1,-2,-3]` (root -1, left -2, right -3) -> `0`. The whole tree is not a BST (`-3` is not greater than `-1`). The only valid BST subtrees are the two leaves, `-2` and `-3`, both negative -- since choosing no subtree at all is always allowed, the reported answer floors at `0` rather than returning a negative sum.
4. Tree `[5]` (single node) -> `5`. A lone node is trivially a valid BST, and its sum is simply its own (positive) value.

## Edge cases checklist
- A single-node tree (always a valid BST; if its value is negative, the floor-at-`0` rule still applies).
- The whole tree is already a valid BST (the answer is the sum of every node).
- Every value in the tree is negative, so no positive-sum BST subtree exists (the floor-at-`0` rule).
- A node whose immediate children look locally consistent (`node.val > left.val` and `node.val < right.val`) but whose subtree is still invalid because of a deeper descendant violating the true BST bound -- confirms full min/max range tracking is required, not just a check against immediate children.
- A duplicate value appearing anywhere between an ancestor and a descendant (must invalidate BST-ness there, since equal values are never allowed).
- A tree where the best-sum valid BST is a strict descendant subtree, not the root's own subtree.
- A tree containing `null` children scattered at different depths (not a perfect/complete shape), to confirm the level-order tree builder used by this page's test harness handles missing children correctly.

## Approach

### Brute force
For every node in the tree, independently check whether the subtree rooted there is a valid BST (e.g. via an in-order traversal, confirming the values come out strictly increasing) and, if so, separately sum its values -- both by traversing that entire subtree again from scratch. Taking the maximum sum over all qualifying subtrees this way repeats the validate-and-sum traversal once per node, giving `O(n^2)` time in the worst case (for example, a tree shaped so every node's subtree really is a valid BST, forcing a full independent traversal at every one of the `n` nesting levels).

### Optimal
Do a single post-order traversal (children fully processed before their parent). At each node, combine the results already computed for its left and right children into one compact tuple describing the node's own subtree: `(isBst, min, max, sum)`. A subtree is a valid BST exactly when both children report `isBst = true` and the node's own value is strictly greater than the left child's max (or there is no left child) and strictly less than the right child's min (or there is no right child). When valid, the subtree's `min`/`max`/`sum` are derived in `O(1)` from the node's own value and its children's already-known `min`/`max`/`sum`; whenever a subtree qualifies, its sum is compared against a running global best.

**Key invariant:** because the traversal is post-order, by the time a node's own tuple is computed, both of its children's tuples already correctly and completely describe whether each child's *entire* subtree is a valid BST and, if so, its exact min, max, and sum -- so validating the current node only ever needs a constant amount of additional work (comparing the node's value against its children's already-summarized bounds), never a fresh traversal of either child's subtree.

Proof sketch: the base case is an empty (`null`) child, treated as a neutral element that trivially satisfies "is a BST" with an inverted, non-constraining range (`min = +infinity`, `max = -infinity`, `sum = 0`) so it never blocks a real parent's own comparisons. By induction, if both children's tuples are correct, the parent's `isBst` check (`left.isBst && right.isBst && node.val > left.max && node.val < right.min`) exactly captures the BST definition for the parent's subtree: every value on the left must be less than `node.val`, which holds if and only if the left subtree is itself a valid BST *and* its maximum value is less than `node.val` (values further down could not violate the bound if the maximum doesn't); the symmetric argument holds for the right side. The parent's own `min`/`max`/`sum` are then simple `O(1)` combinations of already-correct child values, so the invariant holds at the parent too, and induction carries it all the way to the root in one linear pass.

### Step-by-step trace
Trace on the tree `[1,4,3,2,4,2,5]`:
```
        1
       / \
      4   3
     / \ / \
    2  4 2  5
```
Post-order visits leaves first, then each pair of leaves' parent, then the root:

| node visited | left tuple | right tuple | own value | isBst? | resulting tuple | running best |
|---|---|---|---|---|---|---|
| leaf 2 (4's left child) | none (neutral) | none (neutral) | 2 | true (trivially) | (true, min=2, max=2, sum=2) | best = max(0,2) = 2 |
| leaf 4 (4's right child) | none | none | 4 | true | (true, min=4, max=4, sum=4) | best = max(2,4) = 4 |
| node 4 (combining its two leaves) | (true,2,2,2) | (true,4,4,4) | 4 | `4 > left.max(2)` yes; `4 < right.min(4)`? no (equal) -> **invalid** | (false, --, --, --) | best stays 4 |
| leaf 2 (3's left child) | none | none | 2 | true | (true,2,2,2) | best = max(4,2) = 4 |
| leaf 5 (3's right child) | none | none | 5 | true | (true,5,5,5) | best = max(4,5) = 5 |
| node 3 (combining its two leaves) | (true,2,2,2) | (true,5,5,5) | 3 | `3 > 2` yes; `3 < 5` yes -> valid | (true, min=2, max=5, sum=10) | best = max(5,10) = 10 |
| root 1 (combining node 4 and node 3) | (false,...) | (true,2,5,10) | 1 | `left.isBst` is false -> **invalid immediately** | (false, --, --, --) | best stays 10 |

Final answer: `10`, matching worked example 2. Note that the root being invalid does not disqualify the valid subtree found further down at node 3.

## Java 8 solution
```java
public class MaximumSumBstInBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    // Post-order result for one subtree: whether it's a valid BST, and (if so)
    // its min value, max value, and sum -- everything a parent needs in O(1).
    private static class Info {
        boolean isBst;
        long min;
        long max;
        long sum;
        Info(boolean isBst, long min, long max, long sum) {
            this.isBst = isBst;
            this.min = min;
            this.max = max;
            this.sum = sum;
        }
    }

    public static int solve(TreeNode root) {
        long[] best = new long[]{0}; // floor: an empty "no subtree chosen" selection always sums to 0
        postorder(root, best);
        return (int) best[0];
    }

    private static Info postorder(TreeNode node, long[] best) {
        if (node == null) {
            // Neutral element for an empty subtree: an inverted (max, min) range means
            // it never constrains a real parent's own min/max comparison.
            return new Info(true, Long.MAX_VALUE, Long.MIN_VALUE, 0);
        }
        Info left = postorder(node.left, best);
        Info right = postorder(node.right, best);
        if (left.isBst && right.isBst && node.val > left.max && node.val < right.min) {
            long min = Math.min(node.val, left.min);
            long max = Math.max(node.val, right.max);
            long sum = left.sum + right.sum + node.val;
            best[0] = Math.max(best[0], sum);
            return new Info(true, min, max, sum);
        }
        return new Info(false, 0, 0, 0); // not a BST; these sentinel values are never read by a valid parent
    }
}
```

## Complexity
- Time: `O(n)` -- one post-order traversal, doing `O(1)` work per node.
- Space: `O(h)` for the recursion stack, where `h` is the tree's height (`O(log n)` for a balanced tree, `O(n)` for a completely skewed one).

## Java 8 pitfalls for this problem
- Using `long` (not `int`) for the `min`/`max` fields and their sentinels (`Long.MAX_VALUE`/`Long.MIN_VALUE`) is a defensive habit: this problem's own bounds (`-4*10^4` to `4*10^4`) never actually reach `Integer.MIN_VALUE`/`MAX_VALUE`, but relying on `int` sentinels colliding with real data is exactly the kind of off-by-a-constant bug that's easy to introduce when a bound changes later.
- The BST check must use strict `>` and `<`, never `>=`/`<=` -- an equal value between an ancestor and a descendant is never valid in a BST (see node "4" in the trace above, invalidated by its own right child also being `4`).
- Comparing only the immediate children's values (`node.val > node.left.val`) instead of the full aggregated subtree `min`/`max` is the single most common bug in this problem: it produces a false "looks fine locally" result whenever a deeper descendant several levels down actually violates the true ordering constraint.
- The neutral sentinel for an empty subtree is deliberately *inverted* from what "empty min/max" might naively suggest (`min = +infinity`, `max = -infinity`, not the other way around) -- this is exactly what lets the same comparison code work uniformly whether or not a given child exists, without a separate null-check branch at every comparison site.
- A very large, heavily skewed input (up to `4*10^4` nodes in a single chain) drives recursion depth to match, which risks a `StackOverflowError` on an adversarial input even though it will never trigger on this page's own (much smaller) test cases; an iterative post-order traversal with an explicit stack is the production-grade fix if that scale is a real concern.

## Wrong approaches and why they fail
1. **Check only `node.val > node.left.val && node.val < node.right.val` (immediate children's own values, not full subtree bounds).** Counterexample: root `10` with left child `5` (`5 < 10`, looks fine locally), but `5`'s own right child is `15` -- a value that must be less than `10` to keep the whole left subtree valid relative to the root, but the naive local check never looks past the immediate child and would incorrectly call the whole tree a valid BST.
2. **Validate BST-ness and compute the sum in two entirely separate traversals per subtree under consideration (the brute-force approach), rather than combining both into one post-order pass.** Correct, but `O(n^2)` overall, since it repeats a full traversal of the same subtree once for validation and again for summation, for every one of the `n` possible subtree roots.
3. **Forget the floor-at-`0` rule and return the true (possibly negative) best subtree sum directly.** Counterexample: a single node valued `-5` -- the only valid BST subtree sums to `-5`, but this page's stated convention (an empty "choose nothing" selection is always available) means the correct reported answer is `0`, not `-5`.

## Variants
- **Return the root node reference of the best BST subtree, not just its sum.** Track a "best node" pointer alongside the running best sum, updating both together whenever a strictly larger valid sum is found.
- **Count how many valid BST subtrees exist in total, rather than finding the best sum.** Increment a counter every time the `isBst` check succeeds, instead of comparing to a running maximum.
- **Largest BST subtree by node count instead of by value sum** (a closely related, very common variant) -- carry a `size` field through the same `(isBst, min, max, size)` tuple in place of `sum`; the traversal structure is otherwise identical.

## Test cases
| # | tree (level-order, null = missing child) | expected | what it tests |
|---|---|---|---|
| 1 | `[2,1,3]` | 6 | whole tree is already a valid BST |
| 2 | `[1,4,3,2,4,2,5]` | 10 | best BST is a subtree, not the root; duplicate-value trap |
| 3 | `[-1,-2,-3]` | 0 | all negative values, floor-at-0 rule |
| 4 | `[5]` | 5 | single node |
| 5 | `[1,null,2,null,3]` | 6 | right-skewed chain, still a valid whole-tree BST |
| 6 | `[3,2,null,1]` | 6 | left-skewed chain, still a valid whole-tree BST |
| 7 | `[5,1,4,null,null,3,6]` | 13 | root invalid, right subtree is the best valid BST |
| 8 | `[4,2,4]` | 4 | minimal duplicate-value trap, only leaves qualify |
| 9 | `[10,5,15,3,7,12,20,1,4,6,8,11,13,18,25]` | 158 | larger tree, entirely a valid BST |
| 10 | `[1,null,3,2]` | 6 | unusual shape (value "crosses" sides) still a valid whole-tree BST |
