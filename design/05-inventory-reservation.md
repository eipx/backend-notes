# Inventory reservation under contention

## Requirements
- Functional: check availability, reserve N units, confirm or release a
reservation, prevent overselling, accept idempotent order submissions.
- Non-functional: reservation decision under 100 ms at the 99th percentile; a
single hot item can see tens of thousands of requests per second during a
sale; zero tolerance for overselling; a hold that is never confirmed must
expire (10 minutes is typical).
- Ask first: is inventory a single pool or per warehouse? Can a hold be
partially filled? Is a small oversell ever acceptable (pre-order backfill) or
is it always zero? Multi-region, or one primary region for writes?

## Interfaces
- `POST /reservations {itemId, quantity, orderId, idempotencyKey}` ->
`{reservationId, status, expiresAt}`
- `POST /reservations/{id}/confirm {paymentToken}` -> `{status}`
- `POST /reservations/{id}/release` -> `{status}`
- `GET /items/{id}` -> `{available}`

## Data model
- `Item {itemId, totalStock, reserved, version}`. Available = totalStock minus
reserved minus sold, or keep an explicit `available` counter.
- `Reservation {reservationId, itemId, quantity, orderId, status:
HELD|CONFIRMED|RELEASED|EXPIRED, createdAt, expiresAt}`
- `IdempotencyKey {key, reservationId}` for safe retries of the order call.

## Options
1. **Pessimistic row lock** (relational, `SELECT ... FOR UPDATE`). Correct and
simple to reason about. Serializes every writer on that row; a hot item turns
into a queue, and lock hold time under load pushes latency into seconds.
2. **Conditional update (compare-and-set).** Read `available` and `version`,
then `UPDATE ... WHERE version = v AND available >= qty`. No lock held; losers
retry. In a wide-column store this is a lightweight transaction, `UPDATE ...
IF available >= qty`, paying a consensus round per attempt instead of a lock
wait.
3. **Sharded counters** for extreme hot items: split `available` across N
sub-counters, route each request to one shard. Cuts contention by N but needs
a reconciliation read to know true zero, and adds complexity most items never
need.

Pick option 2 as the default (conditional update or lightweight transaction),
option 3 only for items identified in advance as sale-day hot.

## Where the state lives
Item availability and reservation rows live in the primary durable store,
relational or wide-column, never only in a cache. A reservation is
money-adjacent; the source of truth must survive a server restart.

## Concurrency and consistency
Two buyers race for the last unit: exactly one conditional write succeeds, the
loser is told "sold out" immediately rather than parked behind a lock only to
fail later. The idempotency key on the order call means a client retry after a
timeout finds the existing reservation instead of taking a second unit.

## Failure handling
Payment fails after the hold: the reservation stays `HELD`, stock stays
reserved (not sold), and it reverts on explicit release or on TTL expiry
through a background reaper process. Server crash between decrementing stock
and writing the reservation row: do both in the same conditional write or
transaction, or make the decrement idempotent by keying it to the reservation
id so a retry cannot double-decrement. Hot-item contention: bound CAS or
lightweight-transaction retries, back off with jitter, and return "try again"
rather than hold a request thread.

## Scaling
Availability reads are far more frequent than writes; cache the displayed
count with a 1 to 2 second TTL, but always re-check with the authoritative
conditional write at reservation time. Pre-identify hot items before a sale
and switch them to sharded counters ahead of time, not reactively.

## Operations
Metrics: reservation success and failure rate, conditional-write retry rate
(contention signal), hold-to-confirm latency, expired-hold rate. Alarms: retry
rate above a threshold on a single item, and a reconciliation check that fails
loudly if sold-plus-reserved ever exceeds total stock. Rollout: shadow the
conditional-write path against the existing lock-based path on real traffic
before cutting hot items over.

## Follow-up questions
1. What reclaims stock if confirm never arrives? TTL expiry plus a background
reaper that scans for holds past `expiresAt` and flips them to `RELEASED`.
2. How do you stop one buyer from holding many small reservations to freeze
out others? Cap outstanding holds per account and keep the expiry short,
minutes rather than hours.
3. Does this survive one unit and fifty thousand requesters? Yes for
correctness (one winner), but consider a waiting-room queue in front so the
store is not hammered with retries.
4. Why not serialize all updates to an item through a queue? Correct and
simple, but a single consumer per item bounds throughput and adds latency that
checkout budgets may not tolerate.
5. How does multi-warehouse inventory change this? Each warehouse (or virtual
pool) is its own row; the reservation call targets a specific pool, with
allocation rules deciding which pool to try first.
6. Why not eventual consistency with reconciliation instead of a lightweight
transaction? Because a customer already received a confirmation; fixing it
later means telling them their order failed after the fact.
7. Why key idempotency at the reservation attempt, not just the order? A retry
of the reservation call alone (a network blip before the order finishes)
should also be safe; look the key up before writing, not just at the top of
the flow.
