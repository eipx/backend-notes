# Problem set

Thirty-eight algorithm and data-structure problems in six groups, each with a full study page, a standalone Java 8 solution that runs its own test cases, and a fill-in practice stub with the same tests.

Every `.md` page has the same sections: problem statement, constraints, worked examples, an edge-case checklist to enumerate before writing code, brute force to optimal with the key invariant, a step-by-step state trace, the Java 8 solution with comments, complexity, Java 8 pitfalls, wrong approaches with counterexamples, variants, and the test table.

## Run a solution

```
cd problems/<group>
javac --release 8 <ClassName>.java && java <ClassName>
```

Every runner prints PASS/FAIL per case and a final `n/n passed` line, and exits non-zero on any failure. The test rows in each page are identical to the cases in its runner.

## How to practice

Each group has a `practice/` folder with one file per problem. The file contains a `class Solution` stub with the method signature and an empty body, followed by the test runner. You write the body; nothing else changes.

```
cd problems/<group>/practice
javac --release 8 <ClassName>.java && java <ClassName>
```

The runner prints `PASS case n` or `FAIL case n: expected <x> got <y>` for every case and ends with `<passed>/<total> passed`. An untouched stub prints a low count (`0/N` or a few trivial passes where the expected answer is 0, null, or an empty list), which confirms the harness is wired before you start. Exceptions inside your code are caught per case and counted as failures, so one bug never hides the other cases.

Suggested loop for one problem:

1. Read the `## Problem`, `## Constraints`, and `## Edge cases checklist` sections of the page. Say the edge cases aloud.
2. Set a 25-minute timer. Write the body in `practice/<ClassName>.java`. Run it.
3. On a FAIL, read the failing case's "what it tests" column in the page's `## Test cases` table before touching the code.
4. When it passes, read `## Approach` and `## Java 8 pitfalls` and compare with the finished version one directory up.
5. Close the file and rewrite the body from a blank editor in five minutes. That second write is the one that sticks.

The finished solutions in the group folder are the reference, not the exercise. Open them after your own attempt or when the timer expires.

## Index

