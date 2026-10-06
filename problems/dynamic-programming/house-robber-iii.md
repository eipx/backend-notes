# House Robber III
`ref: LC 337` · Difficulty: Medium · Pattern: tree DP, each node returns a pair (best total if its house is robbed, best total if it is not robbed)

## Problem

The houses form a binary tree. The root is the entrance, and every other house hangs below exactly one parent house. Each house holds some money, `node.val`. An alarm goes off if you take the money from two houses that are directly linked, that is, from a house and one of its children. Return the largest total you can take without setting off the alarm.

Input: the root of a binary tree of houses (the runner also passes the empty tree, as `null`).
Output: a single integer, the largest total over all sets of houses in which no house is chosen together with its parent or one of its children.

## Constraints

- The tree has between `1` and `10^4` nodes.
- `0 <= node.val <= 10^4`
- A search over every set of houses looks at `2^n` candidates. The recursion that tries "take this house" (continue from its four grandchildren) or "skip this house" (continue from its two children) is better, but the same house is reached once as a child and once as a grandchild, so on a chain of `n` houses the number of calls follows the Fibonacci pattern, about `1.6^n`. Thirty houses already need around seven million calls. Each house only has to be solved once, which makes the work linear.
- The total of all values is at most `10^4 * 10^4 = 10^8`, which fits in `int`.

## Worked examples

1. Tree `[3,2,3,null,3,null,1]` (root 3; its left child 2 has a right child 3; its right child 3 has a right child 1) -> `7`. Take the root, the 3 under the 2, and the 1 under the right 3: `3 + 3 + 1`. Taking the two children of the root instead gives `2 + 3 = 5`.
2. Tree `[3,4,5,1,3,null,1]` (root 3; left child 4 with children 1 and 3; right child 5 with a right child 1) -> `9`. Take the 4 and the 5, which are both children of the root. The root plus its grandchildren gives `3 + 1 + 3 + 1 = 8`.
3. Tree `[5]` -> `5`. A single house has no parent and no children, so it is taken.
4. Tree `[4,1,null,2,null,3]` (a chain going down to the left: 4, then 1, then 2, then 3) -> `7`. Take the first and the last, `4 + 3`. The pair 4 and 2 gives only 6, and the pair 1 and 3 gives only 4.

## Edge cases checklist

- The empty tree (the answer is 0). The constraints promise at least one node, but a missing root is the base case of the recursion, so it is worth checking.
- A single node (the answer is its value, and 0 when the value is 0).
- All values zero (the answer is 0 whatever is chosen).
- The children together beat the parent (`[2, 1, 3]` is 4), and the parent beats its children (`[10, 1, 1]` is 10).
- A chain that goes only left, and a chain that goes only right. Every node has one missing child, which tests the `null` handling on both sides (`[4, 1, null, 2, null, 3]` and `[3, null, 4, null, 3]`).
- One child is taken while the other is skipped so that its own children can be taken (`[2, 10, 1, 1, 1, 5, 5]` is 20: the 10 on the left, and the two 5s under the 1 on the right).
- A tree where the best set mixes levels, so that taking every house at even depth or every house at odd depth is not enough.
- A perfect tree with equal values (`15` ones is `10`: the eight leaves plus the two children of the root, which are not linked to the leaves).
- A long chain. The constraints allow a chain of `10^4` nodes, which is `10^4` nested calls deep. The runner uses 1000, and the deepest case is not among the tests here (see the pitfalls).

## Approach

### Brute force

Look at the root. If it is taken, none of its children can be taken, so the rest of the total comes from the four grandchildren (each of them free to be taken or not): `root.val + rob(ll) + rob(lr) + rob(rl) + rob(rr)`. If it is skipped, the rest comes from its two children, each free: `rob(left) + rob(right)`. The answer is the larger of the two, and a missing node is worth 0. With no memory, every node is solved again each time it is reached as a child and again as a grandchild, so the number of calls explodes on deep trees (about `1.6^n` on a chain). Storing one answer per node, for instance in a map keyed by the node, removes the repeats.

### Optimal

The repeats come from asking one question twice, "what is the best total in this subtree?", once with the parent taken and once with the parent skipped. Answer both questions at once. Each node returns a pair:

- `robbed`: the best total inside this node's subtree when this node is taken.
- `skipped`: the best total inside this node's subtree when this node is not taken.

A missing child is the pair `(0, 0)`. For a real node with `left` and `right` pairs from its children:

