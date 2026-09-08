# Duplicate detection at high volume

## The problem
Tens of millions of messages per week, each carrying a reference that must be
unique within a time window. Reject any reuse of a reference already seen.

## Requirements
- Decision on the hot path, low single-digit milliseconds.
- Multi-datacenter: the same reference may arrive at two sites.
- Window measured in days; history beyond the window can expire.
- False negatives (missing a duplicate) are worse than false positives.

## Options
1. **Relational table with unique index.** Simple, exact, single-region. Does
   not scale writes across regions without a primary.
2. **Wide-column store keyed by reference, TTL on rows.** Horizontal writes,
   native TTL, multi-DC replication. The check is a read before write, which
   is a race unless the write is conditional.
3. **Bloom filter in front of option 2.** Answers "definitely not seen" in
   memory; only "maybe seen" goes to the store. Cuts store reads; never
   produces a false negative, which matches the requirement.

## The race and the fix
Two nodes receive the same reference in the same millisecond. Both read "not
seen", both write. A **conditional insert** (insert if not exists, a
lightweight transaction) makes exactly one succeed. It costs a consensus
round, so use it only on the insert path, not on plain reads.

## Consistency choice
Cross-DC reads at local quorum are fast but may miss a write landing in the
other DC in the last few hundred milliseconds. Decide: accept a tiny window
of missed duplicates for latency, or pay cross-DC quorum on the insert path.
State the choice and the number.

## Operations
Metrics: duplicate rate (a jump means a client is replaying), store latency
p99, conditional-insert failure rate (contention). Alerts on the last two.