| Group | Problem | Ref | Difficulty | Pattern | Page | Runner | Practice | Tests |
|---|---|---|---|---|---|---|---|---|
| design-structures | Insert Delete GetRandom O(1) | LC 380 | Medium | array + index-map with swap-to-last deletion | [insert-delete-getrandom-o1.md](design-structures/insert-delete-getrandom-o1.md) | [InsertDeleteGetRandomO1.java](design-structures/InsertDeleteGetRandomO1.java) | [InsertDeleteGetRandomO1.java](design-structures/practice/InsertDeleteGetRandomO1.java) | 17 |
| design-structures | LFU Cache | LC 460 | Hard | hash map of frequency buckets (each an ordered set) + a running minimum frequency | [lfu-cache.md](design-structures/lfu-cache.md) | [LfuCache.java](design-structures/LfuCache.java) | [LfuCache.java](design-structures/practice/LfuCache.java) | 16 |
| design-structures | LRU Cache | LC 146 | Medium | hash map + doubly linked list (recency ordering) | [lru-cache.md](design-structures/lru-cache.md) | [LruCache.java](design-structures/LruCache.java) | [LruCache.java](design-structures/practice/LruCache.java) | 15 |
| design-structures | Min Stack | LC 155 | Medium | stack augmented with running-minimum tracking | [min-stack.md](design-structures/min-stack.md) | [MinStack.java](design-structures/MinStack.java) | [MinStack.java](design-structures/practice/MinStack.java) | 20 |
| design-structures | Time Based Key-Value Store | LC 981 | Medium | per-key sorted versions + floor lookup (TreeMap, or ArrayList + binary search) | [time-based-key-value-store.md](design-structures/time-based-key-value-store.md) | [TimeBasedKeyValueStore.java](design-structures/TimeBasedKeyValueStore.java) | [TimeBasedKeyValueStore.java](design-structures/practice/TimeBasedKeyValueStore.java) | 11 |
| design-structures | Underground System | LC 1396 | Medium | two hash maps (in-flight trips + running route totals) | [underground-system.md](design-structures/underground-system.md) | [UndergroundSystem.java](design-structures/UndergroundSystem.java) | [UndergroundSystem.java](design-structures/practice/UndergroundSystem.java) | 18 |
| graphs | Accounts Merge | LC 721 | Medium | Union-Find over string identifiers (emails), keyed by value not index | [accounts-merge.md](graphs/accounts-merge.md) | [AccountsMerge.java](graphs/AccountsMerge.java) | [AccountsMerge.java](graphs/practice/AccountsMerge.java) | 8 |
| graphs | Clone Graph | LC 133 | Medium | Graph traversal with an old-to-new node map | [clone-graph.md](graphs/clone-graph.md) | [CloneGraph.java](graphs/CloneGraph.java) | [CloneGraph.java](graphs/practice/CloneGraph.java) | 16 |
| graphs | Course Schedule II | LC 210 | Medium | Topological sort (Kahn's algorithm, indegree-driven BFS) | [course-schedule-ii.md](graphs/course-schedule-ii.md) | [CourseScheduleII.java](graphs/CourseScheduleII.java) | [CourseScheduleII.java](graphs/practice/CourseScheduleII.java) | 14 |
| graphs | Max Area of Island | LC 695 | Medium | Grid flood fill with in-place marking | [max-area-of-island.md](graphs/max-area-of-island.md) | [MaxAreaOfIsland.java](graphs/MaxAreaOfIsland.java) | [MaxAreaOfIsland.java](graphs/practice/MaxAreaOfIsland.java) | 10 |
| graphs | Number of Provinces | LC 547 | Medium | Union-Find (disjoint set) with path compression and union by size | [number-of-provinces.md](graphs/number-of-provinces.md) | [NumberOfProvinces.java](graphs/NumberOfProvinces.java) | [NumberOfProvinces.java](graphs/practice/NumberOfProvinces.java) | 10 |
| graphs | Redundant Connection | LC 684 | Medium | Union-Find, first edge that closes a cycle | [redundant-connection.md](graphs/redundant-connection.md) | [RedundantConnection.java](graphs/RedundantConnection.java) | [RedundantConnection.java](graphs/practice/RedundantConnection.java) | 8 |
| graphs | Shortest Path in Binary Matrix | LC 1091 | Medium | 8-direction BFS with level counting | [shortest-path-in-binary-matrix.md](graphs/shortest-path-in-binary-matrix.md) | [ShortestPathInBinaryMatrix.java](graphs/ShortestPathInBinaryMatrix.java) | [ShortestPathInBinaryMatrix.java](graphs/practice/ShortestPathInBinaryMatrix.java) | 13 |
| graphs | Word Ladder | LC 127 | Hard | BFS over an implicit word graph, generic-pattern buckets | [word-ladder.md](graphs/word-ladder.md) | [WordLadder.java](graphs/WordLadder.java) | [WordLadder.java](graphs/practice/WordLadder.java) | 15 |
| linked-lists | Copy List with Random Pointer | LC 138 | Medium | weave-and-split interleaving to reach O(1) extra space, with a HashMap of old-to-new nodes as the simple baseline | [copy-list-with-random-pointer.md](linked-lists/copy-list-with-random-pointer.md) | [CopyListWithRandomPointer.java](linked-lists/CopyListWithRandomPointer.java) | [CopyListWithRandomPointer.java](linked-lists/practice/CopyListWithRandomPointer.java) | 10 |
| linked-lists | Linked List Cycle II | LC 142 | Medium | Floyd's tortoise and hare, then a second lockstep walk from the head to locate the entry | [linked-list-cycle-ii.md](linked-lists/linked-list-cycle-ii.md) | [LinkedListCycleII.java](linked-lists/LinkedListCycleII.java) | [LinkedListCycleII.java](linked-lists/practice/LinkedListCycleII.java) | 10 |
| linked-lists | Merge k Sorted Lists | LC 23 | Hard | min-heap over the current heads of k sorted lists, with pairwise divide-and-conquer as the heap-free alternative | [merge-k-sorted-lists.md](linked-lists/merge-k-sorted-lists.md) | [MergeKSortedLists.java](linked-lists/MergeKSortedLists.java) | [MergeKSortedLists.java](linked-lists/practice/MergeKSortedLists.java) | 19 |
| linked-lists | Remove Nth Node From End of List | LC 19 | Medium | two pointers with a fixed gap of n, dummy head to erase the head-removal special case | [remove-nth-node-from-end-of-list.md](linked-lists/remove-nth-node-from-end-of-list.md) | [RemoveNthNodeFromEndOfList.java](linked-lists/RemoveNthNodeFromEndOfList.java) | [RemoveNthNodeFromEndOfList.java](linked-lists/practice/RemoveNthNodeFromEndOfList.java) | 10 |
| linked-lists | Reverse Nodes in k-Group | LC 25 | Hard | in-place group reversal with a dummy head, leaving a short trailing group untouched | [reverse-nodes-in-k-group.md](linked-lists/reverse-nodes-in-k-group.md) | [ReverseNodesInKGroup.java](linked-lists/ReverseNodesInKGroup.java) | [ReverseNodesInKGroup.java](linked-lists/practice/ReverseNodesInKGroup.java) | 13 |
| linked-lists | Sort List | LC 148 | Medium | bottom-up merge sort with iterative width-doubling for O(1) extra space, top-down recursive slow/fast split as the simpler baseline | [sort-list.md](linked-lists/sort-list.md) | [SortList.java](linked-lists/SortList.java) | [SortList.java](linked-lists/practice/SortList.java) | 12 |
| prefix-stack-heap | Daily Temperatures | LC 739 | Medium | Monotonic stack | [daily-temperatures.md](prefix-stack-heap/daily-temperatures.md) | [DailyTemperatures.java](prefix-stack-heap/DailyTemperatures.java) | [DailyTemperatures.java](prefix-stack-heap/practice/DailyTemperatures.java) | 16 |
| prefix-stack-heap | Kth Largest Element | LC 215 | Medium | Min-heap of size k | [kth-largest-element.md](prefix-stack-heap/kth-largest-element.md) | [KthLargestElement.java](prefix-stack-heap/KthLargestElement.java) | [KthLargestElement.java](prefix-stack-heap/practice/KthLargestElement.java) | 16 |
| prefix-stack-heap | Largest Rectangle in Histogram | LC 84 | Hard | Monotonic stack with sentinel | [largest-rectangle-in-histogram.md](prefix-stack-heap/largest-rectangle-in-histogram.md) | [LargestRectangleInHistogram.java](prefix-stack-heap/LargestRectangleInHistogram.java) | [LargestRectangleInHistogram.java](prefix-stack-heap/practice/LargestRectangleInHistogram.java) | 16 |
| prefix-stack-heap | Subarray Sum Equals K | LC 560 | Medium | Prefix sum + frequency map | [subarray-sum-equals-k.md](prefix-stack-heap/subarray-sum-equals-k.md) | [SubarraySumEqualsK.java](prefix-stack-heap/SubarraySumEqualsK.java) | [SubarraySumEqualsK.java](prefix-stack-heap/practice/SubarraySumEqualsK.java) | 14 |
| prefix-stack-heap | Task Scheduler | LC 621 | Medium | Greedy counting (heap-simulation variant) | [task-scheduler.md](prefix-stack-heap/task-scheduler.md) | [TaskScheduler.java](prefix-stack-heap/TaskScheduler.java) | [TaskScheduler.java](prefix-stack-heap/practice/TaskScheduler.java) | 16 |
| prefix-stack-heap | Top K Frequent Elements | LC 347 | Medium | Frequency map + heap | [top-k-frequent-elements.md](prefix-stack-heap/top-k-frequent-elements.md) | [TopKFrequentElements.java](prefix-stack-heap/TopKFrequentElements.java) | [TopKFrequentElements.java](prefix-stack-heap/practice/TopKFrequentElements.java) | 13 |
| search-graph-intervals | Capacity To Ship Packages Within D Days | LC 1011 | Medium | Binary search on the answer | [capacity-to-ship-packages-within-d-days.md](search-graph-intervals/capacity-to-ship-packages-within-d-days.md) | [CapacityToShipPackagesWithinDDays.java](search-graph-intervals/CapacityToShipPackagesWithinDDays.java) | [CapacityToShipPackagesWithinDDays.java](search-graph-intervals/practice/CapacityToShipPackagesWithinDDays.java) | 14 |
| search-graph-intervals | Course Schedule | LC 207 | Medium | Topological sort (Kahn's algorithm) / DFS 3-color cycle detection | [course-schedule.md](search-graph-intervals/course-schedule.md) | [CourseSchedule.java](search-graph-intervals/CourseSchedule.java) | [CourseSchedule.java](search-graph-intervals/practice/CourseSchedule.java) | 14 |
| search-graph-intervals | Koko Eating Bananas | LC 875 | Medium | Binary search on the answer | [koko-eating-bananas.md](search-graph-intervals/koko-eating-bananas.md) | [KokoEatingBananas.java](search-graph-intervals/KokoEatingBananas.java) | [KokoEatingBananas.java](search-graph-intervals/practice/KokoEatingBananas.java) | 14 |
| search-graph-intervals | Merge Intervals | LC 56 | Medium | Sort by start, sweep and merge | [merge-intervals.md](search-graph-intervals/merge-intervals.md) | [MergeIntervals.java](search-graph-intervals/MergeIntervals.java) | [MergeIntervals.java](search-graph-intervals/practice/MergeIntervals.java) | 13 |
| search-graph-intervals | Number of Islands | LC 200 | Medium | Grid DFS/BFS (connected components) | [number-of-islands.md](search-graph-intervals/number-of-islands.md) | [NumberOfIslands.java](search-graph-intervals/NumberOfIslands.java) | [NumberOfIslands.java](search-graph-intervals/practice/NumberOfIslands.java) | 12 |
| search-graph-intervals | Rotting Oranges | LC 994 | Medium | Multi-source BFS with level snapshot | [rotting-oranges.md](search-graph-intervals/rotting-oranges.md) | [RottingOranges.java](search-graph-intervals/RottingOranges.java) | [RottingOranges.java](search-graph-intervals/practice/RottingOranges.java) | 14 |
| windows-and-pointers | Container With Most Water | LC 11 | Medium | two pointers converging from both ends, greedy elimination | [container-with-most-water.md](windows-and-pointers/container-with-most-water.md) | [ContainerWithMostWater.java](windows-and-pointers/ContainerWithMostWater.java) | [ContainerWithMostWater.java](windows-and-pointers/practice/ContainerWithMostWater.java) | 19 |
| windows-and-pointers | Longest Repeating Character Replacement | LC 424 | Medium | variable-size sliding window with a frequency count and a "best-so-far" ceiling | [longest-repeating-character-replacement.md](windows-and-pointers/longest-repeating-character-replacement.md) | [LongestRepeatingCharacterReplacement.java](windows-and-pointers/LongestRepeatingCharacterReplacement.java) | [LongestRepeatingCharacterReplacement.java](windows-and-pointers/practice/LongestRepeatingCharacterReplacement.java) | 17 |
| windows-and-pointers | Longest Substring Without Repeating Characters | LC 3 | Medium | variable-size sliding window with a last-seen index map | [longest-substring-without-repeating-characters.md](windows-and-pointers/longest-substring-without-repeating-characters.md) | [LongestSubstringWithoutRepeatingCharacters.java](windows-and-pointers/LongestSubstringWithoutRepeatingCharacters.java) | [LongestSubstringWithoutRepeatingCharacters.java](windows-and-pointers/practice/LongestSubstringWithoutRepeatingCharacters.java) | 18 |
| windows-and-pointers | Sliding Window Maximum | LC 239 | Hard | fixed-size sliding window with a monotonic deque | [sliding-window-maximum.md](windows-and-pointers/sliding-window-maximum.md) | [SlidingWindowMaximum.java](windows-and-pointers/SlidingWindowMaximum.java) | [SlidingWindowMaximum.java](windows-and-pointers/practice/SlidingWindowMaximum.java) | 18 |
| windows-and-pointers | 3Sum | LC 15 | Medium | sort + fixed anchor + two pointers, with explicit duplicate skipping | [three-sum.md](windows-and-pointers/three-sum.md) | [ThreeSum.java](windows-and-pointers/ThreeSum.java) | [ThreeSum.java](windows-and-pointers/practice/ThreeSum.java) | 10 |
| windows-and-pointers | Trapping Rain Water | LC 42 | Hard | two pointers converging from both ends, tracking running left/right maxima | [trapping-rain-water.md](windows-and-pointers/trapping-rain-water.md) | [TrappingRainWater.java](windows-and-pointers/TrappingRainWater.java) | [TrappingRainWater.java](windows-and-pointers/practice/TrappingRainWater.java) | 22 |
