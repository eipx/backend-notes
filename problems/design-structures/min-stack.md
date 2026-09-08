# Min Stack
`ref: LC 155` · Difficulty: Medium · Pattern: stack augmented with running-minimum tracking

## Problem

Design a stack that supports the usual push/pop/top operations plus retrieving the current minimum element, with **every** operation running in O(1) time (not just amortized, and not O(log n)).

The class exposes:
- `void push(int val)` — push `val` onto the stack.
- `void pop()` — remove the element on top of the stack. Guaranteed to be called only on a non-empty stack.
- `int top()` — return the element on top of the stack without removing it. Guaranteed to be called only on a non-empty stack.
- `int getMin()` — return the minimum element currently anywhere in the stack. Guaranteed to be called only on a non-empty stack.

## Constraints

- Values fit in a 32-bit signed int (`-2^31 <= val <= 2^31 - 1`), including `Integer.MIN_VALUE` and `Integer.MAX_VALUE` as legal inputs — this matters for the delta-based variant, where a naive `int` subtraction can overflow.
- Up to `3 * 10^4` calls total across all four methods — this is small, but the *hard* O(1)-per-call requirement (not "any polynomial-log" bound) is the real constraint: no `TreeMap`/heap-based `getMin` is acceptable even though it would pass on time for this input size, because it would not actually satisfy the stated requirement.
- `pop`, `top`, and `getMin` are never called on an empty stack — no need for defensive empty checks in the core logic, though production code would still add them.

## Worked examples

**Example 1 — classic sequence**
```
push(-2) push(0) push(-3)
getMin() -> -3
pop()
top() -> 0
getMin() -> -2
```
After the three pushes the stack (top-first) is `[-3,0,-2]` and the minimum is `-3`. `pop()` removes `-3`, leaving `[0,-2]`; `top()` correctly reports `0` (the new top), and `getMin()` correctly recomputes to `-2` (the minimum of what remains) — not `-3` again, and not some cached stale value.

**Example 2 — duplicate minimum values**
```
push(1) push(1) push(1)
getMin() -> 1
pop() getMin() -> 1
pop() getMin() -> 1
```
Three copies of the same minimum value are pushed. Popping one copy must not make the tracked minimum "forget" that two more copies of `1` are still in the stack — `getMin()` must keep returning `1` until all three copies are gone. This is exactly why a min-tracking stack must push a value on every single `push` call (even when the new value ties the current min) rather than only pushing when a *strictly new* minimum appears.

**Example 3 — wide value range (overflow awareness)**
```
push(Integer.MAX_VALUE) push(Integer.MIN_VALUE)
getMin() -> Integer.MIN_VALUE   top() -> Integer.MIN_VALUE
pop()
getMin() -> Integer.MAX_VALUE   top() -> Integer.MAX_VALUE
```
This sequence is specifically chosen to stress the delta-based variant (see Variants/Approach): `Integer.MAX_VALUE - Integer.MIN_VALUE` overflows a 32-bit `int` by a wide margin, so any implementation that stores `(value - runningMin)` as an `int` will silently compute the wrong delta here unless it widens to `long`.

## Edge cases checklist

- `getMin` on a stack with exactly one element.
- Popping down to a single element and confirming `getMin`/`top` still work correctly on what remains.
- Duplicate values equal to the current minimum, pushed more than once, then popped one at a time.
- A strictly decreasing sequence of pushes (every push sets a new minimum) followed by pops (the minimum must "roll back" to the previous minimum at each pop).
- A push/pop that never touches the current minimum at all (push something larger, then pop it) — the minimum must remain unchanged throughout.
- Extreme values (`Integer.MIN_VALUE`, `Integer.MAX_VALUE`) pushed together, which stresses any implementation that computes differences between values.

## Approach

### Naive

