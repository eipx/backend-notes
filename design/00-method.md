# Design reasoning in 20 minutes

Every design answer has the same skeleton. Say each part out loud, in order.

1. **Requirements, two kinds.** Functional (what it does) and non-functional
   (scale, latency target, consistency needs, availability target). Ask for
   the numbers if they are not given; state assumptions if nobody answers.
2. **Two options, not one.** Name the simple approach and the scalable
   approach. A single design with no alternative is a guess, not a decision.
3. **The trade-off, in one sentence each.** Performance vs cost vs
   maintainability vs consistency. Pick, and say why the requirement drives
   the pick.
4. **Failure modes.** What breaks under load, under partition, under retry,
   under a bad deploy. Name the mitigation for each.
5. **Operations.** How you know it is healthy: metrics, alerts, dashboards,
   runbooks. A design nobody can operate is incomplete.

## Six pillars (Well-Architected vocabulary)

| Pillar | The question it asks |
|---|---|
| Operational excellence | Can we run, observe, and change it safely? |
| Security | Least privilege, encryption in transit and at rest, audit trail |
| Reliability | Recovery from failure, retries with backoff, multi-AZ or multi-region |
| Performance efficiency | Right data store and compute for the access pattern |
| Cost optimization | Pay for what is used; managed services vs self-hosted |
| Sustainability | Fewer resources for the same outcome |

## Trade-off vocabulary

- **Consistency vs availability**: under partition, refuse writes (consistent)
  or accept writes and reconcile later (available). Payments and ledgers choose
  consistency. Feeds and caches choose availability.
- **Latency vs durability**: acknowledge after memory (fast) or after replicated
  disk (safe). Say which the requirement needs.
- **Managed vs self-hosted**: managed costs more per unit and less in operations
  headcount. Small teams should default to managed.
- **Synchronous vs asynchronous**: sync is simpler to reason about; async
  absorbs bursts and decouples failure domains but needs idempotency.
