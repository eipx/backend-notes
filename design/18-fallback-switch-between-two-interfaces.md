# Design: a fallback switch between two interfaces of a hosted connectivity service

A small operator command, designed from a one-paragraph ticket and a requirements page that
pulled in two directions. The interesting part is not the code — a thirty-line generalisation
of a command that already existed — but reading the requirement correctly: which of the two
"fallbacks" the page describes is this component's job, what the command must leave alone, and
what to call the one option that was not in the ticket. Identifiers are illustrative and do not
come from any public tree; the two patches are attached.

## The system in one paragraph

A messaging network delivers store-and-forward (SaF) messages by parking them in named queues;
the receiving organisation's software later opens the queue and reads. Which queue a message
goes to is decided by a routing-rule table (`TB_SAF`): each row names a receiver pattern, a
service, an environment (live or test; test names carry a `~test` suffix), a target queue, a
*rank* and an *enabled* flag. **The lowest-ranked enabled rule wins.** Rows come in pairs — one
for the organisation's exact directory name, one for the wildcard below it — so an
organisation-level change always touches two rows, and a filter on the organisation catches
both.

The routing-configuration service (RCS) owns this table and exposes operator commands over it:
a shell wrapper in `/opt/rcs/bin/`, a Python command behind it that writes through an audited
transaction, and three registration points so operations staff can run it from the operator
console (a dialog definition in XML, an access-control list, the package spec).

## The hosted service and its two interfaces

Organisations that do not run their own network stack use the operator's hosted connectivity
service (HC). HC connects today through a conventional bridge stack and, because one platform
serves many organisations, routes their SaF traffic to three **shared** queues (`hcshared_a`,
`hcshared_b`, `hcshared_ab`, one per service type). HC is replacing the bridge stack with a
direct gRPC connection. An organisation on the gRPC flow stops using the shared queues and gets
**its own** queues (`<orgcode6>_hostedconna`, `…b`, `…ab`), with new rules at rank 1 pointing
at them; the older rules are kept at worse ranks so there is a way back.

Many HC organisations came from the operator's basic hosted interface (Basic) and still have
the rules that pointed at their own Basic queue. Such an organisation ends up with three tiers:

| Rank | Queue | Interface |
|---|---|---|
| 1 & 2 | `<orgcode6>_hostedconna` | HC over **gRPC** |
| 3 & 4 | `hcshared_a` | HC over the **bridge** stack |
| 5 & 6 | `<orgcode6>_<queuename>` | the organisation's **own** (Basic or on-premises) interface |

## The command that already existed

`RCS_Hc2Basic_SAF_upd.sh -B <orgcode6> -D` finds the organisation's `TB_SAF` rows whose queue
name starts with `hcshared` and sets `enabled = false`; traffic falls through to the next tier.
`-E` sets them back. Optional narrowing: `-S` one service, `-e L|T|A` live, test or both,
`-N` an explicit colon-separated list of queue names (which replaces the `hcshared` default);
`--preview` shows the rows and writes nothing. That is the whole mechanism, and it only knows
about tier 2. Nothing can close tier 1.

## The requirement, and the two fallbacks the page describes

The ticket: "provide operations with a command to switch traffic between the gRPC and the
bridge approaches", built "similar to" the existing command. The requirements page says more,
and says it in two different sections:

- **"Fallback from gRPC to the bridge, staying on HC."** Done *inside HC*: the bridge stack
  is configured to read the organisation's own gRPC queue, the gRPC channels are closed, and
  monitoring is attached. The page states that this "does not alter message routing logic or
  move traffic to shared queues". It needs **no change in RCS** — the rule still says
  `<orgcode6>_hostedconna`; only who reads that queue changes.
- **"Fallback from HC gRPC to the organisation's own interface."** Done *in RCS*, and the
  section that names the existing command as the model. The mechanism "must support enabling
  and disabling the rules associated with the gRPC queues and the shared queues, together or
  independently", and "must remain operational regardless of whether shared-queue or gRPC-queue
  rules are present". Its before/after table shows *one* "fallback command" leaving tiers 1 and
  2 disabled and tier 3 enabled.

