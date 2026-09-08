# Task Scheduler
`ref: LC 621` · Difficulty: Medium · Pattern: Greedy counting (heap-simulation variant)

## Problem
You are given a list of task labels (each a single character, so identical characters mean identical task types) and a non-negative integer `n`, the required cooldown: after running one task of a given type, you must wait at least `n` full intervals before running another task of that same type again. In any single interval you either run exactly one task or sit idle. Tasks can be reordered freely and run in any sequence you like, as long as the cooldown rule is respected. Return the minimum total number of intervals (including any idle ones) needed to get through every task in the list.

## Constraints
- The number of tasks can range from empty up to on the order of thousands.
- Task labels are drawn from a small fixed alphabet (26 possible types), so at most 26 distinct counts ever need to be tracked.
- The cooldown `n` is a small non-negative integer, typically bounded well under 100.
- Implication: because the alphabet is fixed at 26 letters, counting frequencies is O(m) in the number of tasks and everything after that operates on at most 26 numbers — the interesting complexity question isn't about scaling to large inputs, it's about finding a closed-form answer instead of simulating interval by interval.

## Worked examples
1. `tasks = [A,A,A,B,B,B]`, `n = 2` -> `8`. Both `A` and `B` occur 3 times, more than anything else. One valid schedule is `A,B,idle,A,B,idle,A,B` — 8 intervals total, with two idle slots needed because there's nothing else to fill the cooldown gaps.
2. `tasks = [A,A,A,B,B,B]`, `n = 0` -> `6`. With no cooldown requirement at all, the tasks can simply run back to back in any order: 6 tasks, 6 intervals, no idling ever needed.
3. `tasks = [A]`, `n = 5` -> `1`. A single task with no repeats needs no cooldown at all — it just runs once.
4. `tasks = [A,A,A,B,B,C]`, `n = 2` -> `7`. `A` is the most frequent at 3 occurrences and nothing else ties it. One valid schedule is `A,B,C,A,B,idle,A` — 7 intervals, with exactly one idle slot needed near the end because `C` and the second `B` have already been used up by the time the third `A` becomes available again.

## Edge cases checklist
- Empty task list (answer is `0`).
- `n = 0` (no cooldown at all; answer is always just the total task count).
- A single task type repeated many times with a large `n` (idle time dominates the schedule).
- All task types distinct, each occurring exactly once (cooldown never actually forces any idling).
- Multiple task types tied for the highest frequency (the tie count itself affects the answer, not just the frequency value).
- Enough distinct "filler" task types available to completely absorb every cooldown gap, so idle time is never needed even though `n > 0`.
- Very large `n` relative to how many distinct task types exist (idle time is unavoidable and dominates).

## Approach

### Brute force
Simulate the schedule one interval at a time using a max-heap of remaining counts per task type: at each interval, if the heap is non-empty, run the task type with the highest remaining count (breaking ties arbitrarily), decrement its count, and if it's not yet exhausted, hold it in a side "cooling down" queue tagged with the interval at which it becomes eligible again (current interval + `n` + 1); at each interval also check the cooling-down queue for any task type that has become eligible again and push it back onto the heap. If the heap is empty but the cooling-down queue is not, the interval is idle. Repeat until both the heap and the queue are empty, counting intervals as you go. This is correct and, since the alphabet is fixed at 26 letters, actually runs comfortably fast in practice (each interval does at most O(log 26) heap work). It is presented here as the "brute force" because it does much more bookkeeping than necessary — explicitly simulating every single interval — when a direct formula, described next, gets the same answer without any simulation at all.