- `robbed = node.val + left.skipped + right.skipped`. If this node is taken, both children must be skipped, but everything below them is free.
- `skipped = max(left.robbed, left.skipped) + max(right.robbed, right.skipped)`. If this node is skipped, each child is free to be taken or not, and picks whichever is better for its own subtree.

The answer for the whole tree is `max(root.robbed, root.skipped)`. The nodes are visited in post-order, children before parent, so both pairs are ready when a node needs them. A single number per node would not be enough, because the parent has to know what the child is worth in each of the two cases.

**Key invariant:** for every node, the pair that `visit` returns is exactly (the best total in its subtree with the node taken, the best total in its subtree with the node skipped). This holds by induction on the height of the subtree. An empty subtree is `(0, 0)`. For a node, once its own house is taken or skipped, its left subtree and right subtree share no link with each other, so the best choices inside them are independent. When the node is taken, each child must be skipped and its subtree contributes its `skipped` value. When the node is skipped, each child is unrestricted and contributes the larger of its two values. Both formulas use only values that are already exact for the children, so they are exact for the node too, and the root's pair gives the answer.

### Step-by-step trace

Run on the tree `[3,2,3,null,3,null,1]`. The nodes finish in post-order. A missing child counts as `(0, 0)`, and each pair is written `(robbed, skipped)`.

| step | node | val | left pair | right pair | robbed = val + left.skipped + right.skipped | skipped = max(left) + max(right) | pair returned |
|---|---|---|---|---|---|---|---|
| 1 | the 3 under the 2 | 3 | (0, 0) | (0, 0) | 3 | 0 | (3, 0) |
| 2 | the 2 | 2 | (0, 0) | (3, 0) | 2 + 0 + 0 = 2 | 0 + 3 = 3 | (2, 3) |
| 3 | the 1 | 1 | (0, 0) | (0, 0) | 1 | 0 | (1, 0) |
| 4 | the right 3 | 3 | (0, 0) | (1, 0) | 3 + 0 + 0 = 3 | 0 + 1 = 1 | (3, 1) |
| 5 | the root 3 | 3 | (2, 3) | (3, 1) | 3 + 3 + 1 = 7 | 3 + 3 = 6 | (7, 6) |

The answer is `max(7, 6) = 7`, which is worked example 1. Step 5 shows the shape of the idea: with the root taken, the two children contribute their skipped values (3 and 1), and with the root skipped, they contribute their larger values (3 and 3).

Walking back down to see the houses: the root's `robbed` value won (`7` against `6`), so the root is taken and both children are skipped. The 2 is skipped, and its `skipped` value `3` came from taking its right child, the leaf 3. The right 3 is skipped, and its `skipped` value `1` came from taking its right child, the leaf 1. The houses are the root, the leaf 3 and the leaf 1.

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.Deque;

public class HouseRobberIII {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    // What a subtree reports to its parent: the best total inside the subtree
    // when its root is taken, and the best total when its root is not taken.
    // The fields never change after construction, so one shared instance can
    // stand for every empty subtree.
    private static class Pair {
        final int robbed;
        final int skipped;
        Pair(int robbed, int skipped) {
            this.robbed = robbed;
            this.skipped = skipped;
        }
    }

    // An empty subtree has no money, whether or not its (missing) root is taken.
    private static final Pair EMPTY = new Pair(0, 0);

    // Largest total that can be taken from the houses of a binary tree when a
    // house and its parent house may not both be taken.
    public static int solve(TreeNode root) {
        Pair result = visit(root);
        return Math.max(result.robbed, result.skipped);
    }

    // Post-order: both children are finished before their parent is combined.
    private static Pair visit(TreeNode node) {
        if (node == null) {
            return EMPTY;
        }
        Pair left = visit(node.left);
        Pair right = visit(node.right);

        // Take this node: both children must be skipped, but everything below
        // them is free to choose.
        int robbed = node.val + left.skipped + right.skipped;
        // Skip this node: each child is free to be taken or skipped, whichever
        // is better for its own subtree.
        int skipped = Math.max(left.robbed, left.skipped) + Math.max(right.robbed, right.skipped);
        return new Pair(robbed, skipped);
    }