The command is the second kind. The ticket's title uses the first section's words, which is
where the confusion came from, and it leaves one question the page does not settle: is
"gRPC rules closed, shared rules still open" — traffic flowing to HC through the shared bridge
queue — a state operations are meant to stop in? Section one says a bridge fallback should not
go through shared queues; section two's "independently" says the toggle alone must exist. The
implementation is the same either way; the *default* is the product owner's call.

## Three doors

For one organisation and one service, think of the three tiers as three doors in a corridor;
a message takes the **first open door**. Each command owns its doors and nothing else — no
command moves a message, creates a queue or talks to HC.

| | `-D` closes | `-E` opens |
|---|---|---|
| existing `RCS_Hc2Basic_SAF_upd.sh` | door B (shared) | door B |
| new command, default | door A (gRPC) | door A |
| new command, `-I all` | doors A and B, one transaction | doors A and B |

| State | A | B | C | Message goes through |
|---|---|---|---|---|
| normal | open | open | open | A — gRPC |
| fallback to the bridge | closed | open | open | B — shared queue |
| fallback to the own interface | closed | closed | open | C — organisation's queue |

Day 1, gRPC degraded: `new -D` → state 2, the organisation notices nothing. Day 2, leaving HC
for now: `existing -D` → state 3 (or, from state 1 in one step, `new -D -I all`, which avoids
the seconds in which messages would drop into the shared queue between two separate runs).
Day 5, healthy again: `new -E -I all` reopens both; a plain `-E` would also bring traffic back
to gRPC, since A is first, but would leave B closed and the chain not as provisioned.

## Design decisions

**One helper, one selector per command.** The existing function's body becomes
`_update_saf_enabled(session, request, preview, queue_filter, transaction_name)`. The old
command passes `is_hc_shared_queue` and keeps its transaction name, so its behaviour is
unchanged to the byte. The new command passes the selector for the requested interface; the
regex for an organisation's gRPC queue is strict to the three documented suffixes, test suffix
optional, case-insensitive as a defence.

**The option is named after what the operator chooses, not after an implementation detail.**
The first draft had a boolean `--with-shared`. "Shared" names *which queue happens to be
shared*; the operator is choosing *which interface's rules to touch*, and "interface" is the
word both the ticket and the page use. So: `-I/--interface grpc|bridge|all`, default `grpc`.
Three things fall out: the day-of-use story reads as the operator thinks (`-D`, `-D -I all`,
`-E -I all`); `-I bridge` is the old command's behaviour on the same code path, so the two can
never drift; and the console dialog gets an enumerated field, the way the file already presents
the environment choice. A declarative `--to grpc|bridge|own` was considered and rejected: it
departs from the "like the existing command" brief, and `--to grpc` would silently reopen a
bridge rule somebody closed on purpose.

**A coverage check, because the chain can be short.** An organisation that joined HC directly
on gRPC has no door C. `-D -I all` would close every rule it has and its messages would have
nowhere to go. Before writing, the command recomputes, per routing key (organisation, service,
receiver, sender, message type), whether any enabled rule remains once the selected rows are
disabled; if none would, it refuses — a warning under `--preview`, an error otherwise,
overridden by `--force`. One extra read per run; the existing command does not have the check
and was not given it in this change, so its behaviour stays identical.

**Deliberately left alone.** The real-time routing table (the page says the real-time impact is
not yet known); the provisioning side that creates queues and rules; the schema.

## What was and was not verified

Both Python files compile; the dialog XML parses; both patches apply cleanly to a private
checkout of the two repositories. No unit tests exist for the command being generalised and
none were added yet; nothing was run against a live database. Expect a trivial rebase onto the
current branch.

## Open questions for the product owner

1. Is "gRPC rules closed, shared rules open" a supported operational state? If not, `all`
   should be the default and the page's table is the only story.
2. Store-and-forward only, no real-time counterpart — correct?
3. The queue-name rule: exactly the three suffixes, or anything under `<orgcode6>_hostedconn*`?
4. Same runner groups as the existing command?

## Files

- [`18-fallback-switch-between-two-interfaces-service.patch`](18-fallback-switch-between-two-interfaces-service.patch)
  — the service: the generalised helper, the selectors, the coverage check, the new command.
- [`18-fallback-switch-between-two-interfaces-packaging.patch`](18-fallback-switch-between-two-interfaces-packaging.patch)
  — the packaging: wrapper script, package spec, console dialog, access-control list.