### Optimal
Count how many times each task type appears. Let `maxFreq` be the highest count among all task types, and let `maxFreqTaskCount` be how many distinct task types share that highest count. Imagine arranging the schedule as a series of frames, each `n + 1` intervals wide, where every frame starts with one occurrence of a most-frequent task type: this uses up `maxFreq - 1` full frames (since the last occurrence of the most-frequent type doesn't need a full trailing gap after it), plus a final partial frame that holds exactly the `maxFreqTaskCount` tied most-frequent types packed together at the front. That gives a frame-based length of `(maxFreq - 1) * (n + 1) + maxFreqTaskCount`. The true answer is the larger of that frame-based length and the plain total task count, since if there are enough other distinct task types to fill every gap in those frames, no idling is ever needed and the answer is simply however many tasks there are.

**Key invariant:** arranging the schedule so that every occurrence of a most-frequent task type anchors the start of its own `(n + 1)`-wide frame is always at least as good as any other arrangement, so the frame-based length is a valid lower bound on the answer, and it is also always achievable.

Proof sketch: any occurrence of a most-frequent task type after its first must be separated from the previous occurrence by at least `n` other intervals, so the `maxFreq` occurrences of that type alone force at least `(maxFreq - 1) * (n + 1) + 1` intervals just to place that one type, before considering anything else — this gives the lower-bound half of the argument. For the achievability half: place one occurrence of every tied most-frequent type at the start of each frame (this is always possible, since none of them can conflict with each other within the same frame), and fill the remaining slots in each frame with whatever other task types still have occurrences left, in any order, since none of those less-frequent types can possibly need more room than a most-frequent type already gets. If there are ever more leftover tasks than there are gaps to fill, that simply means the true bottleneck is the total task count rather than the frame structure, which is exactly why the answer takes the maximum of the two quantities rather than the frame-based formula alone.

### Step-by-step trace
Trace the formula on `tasks = [A,A,A,B,B,B]`, `n = 2`:

| Step | value computed | result |
|---|---|---|
| 1 | counts | `A: 3, B: 3` |
| 2 | maxFreq | `3` |
| 3 | maxFreqTaskCount (how many types have count 3) | `2` (both A and B) |
| 4 | frame-based length: `(maxFreq - 1) * (n + 1) + maxFreqTaskCount` | `(3-1)*(2+1) + 2 = 6 + 2 = 8` |
| 5 | total task count | `6` |
| 6 | answer: `max(frame-based length, total task count)` | `max(8, 6) = 8` |

This matches worked example 1, and corresponds to the schedule `A,B,idle,A,B,idle,A,B`.

## Java 8 solution
```java
public class TaskScheduler {

    // Returns the minimum number of intervals (including idle slots) to run all tasks,
    // given that identical task types must be separated by at least n intervals.
    public static int solve(char[] tasks, int n) {
        if (tasks.length == 0) {
            return 0;
        }
        int[] counts = new int[26];
        for (char c : tasks) {
            counts[c - 'A']++;
        }

        int maxFreq = 0;
        for (int c : counts) {
            if (c > maxFreq) {
                maxFreq = c;
            }
        }

        int maxFreqTaskCount = 0;
        for (int c : counts) {
            if (c == maxFreq) {
                maxFreqTaskCount++;
            }
        }

        // Frame the schedule around (maxFreq - 1) full gaps of size (n + 1), with the
        // last, partial frame holding just the tied most-frequent task types.
        int framedLength = (maxFreq - 1) * (n + 1) + maxFreqTaskCount;

        // If there are enough other distinct tasks to fill every gap, no idling is
        // needed at all, and the answer is simply the total task count.
        return Math.max(tasks.length, framedLength);
    }
}
```

## Complexity
Time O(m + 26): O(m) to count occurrences across the `m` input tasks, plus two O(26) passes over the fixed-size count array to find `maxFreq` and `maxFreqTaskCount` — effectively O(m) overall since the alphabet size is a constant. Space O(1): the count array is always exactly 26 entries, regardless of input size.

## Java 8 pitfalls for this problem
- `counts[c - 'A']` relies on task labels being uppercase letters `A`-`Z`; if the actual character set is wider (lowercase, digits, symbols), the fixed 26-slot array needs to be replaced with a `Map<Character, Integer>` or a larger fixed array — this is a boundary assumption baked into the constraints, not something the code defends against.
- Using `Map<Character,Integer>` for counting instead of a plain `int[26]` array is not wrong, but comparing boxed `Integer` counts with `==` while finding `maxFreq` would fall into the usual boxed-integer cache trap — the array-based approach here sidesteps it entirely by working in primitive `int`.
- The formula deliberately does not use a heap at all in its final form, but if it's implemented via the brute-force heap simulation instead, remember `PriorityQueue` is a **min-heap** by default — a max-heap simulation needs `new PriorityQueue<int[]>(Collections.reverseOrder(...))` or an explicit comparator that reverses the natural count ordering.
- `(maxFreq - 1) * (n + 1) + maxFreqTaskCount` can overflow `int` only for unrealistically large `maxFreq` and `n` simultaneously; at the bounds implied by a fixed 26-letter alphabet and a small cooldown, this is not a practical concern, but it's the kind of multiplication worth double-checking whenever a similar formula appears with looser bounds elsewhere.
- Declaring `counts` as `int[]` rather than `Integer[]` avoids boxing entirely for this whole solution — there's no reason to introduce boxed integers anywhere in the counting step here.

## Wrong approaches and why they fail
1. **Just count total tasks and add idle slots for the most frequent type, ignoring how many other types tie for that maximum.** Counterexample: `tasks = [A,A,A,B,B,B]`, `n = 2` — treating only `A` as "the" most frequent and computing `(3-1)*(2+1)+1 = 7` misses that `B` is tied with `A` and also needs its own slot in the final partial frame, undercounting the true answer of `8`.
2. **Always answer with the total task count, assuming there are always enough other tasks to fill every cooldown gap.** Counterexample: `tasks = [A,A,A,A]`, `n = 3` — there is only one task type, so every gap between the four `A`s must be filled with idle time; the correct answer is `13`, far more than the total task count of `4`.
3. **Always answer with the frame-based formula alone, without taking the max against the total task count.** Counterexample: `tasks = [A,B,C,D]`, `n = 2` — every type has count 1, so `maxFreq = 1` and the formula gives `(1-1)*(2+1) + 4 = 4`, which happens to be correct here, but the formula alone is only safe because it's combined with the max — for inputs with many distinct low-frequency types and a small `maxFreq`, skipping the `max` against the plain task count is what actually makes the formula correct in general; relying on the formula's numeric output without understanding why the `max` matters is the trap, since it's easy to mistakenly drop the `max` when refactoring and only notice the bug on inputs where padding would otherwise be needed.

## Variants
1. **Return one valid schedule (the actual sequence of task labels and idle slots), not just its length.** This requires the heap-based simulation described under Brute force, since the closed-form formula only produces a count, not a concrete arrangement — run the max-heap-plus-cooldown-queue simulation and record which label (or idle marker) is chosen at each interval.
2. **Each task type has its own distinct cooldown value instead of one shared `n`.** The frame-based formula no longer applies directly, since it assumes a single uniform cooldown; this pushes the problem back toward the heap simulation, where each task type's cooldown queue entry uses its own type-specific cooldown length.
3. **Minimize idle time instead of total schedule length.** Since total length is always `tasks.length + idleCount`, and this problem already computes the minimum total length, idle time is simply `answer - tasks.length` (which is `0` whenever the total task count was already the binding constraint).

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `tasks=[A,A,A,B,B,B]`, `n=2` | `8` | two-way tie for max frequency, cooldown forces idling |
| 2 | `tasks=[A,A,A,B,B,B]`, `n=0` | `6` | no cooldown at all |
| 3 | `tasks=[A,A,A,A,A,A,B,C,D,E,F,G]`, `n=2` | `16` | single dominant type, unique max, several fillers |
| 4 | `tasks=[A]`, `n=5` | `1` | single task, no repeats |
| 5 | `tasks=[A,A]`, `n=0` | `2` | two occurrences, zero cooldown |
| 6 | `tasks=[A,A]`, `n=1` | `3` | two occurrences forcing exactly one idle slot |
| 7 | `tasks=[A,A,A,B,B,C]`, `n=2` | `7` | unique max frequency with partial filler coverage |
| 8 | `tasks=[A,B,C,D]`, `n=2` | `4` | all distinct types, cooldown never binds |
| 9 | `tasks=[]`, `n=2` | `0` | empty task list |
| 10 | `tasks=[A,A,A,A]`, `n=3` | `13` | single type, large cooldown, idle time dominates |
