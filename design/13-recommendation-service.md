# Recommendation service

## Requirements
- Functional: return a ranked list of items for a user, fast enough to sit on
a page load path.
- Non-functional: p99 latency under roughly 100 ms for the serving call, a
catalog of millions of items, request volume in the thousands per second,
freshness where a user's last few minutes of activity should influence results
within a similar order of time, not necessarily instantly.
- Ask first: how many items does a response need (10, 50)? Is there a
cold-start population (new users, new items) that needs a defined fallback? Do
multiple experiment variants need to run concurrently against the same traffic?

## Interfaces
- `GET /recommendations?userId=...&context=...&count=20` -> ranked list of
item IDs with scores.
- Internal: `ShortlistGenerator.generate(userId) -> List<itemId>`,
`Ranker.rank(userId, shortlist) -> List<scoredItem>`.

## Data model
- Offline: a user-item interaction log (views, purchases, ratings) feeding
batch shortlist generation.
- Online: a key-value store keyed by `userId` holding a precomputed shortlist,
refreshed on a schedule; a feature store keyed by `userId` and by `itemId` for
ranking-time features.

## Options
1. **Fully online**: generate a shortlist and rank it at request time from
live data. Freshest possible, but a request-path dependency on a heavy
computation risks the latency budget and couples serving availability to a
more complex pipeline.
2. **Fully offline**: precompute a ranked list per user in a batch pipeline,
serve it as a plain key-value lookup. Meets the latency budget trivially, but
is only as fresh as the last batch run (hours old) and cannot react to what
the user just did.
3. **Offline shortlist, online ranking** (the usual production shape): a batch
pipeline narrows millions of items down to a few hundred per user ahead of
time; a lightweight online ranker re-scores just that shortlist using the
freshest available signals (this session's clicks, current context) at request
time. Bounds the request-path work to ranking a few hundred items instead of
searching millions, while still reacting to recent behavior.

Pick option 3. The split keeps the expensive, embarrassingly-parallel part
offline and the latency-sensitive part small enough to run inline.

## Where the state lives
Precomputed shortlists live in a low-latency key-value store, one row per
user, overwritten on each batch refresh. Ranking features (recent activity
counters, item popularity) live in a feature store built for fast point
lookups by key, updated by a stream process so ranking sees near-real-time
signals without the serving path doing any heavy aggregation itself.

## Concurrency and consistency
Reads are the hot path and are independent per user, no coordination needed
between requests. The batch refresh writes a new shortlist per user; write it
under a new version key and flip a pointer, or overwrite atomically per row,
so a reader never sees a half-written list mixing old and new items.

## Failure handling
Cold start: a new user with no interaction history gets a fallback list
(globally popular items, or popular within a stated segment like region or
referrer) instead of an empty response. Ranking service slow or down: serve
the precomputed shortlist unranked, or ranked by a cheap static score, rather
than failing the request; a slightly worse ranked list beats no list. Feature
store miss for one item: rank it with default feature values rather than
dropping it or failing the whole request.

## Scaling
Shortlist generation is a batch pipeline and scales by adding compute to the
batch cluster, decoupled from serving load entirely. Serving scales
horizontally like any stateless read path in front of a key-value store; the
key-value store scales by adding replicas and partitioning by user ID, which
is naturally well distributed.

## Operations
Metrics: serving latency p50/p99, ranking service error rate and fallback
rate, shortlist staleness (age since last batch refresh per user),
click-through or engagement rate as an offline-measured quality signal.
Alarms: fallback rate spiking (ranking degraded), staleness beyond the
expected batch interval (the pipeline stalled). Rollout: experiment support
means the serving layer can select between shortlists or ranking models by a
bucketing key, logging which variant served each request so results can be
measured after the fact.

## Follow-up questions
1. How do you handle a brand-new item with no interaction history? Seed it
into shortlists through a content-based or metadata-based path (similar items,
same category) until enough interaction data exists to rank it normally.
2. What if the ranking service times out? Fall back to the precomputed
shortlist order or a cheap heuristic score, log the fallback, and never block
the response on the ranker indefinitely.
3. How fresh can this realistically be? As fresh as the streaming feature
pipeline's lag for signals folded into ranking, and only as fresh as the last
batch run for the shortlist itself; state both numbers rather than one blended
freshness claim.
4. How do you run two ranking models against live traffic at once? Bucket
users by a stable hash of their ID, route each bucket to a model variant, and
log the variant with every served response for later comparison.
5. Why not rank the full catalog online for every request? Millions of items
scored per request blows the latency budget; narrowing to a few hundred items
offline makes online ranking cheap enough to run inline.
6. How do you cache repeated requests for the same user? A short-TTL cache in
front of the key-value store absorbs bursts (a user refreshing a page), but
the TTL should be short enough that it does not mask an actual shortlist
refresh.
