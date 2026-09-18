# Nearest location lookup with moving entities

## Requirements
- Functional: given a point, return the nearest available entities (drivers, drop-off
points), continuously updated as entities move, and support a reservation step so two
requesters are not matched to the same entity.
- Non-functional: entity positions update frequently (every few seconds for a moving
vehicle), a lookup must answer in well under a second, a busy city can have tens of
thousands of entities in a small area.
- Ask first: how far can a match reasonably be (a search radius bound)? How often do
positions actually update, and is slight staleness (a few seconds) acceptable? Does
availability change quickly enough that the index needs to reflect it in near real time?

## Interfaces
- `POST /positions {entityId, lat, lng}` from each moving entity, on an interval.
- `GET /nearest?lat=...&lng=...&count=5&radiusMeters=...` -> ranked list of entity IDs
with distance.
- `POST /reservations {entityId, requesterId}` -> succeeds for exactly one requester
per entity.

## Data model
Each entity has a current position and an availability flag, indexed by a spatial key
rather than scanned linearly. A reservation is a short-lived row keyed by entity ID,
the same conditional-write shape as any other single-winner claim.

## Options
Spatial indexing:
1. Geohash: encode lat/lng into a string where a shared prefix means physical
proximity; query by prefix range. Simple to implement on top of an ordinary key-value
or wide-column store (the geohash is just part of the key), but cells are rectangular
and vary in size with latitude, and a point near a cell edge has its nearest neighbor
in an adjacent cell that a naive prefix query misses.
2. Quadtree: recursively subdivide space into four quadrants only where entities are
dense, giving smaller cells in busy areas and larger ones in sparse areas without
wasting index entries on empty space. More complex to shard across machines than a flat
key range, since the tree shape itself is dynamic.
3. S2-style cells: a hierarchical decomposition of a sphere into roughly equal-area
cells, avoiding the latitude distortion of geohash and giving well-defined neighbor
lookups. A more complex dependency, but handles the boundary problem more cleanly than
geohash out of the box.

Pick geohash for a straightforward implementation on top of an existing wide-column
store (it is just a sortable string key), and always query the target cell plus its
eight neighbors to handle the boundary problem, rather than trying to size cells to
avoid it. Move to S2-style cells if the latitude distortion or the boundary logic
becomes a measured problem.

## Where the state lives
Current positions live in an in-memory or low-latency store keyed by the spatial index,
since a moving entity's old position is worthless the moment a newer one arrives; there
is little reason to durably store a long history of raw positions in the hot path store
(a separate analytics pipeline can keep that if needed). Reservations live in a durable
store with conditional writes, since a reservation is a business fact, not disposable
like a stale position.

## Concurrency and consistency
Two requesters both querying for the nearest entity at the same moment can both see the
same one as the closest available option; the nearest-lookup result is only a shortlist
of options. The actual match is a conditional write on the reservation, the same
compare-and-set or lightweight-transaction shape as any other single-winner race:
exactly one requester's reservation succeeds and the other's query moves to its next
option. Position updates are fire-and-forget and eventually consistent; a lookup a few
seconds stale is an acceptable trade for not synchronizing on every position write.

## Failure handling
An entity goes silent (stops sending positions) without explicitly marking itself
unavailable: expire its position entry with a short TTL (tens of seconds) so it drops
out of lookup results automatically rather than staying listed indefinitely. A
reservation is made but never confirmed (the requester abandons the flow): expire the
reservation quickly and return the entity to available, the same hold-then-confirm
shape used for any perishable resource.

## Scaling
Shard the spatial index by region so a lookup only ever queries the shard (or shards,
for a point near a shard boundary) covering the relevant area. Hot cities need
finer-grained sharding than the rest of the map; size shards by entity density, not by
geographic area, so a small dense downtown does not overload one shard while a whole
rural region sits idle on another.

## Operations
Metrics: lookup latency p99, position update rate and staleness, reservation contention
rate (how often the top match is already taken by the time of the write), match failure
rate (no available entity within radius). Alarms: staleness beyond the TTL grace period
(entities are going stale faster than expected), a shard's lookup latency diverging
from the rest (a hot-city shard needs splitting). Rollout: shard boundary changes are
risky mid-traffic; move a region's data during low-traffic hours with dual-read
verification before cutting reads over.

## Follow-up questions
1. What is the boundary problem and how is it fixed? A point near a cell edge can have
its true nearest neighbor in an adjacent cell; always search the target cell's neighbor
cells too, not just the one the point falls in.
2. How do you stop two requesters from getting the same entity? The lookup only returns
options; the actual claim is a conditional write on a reservation, so exactly one
requester's write succeeds.
3. Why geohash over a quadtree for a first version? A geohash is just a sortable string
prefix on top of a store you already run; a quadtree needs its own dynamic sharding
scheme to distribute across machines.
4. How often should moving entities report position? Frequently enough that staleness
stays under the lookup's acceptable error (every few seconds for a vehicle), balanced
against the write volume that frequency creates across the whole fleet.
5. How do hot cities get handled differently from the rest of the map? Shard by entity
density rather than a fixed geographic grid, so a small area with heavy traffic gets
proportionally more shards than a large sparse one.
6. What happens to a reservation if the requester's app crashes before confirming? A
short expiry returns the entity to the available pool; the requester (or a retry) sees
it as available again on their next lookup.
