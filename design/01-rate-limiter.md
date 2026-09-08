# API rate limiter

## Requirements
- Limit each client to N requests per window (say 100 per minute).
- Decision in under 1 ms; must not become the bottleneck it protects against.
- Runs behind many API servers, so the count must be shared or approximated.

## Options
1. **Fixed window counter.** One counter per client per minute. Simple; allows
   2N at a window boundary (burst at 0:59 and 1:00).
2. **Sliding window log.** Store every request timestamp; count those inside
   the last 60 s. Exact; memory grows with traffic.
3. **Sliding window counter.** Weighted blend of the current and previous
   fixed windows. Near-exact, constant memory. Usual production choice.
4. **Token bucket.** Bucket refills at a steady rate; each request takes a
   token. Allows controlled bursts; the standard for "average rate plus burst".

## Where the state lives
- **Local in-process**: fastest, but each server enforces its own limit, so the
  effective global limit is N times the number of servers. Acceptable if the
  load balancer pins clients to servers or if approximation is fine.
- **Shared store (Redis)**: one truth. Use an atomic increment with expiry, or
  a small Lua script for the sliding window. Adds a network hop; put the store
  in the same zone. Failure mode: if the store is down, decide **fail open**
  (allow) or **fail closed** (deny). Protecting a payments backend usually
  fails closed; protecting a read API usually fails open.

## Concurrency
The counter update must be atomic. Read-then-write from two servers loses
increments. Use the store's atomic primitive, never get-add-set.

## Response
Return 429 with `Retry-After`. Clients then back off with jitter; without
jitter, all clients retry at the same instant and recreate the spike.

## Operations
Metrics: allow/deny counts per client, store latency, store error rate.
Alert on deny spikes (a client misbehaving) and on store errors (limiter
degraded).
