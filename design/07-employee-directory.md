# Employee directory at large scale

## Requirements
- Functional: search by name, email, or ID; view a profile; view a person's manager;
view direct reports; view every report under a person, transitive.
- Non-functional: about 1M people, read-heavy (searches and profile views dominate; org
changes are a few thousand a day at most), search latency under 200 ms, hierarchy
queries fast enough for interactive use even for someone with tens of thousands of
transitive reports.
- Ask first: is search prefix-only or does it need fuzzy or substring matching? Can
hierarchy reads lag by seconds, or must a manager change be visible immediately? Is the
org a strict tree, or does it need dotted-line (matrix) relationships too? Is
point-in-time history required, such as who reported to whom last quarter?

## Interfaces
- `GET /people?q=...` -> list of `{id, name, email, title}` matches
- `GET /people/{id}` -> profile
- `GET /people/{id}/manager` -> profile
- `GET /people/{id}/reports?transitive=false|true` -> list of profiles
- `PUT /people/{id}/manager {newManagerId}` -> admin-only, triggers a hierarchy update

## Data model
- `Person {id, name, email, title, managerId, active}` in the primary store, keyed by
`id`.
- A search document per person, `{id, name, email}`, in a text index for prefix and
token search.
- A hierarchy index, shape depends on the option chosen below.

## Options
Search: an inverted index over name and email tokens (edge n-grams for prefix search)
beats scanning the primary store past a few tens of thousands of rows; an exact email
or ID lookup is just a keyed read and does not need the index at all.

Hierarchy storage:
1. **Adjacency list** (store `managerId` on each row). Trivial to update: one row
changes when someone gets a new manager. Direct reports is a single indexed lookup.
"All reports under X" needs a recursive walk that, for someone near the top, touches
most of the table.
2. **Materialized path** (store a path string like `/1/45/302/` per person). "All
descendants" becomes a prefix query on the path, fast to read. Moving a subtree to a
new parent means rewriting the path of every person under it, expensive exactly when a
reorganization moves a whole department.
3. **Closure table** (a row per ancestor-descendant pair with depth). Reads for
ancestors or descendants are a simple keyed query with no recursion. A subtree move
still requires rewriting that subtree's ancestor rows, the same cost as materialized
path, but single-person moves (the common case) touch only that person's ancestor chain.

Pick adjacency list as the source of truth, since it is the simplest thing to keep
correct on write, and derive a closure table asynchronously as a read-optimized index
for hierarchy queries. This trades a few seconds of staleness on the derived index for
fast reads and a write path that never has to touch a large subtree synchronously.

## Where the state lives
`Person` rows and the adjacency `managerId` are the durable source of truth. The search
index and the closure table are derived, rebuilt from change events, and can be dropped
and regenerated without losing information.

## Concurrency and consistency
Manager changes are rare relative to reads, so the derived indexes can be eventually
consistent; seconds of lag is acceptable for an org chart. Propagate changes through a
change log or outbox so index updates are ordered and retryable, rather than relying on
every write path remembering to update every index.

## Failure handling
A closure-table rebuild that fails partway must not leave readers looking at a
half-updated subtree: write the new version under a new key and swap a pointer, or use
a done marker. A missed search-index update is caught by a nightly reconciliation pass
against the primary store, since search staleness is low-stakes.

## Scaling
1M rows is small in raw volume; the risk is a hot partition, someone near the top of
the tree with tens of thousands of transitive reports, if the closure table is
partitioned by ancestor ID alone. Paginate hierarchy responses and, for the handful of
very large subtrees, shard the closure rows by ancestor plus a depth bucket instead of
ancestor alone. Profile and search reads scale by adding read replicas or index shards;
writes are low volume enough that they are never the bottleneck.

## Operations
Metrics: search latency p99, index staleness (age of the last applied change event),
hierarchy query latency for the largest subtrees, reorganization batch duration.
Alarms: index staleness beyond a set bound, closure-table rebuild failures, a hierarchy
query timing out (usually a sign a subtree got too large for the current partitioning).
Rollout: run the closure table alongside the adjacency-only design, reconcile counts,
and only route reads to it once counts match for a full day.

## Follow-up questions
1. Why not just recurse over the adjacency list at read time? Fine for a small subtree;
for someone near the root it means touching most of the table on every read.
2. What happens when a whole department gets a new manager at once? Treat it as a batch
subtree move, rewrite the affected closure rows offline, and cut over at a scheduled
time rather than inline with a request.
3. Do you need historical org state? If yes, do not overwrite `managerId`; append
change events and derive the current tree from them, keeping the ability to reconstruct
any past date.
4. How do you handle a person with two functional managers? The adjacency list assumes
one tree; add a separate many-to-many relationship for dotted lines, kept out of the
primary hierarchy so reports-under-a-person stays well defined.
5. How does the search index stay current? Change data capture or an outbox from the
primary store feeds the index pipeline, so it reflects committed writes rather than
best-effort dual writes from the application.
6. What happens when someone leaves? Mark the row inactive rather than deleting it
(audit trail, payroll history), reassign their direct reports to their own manager or a
named interim, and update both derived indexes.
7. Why cache profiles but not hierarchy results? Profile lookups cluster heavily on a
small set of popular people; hierarchy result sets are already served quickly from the
closure table and vary too much per person to cache well.