Use a single stack of raw values and, for `getMin`, scan the entire stack to find the smallest element — O(n) per `getMin` call instead of O(1). This satisfies `push`/`pop`/`top` in O(1) but fails the stated requirement that *every* operation, including `getMin`, be O(1). A variant using a sorted structure (e.g. a `TreeMap<Integer,Integer>` counting occurrences) gets `getMin` down to O(log n) via `firstKey()`, which is fast enough for `3*10^4` calls in practice, but still does not meet the strict O(1) bound the problem asks for.

### Optimal

**Key invariant:** at every point in time, the top of a second, parallel "running minimum" structure holds the minimum of every element currently in the stack **at or below** that position — so popping the main stack and popping this parallel structure together always leaves both structures perfectly describing "what the minimum was right before the popped element was pushed."

**Version A — two parallel stacks (equivalent to one stack of `{value, currentMin}` pairs).** On `push(val)`, push `val` onto the value stack, and push `min(val, currentMinTop)` onto the min stack (or just `val` if the min stack is empty) — critically, push a *new* entry onto the min stack on every single push, even when `val` ties or exceeds the current minimum, so that `pop()` can simply pop both stacks in lockstep without ever needing to figure out whether the popped value "was" the minimum.

**Version B — single stack of deltas (`val - runningMin`), O(1) *extra* space follow-up.** Instead of a second full stack, store only the difference between each pushed value and what the running minimum was *immediately before* that push. A negative delta means "this push set a new minimum" (and the old minimum is recoverable from the delta and the new minimum: `oldMin = newMin - delta`); a non-negative delta means "the minimum was unaffected" (and the original value is recoverable as `runningMin + delta`). This trades a second same-size stack for a single stack of numbers plus one extra variable, which is the O(1)-extra-space refinement of Version A.

### Step-by-step trace

Trace of Example 1 with Version A (two stacks), shown top-first:

| Step | Operation | Return | values (top-first) | mins (top-first) |
|---|---|---|---|---|
| 1 | `push(-2)` | — | `[-2]` | `[-2]` |
| 2 | `push(0)` | — | `[0,-2]` | `[-2,-2]` |
| 3 | `push(-3)` | — | `[-3,0,-2]` | `[-3,-2,-2]` |
| 4 | `getMin()` | `-3` | unchanged | unchanged |
| 5 | `pop()` | — | `[0,-2]` | `[-2,-2]` |
| 6 | `top()` | `0` | unchanged | unchanged |
| 7 | `getMin()` | `-2` | unchanged | unchanged |

Trace of the same sequence with Version B (delta stack), tracking `minVal` separately:

| Step | Operation | Return | deltas (top-first) | minVal |
|---|---|---|---|---|
| 1 | `push(-2)` | — | `[0]` | `-2` |
| 2 | `push(0)` | — | `[2, 0]` (0-(-2)=2) | `-2` |
| 3 | `push(-3)` | — | `[-1, 2, 0]` (-3-(-2)=-1, negative -> new min) | `-3` |
| 4 | `getMin()` | `-3` | unchanged | `-3` |
| 5 | `pop()` | — | `[2, 0]`; popped delta was `-1` (negative) so `minVal = -3 - (-1) = -2` | `-2` |
| 6 | `top()` | `0`; top delta `2` is `>= 0` so value `= minVal + delta = -2 + 2 = 0` | unchanged | `-2` |
| 7 | `getMin()` | `-2` | unchanged | `-2` |

## Java 8 solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LC 155 - a stack that supports push, pop, top, and getMin, all in O(1) time.
 * Two implementations:
 *   1) MinStackTwoStacks - a value stack plus a parallel min-tracking stack
 *      (equivalent in cost to a single stack of {value, currentMin} pairs)
 *   2) MinStackDelta      - a single stack storing (value - runningMin) deltas,
 *      the classic O(1)-extra-space follow-up
 */
public class MinStack {

    interface IMinStack {
        void push(int val);
        void pop();
        int top();
        int getMin();
    }

