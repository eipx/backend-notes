# Migrating a configuration store with zero downtime

## The problem
A relational configuration store is the source of truth for many services.
Move it to a distributed store across datacenters without stopping readers or
writers.

## Requirements
- No read outage for consumers at any point.
- Bounded inconsistency window during migration, measured and reported.
- Reversible until cut-over is declared final.

## Phases
1. **Dual write.** Application writes go to both stores. Reads still come from
   the old one. Any write failure on the new store is logged, not surfaced.
2. **Backfill.** Copy historical rows in batches, ordered by a stable key,
   resumable from a checkpoint.
3. **Reconcile.** Compare row counts and per-row hashes between stores;
   publish the drift number. Drift must go to zero and stay there.
4. **Shadow read.** Serve from old, also read new, compare, log mismatches.
   Zero mismatch for a defined period is the gate.
5. **Cut over reads** to the new store, one consumer at a time. Keep dual
   writes so rollback is a flag flip.
6. **Retire** the old store only after a full retention period with no
   rollback.

## Trade-offs
- **Change data capture vs application dual write**: CDC needs no code change
  in every writer but adds a pipeline to operate; dual write is explicit but
  touches every writer.
- **Replication direction**: one-way during migration; two-way only if
  rollback must preserve writes made after cut-over, at the cost of conflict
  handling.

## Failure modes
- A writer missed in the dual-write inventory: caught by reconciliation.
- Schema drift between stores: generate both schemas from one model so they
  cannot diverge by hand.
- Cut-over during an incident: cut-over is a scheduled change with a rollback
  step rehearsed beforehand.
