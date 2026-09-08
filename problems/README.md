# Problem set

Twenty-four algorithm and data-structure problems, each with a full study page and a standalone Java 8 solution that runs its own test cases.

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

The runner prints `PASS case n` or `FAIL case n: expected <x> got <y>` for every case and ends with `<passed>/<total> passed`. An untouched stub prints a low count (`0/N` or a few trivial passes where the expected answer is 0 or an empty list), which confirms the harness is wired before you start. Exceptions inside your code are caught per case and counted as failures, so one bug never hides the other cases.

Suggested loop for one problem:

1. Read the `## Problem`, `## Constraints`, and `## Edge cases checklist` sections of the page. Say the edge cases aloud.
2. Set a 25-minute timer. Write the body in `practice/<ClassName>.java`. Run it.
3. On a FAIL, read the failing case's "what it tests" column in the page's `## Test cases` table before touching the code.
4. When it passes, read `## Approach` and `## Java 8 pitfalls` and compare with the finished version one directory up.
5. Close the file and rewrite the body from a blank editor in five minutes. That second write is the one that sticks.

The finished solutions in the group folder are the reference, not the exercise. Open them after your own attempt or when the timer expires.

## Index

| Group | Problem | Ref | Difficulty | Pattern | Page | Runner | Tests | Practice |
|---|---|---|---|---|---|---|---|---|
| design-structures | Insert Delete GetRandom O(1) | LC 380 | Medium | array + index-map with swap-to-last deletion | [insert-delete-getrandom-o1.md](design-structures/insert-delete-getrandom-o1.md) | [InsertDeleteGetRandomO1.java](design-structures/InsertDeleteGetRandomO1.java) | 17 | [InsertDeleteGetRandomO1.java](design-structures/practice/InsertDeleteGetRandomO1.java) |
| design-structures | LFU Cache | LC 460 | Hard | hash map of frequency buckets (each an ordered set) + a running minimum frequency | [lfu-cache.md](design-structures/lfu-cache.md) | [LfuCache.java](design-structures/LfuCache.java) | 16 | [LfuCache.java](design-structures/practice/LfuCache.java) |
| design-structures | LRU Cache | LC 146 | Medium | hash map + doubly linked list (recency ordering) | [lru-cache.md](design-structures/lru-cache.md) | [LruCache.java](design-structures/LruCache.java) | 15 | [LruCache.java](design-structures/practice/LruCache.java) |
| design-structures | Min Stack | LC 155 | Medium | stack augmented with running-minimum tracking | [min-stack.md](design-structures/min-stack.md) | [MinStack.java](design-structures/MinStack.java) | 20 | [MinStack.java](design-structures/practice/MinStack.java) |
| design-structures | Time Based Key-Value Store | LC 981 | Medium | per-key sorted versions + floor lookup (TreeMap, or ArrayList + binary search) | [time-based-key-value-store.md](design-structures/time-based-key-value-store.md) | [TimeBasedKeyValueStore.java](design-structures/TimeBasedKeyValueStore.java) | 11 | [TimeBasedKeyValueStore.java](design-structures/practice/TimeBasedKeyValueStore.java) |
| design-structures | Underground System | LC 1396 | Medium | two hash maps (in-flight trips + running route totals) | [underground-system.md](design-structures/underground-system.md) | [UndergroundSystem.java](design-structures/UndergroundSystem.java) | 18 | [UndergroundSystem.java](design-structures/practice/UndergroundSystem.java) |
| prefix-stack-heap | Daily Temperatures | LC 739 | Medium | Monotonic stack | [daily-temperatures.md](prefix-stack-heap/daily-temperatures.md) | [DailyTemperatures.java](prefix-stack-heap/DailyTemperatures.java) | 16 | [DailyTemperatures.java](prefix-stack-heap/practice/DailyTemperatures.java) |
| prefix-stack-heap | Kth Largest Element | LC 215 | Medium | Min-heap of size k | [kth-largest-element.md](prefix-stack-heap/kth-largest-element.md) | [KthLargestElement.java](prefix-stack-heap/KthLargestElement.java) | 16 | [KthLargestElement.java](prefix-stack-heap/practice/KthLargestElement.java) |
| prefix-stack-heap | Largest Rectangle in Histogram | LC 84 | Hard | Monotonic stack with sentinel | [largest-rectangle-in-histogram.md](prefix-stack-heap/largest-rectangle-in-histogram.md) | [LargestRectangleInHistogram.java](prefix-stack-heap/LargestRectangleInHistogram.java) | 16 | [LargestRectangleInHistogram.java](prefix-stack-heap/practice/LargestRectangleInHistogram.java) |
| prefix-stack-heap | Subarray Sum Equals K | LC 560 | Medium | Prefix sum + frequency map | [subarray-sum-equals-k.md](prefix-stack-heap/subarray-sum-equals-k.md) | [SubarraySumEqualsK.java](prefix-stack-heap/SubarraySumEqualsK.java) | 14 | [SubarraySumEqualsK.java](prefix-stack-heap/practice/SubarraySumEqualsK.java) |
| prefix-stack-heap | Task Scheduler | LC 621 | Medium | Greedy counting (heap-simulation variant) | [task-scheduler.md](prefix-stack-heap/task-scheduler.md) | [TaskScheduler.java](prefix-stack-heap/TaskScheduler.java) | 16 | [TaskScheduler.java](prefix-stack-heap/practice/TaskScheduler.java) |
| prefix-stack-heap | Top K Frequent Elements | LC 347 | Medium | Frequency map + heap | [top-k-frequent-elements.md](prefix-stack-heap/top-k-frequent-elements.md) | [TopKFrequentElements.java](prefix-stack-heap/TopKFrequentElements.java) | 13 | [TopKFrequentElements.java](prefix-stack-heap/practice/TopKFrequentElements.java) |
| search-graph-intervals | Capacity To Ship Packages Within D Days | LC 1011 | Medium | Binary search on the answer | [capacity-to-ship-packages-within-d-days.md](search-graph-intervals/capacity-to-ship-packages-within-d-days.md) | [CapacityToShipPackagesWithinDDays.java](search-graph-intervals/CapacityToShipPackagesWithinDDays.java) | 14 | [CapacityToShipPackagesWithinDDays.java](search-graph-intervals/practice/CapacityToShipPackagesWithinDDays.java) |
| search-graph-intervals | Course Schedule | LC 207 | Medium | Topological sort (Kahn's algorithm) / DFS 3-color cycle detection | [course-schedule.md](search-graph-intervals/course-schedule.md) | [CourseSchedule.java](search-graph-intervals/CourseSchedule.java) | 14 | [CourseSchedule.java](search-graph-intervals/practice/CourseSchedule.java) |
| search-graph-intervals | Koko Eating Bananas | LC 875 | Medium | Binary search on the answer | [koko-eating-bananas.md](search-graph-intervals/koko-eating-bananas.md) | [KokoEatingBananas.java](search-graph-intervals/KokoEatingBananas.java) | 14 | [KokoEatingBananas.java](search-graph-intervals/practice/KokoEatingBananas.java) |
| search-graph-intervals | Merge Intervals | LC 56 | Medium | Sort by start, sweep and merge | [merge-intervals.md](search-graph-intervals/merge-intervals.md) | [MergeIntervals.java](search-graph-intervals/MergeIntervals.java) | 13 | [MergeIntervals.java](search-graph-intervals/practice/MergeIntervals.java) |
| search-graph-intervals | Number of Islands | LC 200 | Medium | Grid DFS/BFS (connected components) | [number-of-islands.md](search-graph-intervals/number-of-islands.md) | [NumberOfIslands.java](search-graph-intervals/NumberOfIslands.java) | 12 | [NumberOfIslands.java](search-graph-intervals/practice/NumberOfIslands.java) |
| search-graph-intervals | Rotting Oranges | LC 994 | Medium | Multi-source BFS with level snapshot | [rotting-oranges.md](search-graph-intervals/rotting-oranges.md) | [RottingOranges.java](search-graph-intervals/RottingOranges.java) | 14 | [RottingOranges.java](search-graph-intervals/practice/RottingOranges.java) |
| windows-and-pointers | Container With Most Water | LC 11 | Medium | two pointers converging from both ends, greedy elimination | [container-with-most-water.md](windows-and-pointers/container-with-most-water.md) | [ContainerWithMostWater.java](windows-and-pointers/ContainerWithMostWater.java) | 19 | [ContainerWithMostWater.java](windows-and-pointers/practice/ContainerWithMostWater.java) |
| windows-and-pointers | Longest Repeating Character Replacement | LC 424 | Medium | variable-size sliding window with a frequency count and a "best-so-far" ceiling | [longest-repeating-character-replacement.md](windows-and-pointers/longest-repeating-character-replacement.md) | [LongestRepeatingCharacterReplacement.java](windows-and-pointers/LongestRepeatingCharacterReplacement.java) | 17 | [LongestRepeatingCharacterReplacement.java](windows-and-pointers/practice/LongestRepeatingCharacterReplacement.java) |
| windows-and-pointers | Longest Substring Without Repeating Characters | LC 3 | Medium | variable-size sliding window with a last-seen index map | [longest-substring-without-repeating-characters.md](windows-and-pointers/longest-substring-without-repeating-characters.md) | [LongestSubstringWithoutRepeatingCharacters.java](windows-and-pointers/LongestSubstringWithoutRepeatingCharacters.java) | 18 | [LongestSubstringWithoutRepeatingCharacters.java](windows-and-pointers/practice/LongestSubstringWithoutRepeatingCharacters.java) |
| windows-and-pointers | Sliding Window Maximum | LC 239 | Hard | fixed-size sliding window with a monotonic deque | [sliding-window-maximum.md](windows-and-pointers/sliding-window-maximum.md) | [SlidingWindowMaximum.java](windows-and-pointers/SlidingWindowMaximum.java) | 18 | [SlidingWindowMaximum.java](windows-and-pointers/practice/SlidingWindowMaximum.java) |
| windows-and-pointers | 3Sum | LC 15 | Medium | sort + fixed anchor + two pointers, with explicit duplicate skipping | [three-sum.md](windows-and-pointers/three-sum.md) | [ThreeSum.java](windows-and-pointers/ThreeSum.java) | 10 | [ThreeSum.java](windows-and-pointers/practice/ThreeSum.java) |
| windows-and-pointers | Trapping Rain Water | LC 42 | Hard | two pointers converging from both ends, tracking running left/right maxima | [trapping-rain-water.md](windows-and-pointers/trapping-rain-water.md) | [TrappingRainWater.java](windows-and-pointers/TrappingRainWater.java) | 22 | [TrappingRainWater.java](windows-and-pointers/practice/TrappingRainWater.java) |
