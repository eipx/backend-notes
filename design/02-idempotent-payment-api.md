# Idempotent payment API

## The problem
A client sends "transfer 100" and the connection drops before the response.
Did it happen? The client retries. Without protection the transfer runs twice.

## Requirements
- Exactly-once effect for a given client intent, under retries and timeouts.
- Safe across multiple API servers.
- Reasonable retention: a retry days later should still be recognized.

## Design
1. Client generates an **idempotency key** (UUID) per intent and sends it in a
   header. Same intent, same key; new intent, new key.
2. Server, before doing the work, **reserves** the key: insert into a store
   with a conditional write ("create only if absent"). Two concurrent requests
   with the same key: one wins the insert, the other sees the existing row.
3. Winner does the work, then stores the **result** (status code and body)
   against the key.
4. Loser (or a later retry) reads the stored result and returns it verbatim.
   If the winner is still in progress, return 409 or wait briefly, never
   redo the work.

## Trade-offs
- **Key scope**: per client, so one client cannot collide with another. Store
  the key as `clientId:key`.
- **Retention**: 24 h to 7 days is common. TTL on the row; document it.
- **Store choice**: needs a conditional insert. A relational unique index does
  it; a DynamoDB conditional put does it; Redis `SET NX` does it. Cassandra
  lightweight transactions do it at a latency cost.
- **Request fingerprint**: store a hash of the body with the key and reject a
  retry whose body differs (same key, different amount is a client bug, and
  returning the old result would hide it).

## Failure modes
- Server crashes after the work but before storing the result: the key is
  reserved but has no result. Mitigation: do the work and the result write in
  one transaction where the store allows, or make the work itself idempotent
  downstream (the ledger checks the key too).
- Clock skew on TTL: use the store's own expiry, not application time.

## Retries done right
Exponential backoff with jitter, capped attempts, and a circuit breaker so a
dead dependency does not consume every thread in the pool.
