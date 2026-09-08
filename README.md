# backend-notes

Working notes on distributed systems design and Java collections.
Personal reference, kept short on purpose.

- `design/` — architecture reasoning: requirements, options, trade-offs, failure modes
- `java/` — collections idioms and Java 8 constraints
- `drills/` — paper exercises: read the prompt, write the code by hand, verify later

## Problem set

`problems/` holds 24 worked problems across four groups (windows and pointers, prefix sum / monotonic stack / heap, binary search / BFS / graphs / intervals, and data-structure design). Each has a detailed study page and a standalone Java 8 solution with its own test runner. Start at [problems/README.md](problems/README.md).

Run any solution with:

```
cd problems/<group>
javac --release 8 <ClassName>.java && java <ClassName>
```
