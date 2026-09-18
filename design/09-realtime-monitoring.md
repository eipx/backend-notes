# Real-time monitoring and alerting

## Requirements
- Functional: agents on each host emit counters and latency samples; the system
aggregates, shows dashboards, and fires alarms on thresholds or anomalies.
- Non-functional: a fleet of thousands of hosts, each emitting on the order of hundreds
of distinct time series, ingestion in the millions of data points per minute, dashboard
queries answered in low seconds, alarm evaluation within a minute of the underlying
condition.
- Ask first: push or pull collection? What retention is required at full resolution
versus downsampled? What is an acceptable false-positive alarm rate? Does latency need
to be attributed per component of a request path, or only end to end?

## Interfaces
- Agent emits over a small client library: `counter.increment(name, tags)`,
`histogram.record(name, valueMs, tags)`.
- `GET /query?metric=...&from=...&to=...&groupBy=...` for dashboards.
- `PUT /alarms/{id} {metric, condition, evaluationWindow, threshold}` to define an
alarm.

## Data model
A time series is identified by a metric name plus a tag set (host, service, region,
endpoint). Each point is `(timestamp, value)` for a counter, or a compact sketch for a
latency distribution rather than every raw sample.

## Options
Collection: push (agents send to a collector) reaches short-lived processes that a
puller could miss between scrapes, but a burst of agents can overwhelm the collector
unless it sits behind a queue. Pull (a central system scrapes each agent on an
interval) makes back-pressure trivial, since the puller controls its own rate, and
makes checking host reachability a natural health signal, but it needs service
discovery to know what to scrape and struggles with anything too short-lived to be
scraped. Pick pull for a stable fleet of long-lived services, push (through a local
agent that batches and forwards) for anything short-lived.

Percentiles: never average per-host or per-window percentiles together; the average of
several p99s is not the p99 of the combined population and understates tail latency.
Aggregate from the raw distribution instead: HDR histograms give exact percentiles
within a fixed bucket error and fixed memory, good when the value range is known in
advance (request latency in milliseconds). t-digest sketches adapt to an unknown range
and merge cheaply across hosts, better for values whose scale is not known up front.
Either beats storing and averaging pre-computed percentiles.

## Where the state lives
Raw or lightly-aggregated points land in a write-optimized time series store. Long-term
data moves through storage tiers: full resolution for a short window (hours to a couple
of days), then downsampled (one-minute, then one-hour rollups) for weeks to months,
since nobody needs second-level resolution from six months ago and keeping it is pure
cost.

## Concurrency and consistency
Ingestion is append-only and tolerant of slight out-of-order arrival (a delayed agent
flush); aggregation windows should accept points a little late (a grace period of tens
of seconds) before closing a window, and mark a window final only after that grace
period. Dashboards and alarms read slightly stale, eventually-consistent aggregates;
nothing here needs strong consistency, a fresher number a few seconds later is fine.

## Failure handling
Cardinality control: an unbounded tag (a raw user ID, or a full URL with a query string
as a tag) multiplies the number of time series and can take down the ingestion path;
reject or bucket high-cardinality tags at the client library level, not after the fact.
Alarm flapping: a threshold hit for one data point should not page anyone; require the
condition to hold for a sustained evaluation window, and apply hysteresis (a higher
threshold to trigger than to clear) so the alarm does not oscillate. A collector outage
should not look like every service going down; distinguish absent data from a true zero
value and alarm on the former only after a longer grace period.

## Scaling
Shard the ingestion and storage tier by metric name or by a hash of the tag set so no
single node owns the whole fleet's writes. Pre-aggregate at the agent or at a local
collector (sum counters, merge sketches) before the central store, so fan-in does not
mean sending every raw sample from every host over the network.

## Operations
Metrics about the metrics system: ingestion lag, dropped-point rate, cardinality per
metric name, query latency, alarm evaluation latency. Alarms: ingestion lag beyond a
bound (the monitoring system going blind), a cardinality spike on a metric (a bad
deploy adding an unbounded tag). Rollout: a new alarm starts in a dry-run mode that
logs would-have-fired events before it is allowed to page anyone.

## Follow-up questions
1. Why not just store and later average percentiles computed per host? Averaging
percentiles is not a valid statistical operation; it systematically understates the
true tail. Aggregate from histograms or sketches instead.
2. How do you attribute end-to-end latency to one slow component in a request path?
Propagate a trace ID across service calls and record a span per component; the
end-to-end number is the sum, and the slow span is visible directly rather than
inferred.
3. What stops a bad deploy from creating millions of new time series? Cap cardinality
per metric at the client library, and alarm on a sudden rise in distinct tag
combinations for a given metric name.
4. Why pull instead of push for a stable fleet? Back-pressure is controlled by the
puller, and a host that stops responding to scrapes is itself a signal, not just a gap
in the data.
5. How do you keep an alarm from flapping right at the threshold? Require the condition
over a sustained window and use a different threshold to clear than to trigger.
6. Why downsample instead of keeping everything at full resolution? Storage cost grows
without bound and nobody queries six-month-old data at one-second resolution; rollups
cut cost by orders of magnitude for data nobody needs at full fidelity.
7. What happens to an alarm's evaluation if the monitoring pipeline itself falls
behind? Distinguish missing data from a healthy zero, and either widen the evaluation
window or suppress the alarm rather than firing on incomplete data.
