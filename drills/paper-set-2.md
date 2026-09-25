# Paper set 2

Read the prompt. Say the time and space complexity before the first line.
Write the full Java on paper, Java 8 only. Trace the smallest input in a
comment before calling it done. Open the hint only after your version is
complete. Verify by typing it in later against the page's runner.

## 1. K closest elements in a sorted array
Sorted int array, ints k and x. Return the k values closest to x, ascending.
Closer means smaller absolute difference; on a tie the smaller value wins.

<details><summary>Hint</summary>

The answer is a contiguous block of length k. Binary search its left edge
lo in [0, n-k]: compare x - arr[mid] with arr[mid+k] - x. If the left
distance is strictly larger, lo = mid + 1, else hi = mid. Strict, so ties
keep the smaller values. O(log(n-k) + k). Two pointers from both ends,
dropping the farther end each step, is the O(n-k) baseline.

Page: [find-k-closest-elements.md](../problems/search-graph-intervals/find-k-closest-elements.md)
</details>

## 2. Houses in a circle
Money in each house. Adjacent houses cannot both be taken, and the first
and last houses are adjacent. Maximum total.

<details><summary>Hint</summary>

One house alone: take it. Otherwise the first and last cannot both be in
the answer, so run the straight-line version twice, once without the first
house and once without the last, and take the larger. Straight line: two
rolling variables, cur = max(prev1, prev2 + nums[i]). O(n), O(1).

Page: [house-robber-ii.md](../problems/dynamic-programming/house-robber-ii.md)
</details>

## Read once, then close

Pages, in this order. Read the approach and the wrong-approaches sections;
skip the code.

1. [Network Delay Time](../problems/graphs/network-delay-time.md)
2. [Evaluate Division](../problems/graphs/evaluate-division.md)
3. [Reverse Nodes in k-Group](../problems/linked-lists/reverse-nodes-in-k-group.md)
4. [LFU Cache](../problems/design-structures/lfu-cache.md)
5. [Largest Rectangle in Histogram](../problems/prefix-stack-heap/largest-rectangle-in-histogram.md)

Then the wrong-approaches section of
[Maximum Points You Can Obtain from Cards](../problems/windows-and-pointers/maximum-points-you-can-obtain-from-cards.md):
the two-dimensional table, the three places it broke, and why the max
inside the cell disappeared.
