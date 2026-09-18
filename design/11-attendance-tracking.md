# Attendance tracking for hourly staff

## Requirements
The prompt as given is intentionally thin (track clock-in and clock-out for hourly staff), so
start with what to ask before designing anything.

Clarifying questions:
1. How many sites and how many employees per site, and what is the peak clock-in rate (shift
start at a large site can be a real burst)?
2. Do devices ever operate offline, and for how long, before syncing?
3. Who can correct a record, and does a correction need to be visible to the employee, or
only to payroll?
4. What payroll cycle and export format does this feed, and how far back can a late
correction still affect an already-run payroll?
5. Are sites in different time zones, and does a shift ever cross midnight?

Assumptions made to proceed (state these explicitly when nobody answers): several hundred
sites, tens of thousands of employees, devices can be offline for hours and buffer events
locally, corrections are supervisor-only and must be auditable, payroll runs weekly with a
cutoff, and shifts can cross midnight.

Non-functional: ingestion must accept a burst of events at shift change without dropping any,
duplicate or out-of-order events from an offline-buffered device must not double-count a
shift, and the payroll export for a given period must be reproducible (running it twice
produces the same numbers unless a correction was made in between).

## Interfaces
- `POST /events {employeeId, siteId, type: CLOCK_IN|CLOCK_OUT, deviceTimestamp,
deviceEventId}` from a device, possibly replayed after an offline period.
- `POST /corrections {employeeId, shiftId, newClockIn, newClockOut, supervisorId, reason}`.
- `GET /payroll-export?period=...` -> finalized hours per employee for the period.

## Data model
Store an append-only event log, not a mutable current-shift row: `AttendanceEvent {eventId,
employeeId, siteId, type, deviceTimestamp, receivedTimestamp, deviceEventId}`. A shift is
derived by pairing a clock-in with the next clock-out for that employee. Corrections are
their own event type, `CORRECTION {shiftId, field, oldValue, newValue, supervisorId, reason,
appliedAt}`, layered on top rather than overwriting the original event, so the audit trail is
the log itself.

## Options
1. Mutable current-shift record, updated in place on clock-out. Simple to read, but a late or
duplicate event has nowhere to go except overwrite history, which destroys the audit trail
supervisors and payroll disputes need.
2. Event log with derived shifts, computed by a batch or streaming process that pairs events
per employee in timestamp order. Naturally idempotent (the same event applied twice is
deduplicated by `deviceEventId`), naturally auditable (nothing is ever overwritten), at the
cost of needing a derivation step before the data is queryable as shifts.

Pick the event log. Payroll and disputes need history more than they need the simplest
possible read path, and the derivation process is a well-understood batch or stream pattern.

## Where the state lives
Raw events live in an append-only log or wide-column table keyed by employee and time,
immutable once written. Derived shift and payroll views are materialized from the log and can
be recomputed from scratch if a bug is found in the derivation logic, since the log itself is
the source of truth.

## Concurrency and consistency
Idempotent ingestion: `deviceEventId` (generated on the device) is the dedup key; a replayed
event with the same ID is a no-op, not a duplicate shift. Late and out-of-order events: order
by `deviceTimestamp`, not `receivedTimestamp`, and let the derivation process reprocess a
recent window (say, the last 48 hours) whenever an event arrives late, rather than assuming
events arrive in order. Corrections take effect by appending a correction event; the derived
shift view reflects the correction plus the original as two log entries, so an auditor can
see what changed and who changed it, not just the final number.

## Failure handling
A device buffers events offline and replays them hours later: the dedup key handles exact
duplicates, and the reprocessing window handles the case where a shift's derived total needs
to be recomputed after the fact. A correction made after payroll has already run for that
period: never mutate a finalized export; a correction after cutoff produces an adjustment in
the next cycle, with a clear trail linking it back to the original period.

## Scaling
Partition the event log by employee ID or by site plus day, so a shift-change burst at one
site does not contend with writes at another. The derivation process handles per-employee (or
per-site) partitions independently and can be scaled out horizontally; the payroll export is
a batch read over a bounded time window, not a hot-path query.

## Operations
Metrics: event ingestion rate and lag, percentage of events arriving out of order or late,
correction rate per site (a spike suggests a broken device or a training gap), payroll export
reconciliation (derived hours versus a manual spot check). Alarms: ingestion lag beyond the
reprocessing window (late events would be missed), a site with an abnormal correction rate.
Rollout: run the new derivation logic alongside the old system for a full payroll cycle and
diff the two exports before cutting over.

## Follow-up questions
1. What happens if a device's clock is wrong? Sanity-check `deviceTimestamp` against
`receivedTimestamp` bounds, and flag (not silently accept) events far outside a plausible
range for manual review.
2. How do you handle a missing clock-out (employee forgot, device died)? Derive an open
shift, surface it to the supervisor for correction before payroll cutoff, and never silently
auto-close it without a record of the assumption made.
3. How do time zones interact with a shift that crosses midnight? Store timestamps in UTC
with the site's time zone as metadata, and define the shift date by the site's local calendar
day at clock-in, not by UTC date.
4. Why not just let supervisors edit the record directly? Direct edits erase the original
value; an audit trail requires the original and the correction to both exist, which means
corrections are new events, not updates.
5. How do you keep payroll exports reproducible? Freeze the input window at a cutoff
timestamp per period and never let the export read events that arrived after that cutoff,
even if they claim to belong to that period.
6. What is the reprocessing window and why bound it? Bound it (48 hours, a week) so a very
late event does not force reprocessing the entire history; anything later than the bound
becomes a correction instead of a re-derivation.