    // ---------------------------------------------------------------
    // Version A: two parallel stacks
    // ---------------------------------------------------------------
    static class MinStackTwoStacks implements IMinStack {
        private final Deque<Integer> values;
        private final Deque<Integer> mins; // mins.peek() is always the min of everything below it, inclusive

        MinStackTwoStacks() {
            values = new ArrayDeque<Integer>();
            mins = new ArrayDeque<Integer>();
        }

        @Override
        public void push(int val) {
            values.push(val);
            // Push a duplicate onto mins even on a tie, so pop() cannot pop the wrong min later.
            if (mins.isEmpty() || val <= mins.peek()) {
                mins.push(val);
            } else {
                mins.push(mins.peek());
            }
        }

        @Override
        public void pop() {
            values.pop();
            mins.pop();
        }

        @Override
        public int top() {
            return values.peek();
        }

        @Override
        public int getMin() {
            return mins.peek();
        }
    }

    // ---------------------------------------------------------------
    // Version B: single stack of deltas (val - currentMin), O(1) extra space
    // ---------------------------------------------------------------
    static class MinStackDelta implements IMinStack {
        // long avoids overflow: val - minVal can exceed the int range when val and
        // minVal sit near opposite ends of Integer.MIN_VALUE..MAX_VALUE.
        private final Deque<Long> deltas;
        private long minVal;

        MinStackDelta() {
            deltas = new ArrayDeque<Long>();
        }

        @Override
        public void push(int val) {
            if (deltas.isEmpty()) {
                deltas.push(0L);
                minVal = val;
            } else {
                long delta = (long) val - minVal;
                deltas.push(delta);
                if (delta < 0) minVal = val; // val is a new minimum
            }
        }

        @Override
        public void pop() {
            long delta = deltas.pop();
            if (delta < 0) {
                minVal = minVal - delta; // undo: recover the min that was active before this push
            }
            // if delta >= 0, minVal was not changed by this push, so it needs no update
        }

        @Override
        public int top() {
            long delta = deltas.peek();
            if (delta < 0) {
                return (int) minVal; // this element WAS the minimum when pushed
            } else {
                return (int) (minVal + delta);
            }
        }

