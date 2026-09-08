# Paper set 1

Read the prompt. Write the full Java on paper, Java 8 only. Open the hint only
after your version is complete. Verify by typing it in later.

## 1. Sliding window maximum
Array of ints and a window size k. Return the max of every window.

<details><summary>Hint</summary>

Deque of **indices**, values decreasing front to back. Front is the current
max. Before pushing i, pop the back while its value is smaller than nums[i].
Pop the front when its index leaves the window. O(n).
</details>

## 2. Largest rectangle in a histogram
Heights array. Return the largest rectangle area.

<details><summary>Hint</summary>

Stack of indices with increasing heights. When a bar is lower than the stack
top, pop and compute area with the popped height: width runs from the new
stack top plus one to the current index minus one. Append a sentinel 0 at the
end to flush the stack.
</details>

## 3. Subarray sum equals k, negatives allowed
Count subarrays whose sum is exactly k.

<details><summary>Hint</summary>

Prefix array has n+1 entries with P[0] = 0. Count pairs a < b with
P[b] - P[a] = k. Map from prefix value to count; at each b add
count[P[b] - k], then record P[b]. Query before insert.
</details>

## 4. Three numbers summing to zero, no duplicate triplets
Return all unique triplets.

<details><summary>Hint</summary>

Sort. For each i, two pointers on the remainder. Skip duplicate values at i,
at left after a match, and at right after a match.
</details>

## 5. Least-recently-used cache, no library map
`get` and `put` in O(1), fixed capacity.

<details><summary>Hint</summary>

HashMap from key to node, doubly linked list with two sentinels. Node stores
its own key so eviction can remove the map entry. `static` node class.
</details>