    // Standard level-order tree builder: null entries mark a missing child, and
    // that position's subtree is simply not expanded further.
    private static TreeNode build(Integer[] values) {
        if (values.length == 0 || values[0] == null) {
            return null;
        }
        TreeNode root = new TreeNode(values[0]);
        Deque<TreeNode> queue = new ArrayDeque<TreeNode>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < values.length) {
            TreeNode current = queue.poll();
            if (i < values.length) {
                Integer leftVal = values[i++];
                if (leftVal != null) {
                    current.left = new TreeNode(leftVal);
                    queue.add(current.left);
                }
            }
            if (i < values.length) {
                Integer rightVal = values[i++];
                if (rightVal != null) {
                    current.right = new TreeNode(rightVal);
                    queue.add(current.right);
                }
            }
        }
        return root;
    }

    // Level-order array of a complete tree with n nodes, every value equal to val.
    private static Integer[] complete(int n, int val) {
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) {
            values[i] = val;
        }
        return values;
    }

    // Level-order array of a tree that is a single chain of n nodes, each node
    // the left child of the one before it, every value equal to val.
    private static Integer[] leftChain(int n, int val) {
        Integer[] values = new Integer[2 * n - 1];
        values[0] = val;
        for (int d = 1; d < n; d++) {
            values[2 * d - 1] = val;
            values[2 * d] = null;
        }
        return values;
    }

    private static void check(int caseNum, Integer[] values, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(build(values));
        if (got == expected) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new Integer[]{3, 2, 3, null, 3, null, 1}, 7, fail, total);
        check(2, new Integer[]{3, 4, 5, 1, 3, null, 1}, 9, fail, total);
        check(3, new Integer[]{}, 0, fail, total);
        check(4, new Integer[]{5}, 5, fail, total);
        check(5, new Integer[]{0, 0, 0}, 0, fail, total);
        check(6, new Integer[]{2, 1, 3}, 4, fail, total);
        check(7, new Integer[]{10, 1, 1}, 10, fail, total);
        check(8, new Integer[]{4, 1, null, 2, null, 3}, 7, fail, total);
        check(9, new Integer[]{3, null, 4, null, 3}, 6, fail, total);
        check(10, new Integer[]{2, 10, 1, 1, 1, 5, 5}, 20, fail, total);
        check(11, complete(15, 1), 10, fail, total);
        check(12, leftChain(1000, 1), 500, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n): `visit` is called once for every node and once for every missing child, and each call does constant work. Space O(h), where `h` is the height of the tree, for the recursion stack. That is about `log n` for a balanced tree and `n` for a chain. Each call also creates one small `Pair`, but it is dropped as soon as the parent has used it, so at most about `h` of them are alive at once.

## Java 8 pitfalls for this problem

- Recursion depth. A chain of `10^4` nodes means `10^4` nested calls. A left chain of 10000 nodes can overflow the default thread stack with this solution (it did in a cold test run), so the runner uses a chain of 1000, which fits with a wide margin. For the full limit, rewrite the traversal with an explicit stack, or run the work in a thread created with a larger stack size (`new Thread(null, task, "worker", stackBytes)`).
- Overflow. Here the total is at most `10^4 * 10^4 = 10^8`, so `int` is safe. If the values or the node count grow, the sums need `long`.
- Pair fields mixed up. The two numbers mean different things, and swapping them compiles and runs and gives wrong answers. Named fields (`robbed` and `skipped`) are safer than an `int[]` where index 0 and index 1 must be remembered.
- Shared default values. One `EMPTY` pair stands for every missing child. That is only safe because its fields are `final`. If the pair were mutable and a parent changed one, every empty subtree in the tree would change with it. A memo map kept in a `static` field has a similar problem, because entries from one tree survive into the next call.
- Level-order arrays. The children of position `p` are at `2 * p + 1` and `2 * p + 2` only in a complete tree. When missing children are marked with `null` and their own children are left out, those formulas land on the wrong entries, so the runner builds trees with a queue instead. The array for a chain of `n` nodes has `2 * n - 1` entries, because every missing child takes one `null` slot. Sizing it as `n` drops half the chain.
- Boxed values. The test inputs are `Integer[]` so that `null` can mean a missing child. Writing `int v = values[i]` on a `null` entry throws a `NullPointerException`, and comparing two `Integer` objects with `==` is wrong for values above 127. Check for `null` first, and compare the unboxed `int` values.
- The empty tree. `visit(null)` must return the empty pair. Without that base case, the very first call on a missing root throws a `NullPointerException`. In the brute-force version, reading `node.left.left` without checking that `node.left` exists fails the same way.
- `Math.max` takes exactly two arguments in Java 8, so the skipped value uses two separate calls, one per child.