        @Override
        public int getMin() {
            return (int) minVal;
        }
    }
}
```

## Complexity

- `push`: O(1) for both versions — a constant number of stack pushes and comparisons.
- `pop`: O(1) for both versions — a constant number of stack pops and, for the delta version, one conditional arithmetic update.
- `top`, `getMin`: O(1) for both versions — a single peek (Version A) or a peek plus constant arithmetic (Version B).
- Space: O(n) for both, where n is the number of elements pushed and not yet popped — Version A uses two same-size stacks (roughly 2x the values), Version B uses one stack of `long` deltas plus a single `long` variable (roughly 1x the values, the "O(1) extra space" improvement referenced in its name).

## Java 8 pitfalls for this problem

- `Deque<Integer>` used as a stack via `push`/`pop`/`peek` — `ArrayDeque` is the standard Java 8 choice; avoid `java.util.Stack` (it is a legacy, synchronized class with unnecessary locking overhead for single-threaded use).
- Boxed `Integer` from `mins.peek()`/`values.peek()` auto-unboxes when compared with `<=` or assigned to an `int` — safe here since the code only peeks when the deque is known non-empty (guaranteed by the problem, or checked explicitly), but calling `.peek()` on an empty deque returns `null`, and unboxing that `null` throws `NullPointerException`.
- The delta version must use `long`, not `int`, for both `minVal` and every stored delta — `Integer.MAX_VALUE - Integer.MIN_VALUE` overflows `int` by a factor of nearly 2, and casting back to `int` only happens at the very end, after arithmetic is safely done in `long`.
- `(int) minVal` and `(int) (minVal + delta)` narrowing casts are safe only because the *original* pushed values were themselves valid `int`s and the arithmetic reconstructs one of those original values exactly — never cast an intermediate `long` sum that has not been proven to fit back in range.
- No `var`, no records — `Deque<Integer>` and `Deque<Long>` fields must be declared with explicit generic types.
- Comparing popped/returned values with `==` is fine for primitive `int`/`long` results after unboxing, but never rely on `==` for the boxed `Integer`/`Long` objects themselves (e.g. `mins.peek() == val` is unsafe outside the small cached integer range); this solution avoids the issue by unboxing into primitives before comparing.

## Wrong approaches and why they fail

- **Only pushing onto the min-tracking stack when a value is a *strictly new* minimum (skipping ties).** Sequence: `push(1) push(1) push(1) pop() getMin()` — if the min stack only recorded `[1]` once instead of three times, the single `pop()` would pop the min stack's only entry, leaving `getMin()` with nothing to report (or falling back to a stale/incorrect value), even though two more `1`s are still on the value stack.
- **Recomputing the minimum by scanning the whole value stack inside `getMin`.** This is correct but O(n) per call, failing the explicit O(1)-per-operation requirement even though it would run fast enough in practice for `3*10^4` calls — a "passes because the input is small" solution is not the same as a correct-complexity solution, and this repo cares about the latter.
- **Storing `(value - runningMin)` as an `int` instead of `long` in the delta version.** Sequence: `push(Integer.MAX_VALUE) push(Integer.MIN_VALUE)` — the delta `Integer.MIN_VALUE - Integer.MAX_VALUE` is far outside the `int` range and wraps around silently (no exception in Java's default arithmetic), corrupting both the stored delta and every subsequent `top()`/`pop()` reconstruction that depends on it.

## Variants

- **Support `getMax()` as well as `getMin()`.** Maintain a second parallel max-tracking stack (or a second delta relative to a running max) alongside whichever min-tracking approach is chosen — the two are independent and do not interact, so both can be maintained simultaneously at the same O(1) per-operation cost, roughly doubling the extra space.
- **Thread safety.** Wrap `push`/`pop` in a single lock since they must update the value structure and the min-tracking structure atomically together; `top`/`getMin` under the same lock are then trivially consistent. A lock-free stack is possible in general but significantly harder to reason about once a second piece of state (the running minimum) must stay perfectly in sync with every push/pop.
- **Streaming input where old elements must expire after a time window (a "sliding window minimum" rather than a stack).** This is a materially different problem — a monotonic deque (increasing order of values from front to back) is the standard structure, since a plain stack has no way to discard elements from the *opposite* end that a stack's LIFO discipline requires; it is worth knowing this variant is not a small tweak to Min Stack but a different pattern entirely.

## Test cases

| # | Operation sequence | Expected outputs | What it tests |
|---|---|---|---|
| 1 | `push(-2) push(0) push(-3) getMin() pop() top() getMin()` | `-,-,-,-3,-,0,-2` | Classic example |
| 2 | `push(5) getMin() top()` | `-,5,5` | Single element: getMin and top agree |
| 3 | `push(3) push(2) push(1) getMin() pop() getMin() pop() getMin()` | `-,-,-,1,-,2,-,3` | Strictly decreasing pushes, then min rolls back on each pop |
| 4 | `push(1) push(1) push(1) getMin() pop() getMin() pop() getMin()` | `-,-,-,1,-,1,-,1` | Duplicate minimum values survive repeated pops |
| 5 | `push(5) push(1) push(10) getMin() pop() getMin() top()` | `-,-,-,1,-,1,1` | Min unaffected by a larger push/pop that never touches it |
| 6 | `push(MAX) push(MIN) getMin() top() pop() getMin() top()` | `-,-,MIN,MIN,-,MAX,MAX` | Wide value range, overflow safety for the delta variant |

Randomization note: this problem has no randomized methods, so every expected value above is exact. Both the two-stack and delta implementations are run against every row and must agree.