## Wrong approaches and why they fail

1. **Add up the values at even depths, add up the values at odd depths, and return the larger sum.** This assumes the best set is always a whole level pattern. It breaks as soon as the best set needs a gap of two levels. Counterexample: `[4,1,null,2,null,3]`, the chain 4, 1, 2, 3. The even depths give `4 + 2 = 6` and the odd depths give `1 + 3 = 4`, so the method returns `6`. Taking the first and the last house gives `7`, which is the correct answer. On `[2,10,1,1,1,5,5]` it returns `14`, and the correct answer is `20`.
2. **Greedy: take the largest remaining house, remove its parent and children, repeat.** One large house in the middle can block two good ones. Counterexample: `[3,null,4,null,3]`, the chain 3, 4, 3. The method takes the 4, removes both 3s, and returns `4`. The correct answer is `6`.
3. **Return one number per node, `best(node) = max(node.val, best(left) + best(right))`.** Taking a node should still let its grandchildren contribute, but this formula counts either the node alone or its children, never the node plus the grandchildren. Counterexample: `[3,2,3,null,3,null,1]`. The leaf values are `best = 3` and `best = 1`, the node 2 gets `max(2, 3) = 3`, the right 3 gets `max(3, 1) = 3`, and the root gets `max(3, 3 + 3) = 6`. The correct answer is `7`.
4. **Return a pair, but compute `skipped = left.skipped + right.skipped`.** That quietly forces the children to be skipped as well, so a skipped node can never use its children. Counterexample: `[2,1,3]`. Both leaves are `(robbed, skipped) = (1, 0)` and `(3, 0)`. The root gets `robbed = 2 + 0 + 0 = 2` and `skipped = 0 + 0 = 0`, so the method returns `2`. The correct answer is `4`, from taking the two leaves.

## Variants

1. **Memoize the brute force.** Keep the recursion over children and grandchildren and store each node's answer in a map keyed by the node. The time is O(n) as well, but it needs O(n) extra space for the map, and the pair version needs none.
2. **Return the houses, not just the total.** Store each node's pair in a map, then walk down from the root. A taken node sends its children down as skipped, and a skipped node sends each child down as whichever of its two values is larger.
3. **House Robber (LC 198)** is the same choice on a path, see `house-robber.md`. **House Robber II (LC 213)** puts the path in a circle, see `house-robber-ii.md`.
4. **Iterative version.** Do a post-order traversal with an explicit stack and keep the two numbers per node in a map or in parallel arrays. This removes the recursion depth limit.
5. **Binary Tree Cameras (LC 968).** Another tree DP with a few states per node (has a camera, is covered, is not covered), combined from the children in the same post-order way.
6. **A tree where a node can have any number of children.** The same pair works: `robbed = val + sum of the children's skipped`, and `skipped = sum of the larger value of each child`.
7. **At most `k` houses may be taken.** Each node returns two arrays of length `k + 1` (best total for each count) instead of two numbers, and the children's arrays are merged by trying every split of the count.

## Test cases

| # | tree (level-order, null = missing child) | expected | what it tests |
|---|---|---|---|
| 1 | `[3,2,3,null,3,null,1]` | `7` | first example; root taken with two grandchildren (a single number per node gives 6) |
| 2 | `[3,4,5,1,3,null,1]` | `9` | second example; the two children of the root are taken |
| 3 | `[]` | `0` | empty tree, the base case |
| 4 | `[5]` | `5` | single node |
| 5 | `[0,0,0]` | `0` | zeros everywhere |
| 6 | `[2,1,3]` | `4` | children beat the parent (a skipped value that forces children skipped gives 2) |
| 7 | `[10,1,1]` | `10` | parent beats its children |
| 8 | `[4,1,null,2,null,3]` | `7` | chain going left; the best set is the two ends (depth parity gives 6) |
| 9 | `[3,null,4,null,3]` | `6` | chain going right; a large middle house blocks two (greedy gives 4) |
| 10 | `[2,10,1,1,1,5,5]` | `20` | one child taken, the other skipped for its own children (depth parity gives 14) |
| 11 | `[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]` (15 ones) | `10` | perfect tree; the leaves plus the root's two children |
| 12 | left chain of 1000 nodes, each worth 1 | `500` | deep recursion; every second node is taken |
