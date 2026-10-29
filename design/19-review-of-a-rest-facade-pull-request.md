# Review: the stateless-module REST facade, as a pull request

The design in [note 16](16-rest-facade-for-a-stateless-module.md) came back as a pull request on
the same Java agent service ("Remote Agent"): the author had ported the first published version by
hand onto the branch where the Link facade of [note 15](15-rest-facade-over-soap-service.md) had
just been merged, with its own test, its own explorer cards and its own module page. This is the
review of that pull request, written against the actual commit rather than screenshots, with the
requested changes attached as a diff
([`16-rest-facade-for-a-stateless-module.follow-up.patch`](16-rest-facade-for-a-stateless-module.follow-up.patch),
11 files, applies to the commit as is). Identifiers are illustrative.

## Verdict

**Approve after changes.** The shape is right: a REST facade in front of an unchanged domain module,
one registry for both kinds of namespace, verbs chosen by idempotency, an honest mapping of the
domain's quiet failures to status codes, a bounded upload. Five things must change before merge, and
the attached diff does all five. Nothing else in the diff is a matter of taste.

## Must change before merge

1. **Session semantics.** The file-system module's SOAP endpoint is published with
   `requireSession = true`: its constructor passes the flag to the RPC base class, which puts the
   session filter on the SOAP context, and a call without a valid session cookie gets 401. The
   Python test-framework wrapper opens a session before its first file call for exactly that reason.
   The pull request registers the REST namespace with `false` and says "not owned by a session" in
   the class Javadoc, the module page, the domain module's page and the explorer. That is a
   transport policy change the pull request does not announce, resting on a documentation line
   ("Session Required: NO") that was wrong at the source. Parity is the defensible choice: register
   with `true`, mount under `/agent/sessions/{sessionToken}/fs`. Sessions are a lifecycle mechanism,
   not a security control (anyone can open one), so nothing is lost either way; but a REST surface
   more permissive than the SOAP surface for the same module needs a written justification, and
   parity needs none. With the unified API this is one argument plus its consequences: the
   `Location` header, `getStatus()`, the Javadoc, the cards, the endpoint map, the pages. If the
   team prefers the no-session variant, record the decision in the pull request and keep the
   root-namespace kind in the registry either way; it is tested.

2. **The explorer cards are dead.** Nine cards were added to the API page, but the explorer's
   `ENDPOINTS` map has no entries for them. The Execute handler returns when a card's id is not in
   the map, so every card renders and does nothing, and the curl preview stays empty. The module
   page is also missing from the sidebar list, and the cards have no `sessionToken` field, which
   parity requires.

3. **Configuration reference.** `FSRESTModuleImpl.maxFileSize` is new, but neither the reference
   generator's manual list nor the generated reference knows it, so the only knob this module has is
   undocumented. The Link pull request hit the same gap with its three properties; the fix is the
   same one-line `addManual` entry.

4. **Proofreading leftovers in the facade.** The class Javadoc has a sentence spliced from two
   drafts (`...boolean)} with` / `than a session-scoped sub-router, and delegates`), the author tag
   is a placeholder, and the `@param session` text differs from the interface's. A reviewer sees the
   placeholder first and reads the rest less charitably.

5. **Tests prove routing, not behaviour.** The pull request's test (17 cases) mocks the domain
   module and asserts status codes only. The facade's actual logic is the status mapping, and the
   inputs to that mapping (`false`, `null`, `-1`) come from `java.io.File`, which a mock cannot
   exercise: no 403 (unreadable or unwritable), no 409 (parent missing, non-empty directory,
   destination occupied), no 413 (declared or streamed), no 400 for a bad `offset`/`length`, a
   malformed `Content-Length` or bad JSON, no `Location` check, no body-in-small-chunks case (the
   single-read bug the design fixed), no `+`/`%` path (the raw-query fix), no 405. Two cases are also
   Unix-only: the write tests mock `directoryExists("/tmp")` while the facade asks for
   `new File(path).getParent()`, which is `\tmp` on Windows, so they fail on a Windows developer
   machine (51 of 53 there) and pass only on the Linux build host. The replacement test runs the real
   domain module on a `TemporaryFolder`: 37 cases, a superset of the 17, two of which pin the
   session registration through a mocked module manager. It compiles against the project's JUnit
   4.7.

## Design notes: no change required, say them in the pull request

- **One pull request, two changes.** The infrastructure refactor (delete `RESTSubRouter`, add
  `RESTNamespaceHandler`, one registry with a `requireSession` flag, the raw-query fix and the shared
  binary sender in `RESTUtil`, `removeContext` made protected) ships together with the feature and
  touches the Link facade merged three days earlier. Acceptable, but the description must call out
  the API change: anyone on `RESTSubRouter`/`registerSubRouter` breaks at compile time. Two commits
  (infrastructure, feature) would bisect better.
- **Opt-in versus default-on.** The properties file keeps the facade opt-in, like the Link facade.
  Consistent, and fine; the alternative (default-on, since both of its dependencies are default
  modules) would make the explorer cards work out of the box. Whichever is chosen, the explorer must
  say the routes answer only where the module is enabled; the attached intro paragraph does.
- **Structural debt unchanged.** The REST host and the sessions/processes module are still one
  class, the abstract base still hard-codes two contexts, and the reserved-name check exists because
  core routes share the namespace space ([note 17](17-rest-namespace-registration.md)). This pull
  request makes none of it worse; the extraction of a REST server twin of the SOAP one is the
  follow-up ticket.
- **A domain quirk worth a line in the module page, not a change here.** The domain's windowed read
  performs a single `read()` of up to `length` bytes, so a windowed REST read may return fewer bytes
  than asked; `Content-Length` tells the client. And the whole-file read returns `null` for an empty
  file, the same value as "unreadable"; the facade checks existence and readability first, so it
  maps that to an empty 200, which is right.

## Code quality

**Good, keep.** Entry and exit logging on every method including helpers; dependencies declared in
the constructor and checked in `load()`; `unload()` unregisters; the 404/403/409/413 split with
messages that say which precondition failed; the declared-length precheck plus a read-to-end-of-stream
loop; malformed `Content-Length` as 400; 201 versus 200 on create versus overwrite; `Location` on
copy; the size bound read through the configuration class, with a Javadoc that says why the bound
exists; the split between reserved session names and reserved root names; `putIfAbsent` plus
rollback when the context bind throws; `unload()` sweeping mounts left behind by dependents;
`volatile` on the mount's context; the shared binary sender sending `-1` for an empty body (the JDK
server would otherwise emit a chunked zero-length response); the Link facade's private sender
replaced by the shared one.

**Fix, all in the attached diff.**

| Where | What |
|---|---|
| `FSRESTModuleImpl` | Javadoc splice, placeholder author, `@param session` wording; the `Session` import sits between the JDK and logging groups instead of with the project's own imports; 404s carry no message although the core's 404s were given messages in the previous pull request's review round ("Session not found") |
| `RESTModuleImpl` | the handler import placed where the deleted interface's was, breaking the sorted block; the Javadoc route table lists the root path |
| `LinkRESTModuleImpl` | `load()` still logs "sub-router namespace" |
| domain module page | "Creates a directory (and parent directories)" is wrong (`File.mkdir`); "Session Required: NO" is wrong; the new REST paragraph repeats the no-session premise |
| facade page | repeats the premise; no Activation section although the module is opt-in; "Session Scope: None" |
| API page | no section intro (session, opt-in); no `sessionToken` field; root paths |
| properties file | the comment should say where the routes live and that a session is required |

**Nits, not in the diff.** The source/destination helper catches `Exception` around the JSON parse
(acceptable: any parse failure is a 400); the card descriptions are terser than the Link ones;
`handleStat` reports `readable` for files only (documented).

## What the attached diff changes

- `FSRESTModuleImpl`: `registerNamespace(FS_NAMESPACE, this, true)`; the route table becomes
  `Route<Session>` so the copy handler receives the session and builds `Location` from the session
  context path and the encoded session id; `getStatus()`, the class Javadoc and its route table
  follow; the Javadoc defects fixed; imports regrouped; six 404s name the missing entry.
- The facade test: replaced by the real-file-system test, 37 cases.
- `RESTModuleImpl`: sorted imports; the Javadoc table line.
- `LinkRESTModuleImpl`: the log message.
- The explorer page: a sidebar entry; nine `ENDPOINTS` entries keyed by the pull request's card ids,
  with `{sessionToken}` paths, `?path={path}` templates, `offset`/`length` as optional query, and
  `application/octet-stream` for the upload.
- The API page: an intro paragraph; session-scoped paths in comments, path spans and summary rows;
  a `sessionToken` field in each card.
- The reference generator and the generated reference: `FSRESTModuleImpl.maxFileSize`.
- The domain module's page: Session Required YES with the reason; the REST line and paragraph
  corrected; the `mkdir` row corrected.
- The facade's page: namespace, Session Required YES, description, an Activation section, table
  paths, summary.
- The properties file: the comment.

Not changed, deliberately: opt-in stays opt-in; the registry keeps the no-session kind;
`RESTSubRouter` stays deleted.

## Evidence

- Baseline at the pull request's commit, compiled with `javac` against the real sources
  (third-party APIs stubbed): the pull request's 17 facade cases, the 11 registry cases and the 25
  Link cases ran; 51 passed, 2 failed (the Unix-only write cases, on Windows).
- Patched tree: 151 classes, zero errors; 72 passed, 0 failed, 1 skipped (the unreadable-file case,
  which Windows cannot simulate).
- The attached diff applies cleanly on the commit in a throwaway worktree. Not built through the
  project's own pipeline; not run against a live agent.

## Suggested pull request description

> Adds a REST entry point for the file-system module and, underneath it, replaces the two REST
> handler interfaces with one. `RESTSubRouter`/`registerSubRouter` are gone; modules implement
> `RESTNamespaceHandler` and call `registerNamespace(name, handler, requireSession)`, the REST twin
> of the SOAP server's `addEndpoint(impl, path, requireSession)`. The Link facade is switched over
> (one line); its tests are unchanged. `RESTUtil` gains the shared binary sender and a fix for
> double-decoded query strings (`+` and `%` in a path were corrupted).
>
> The file-system namespace is session-scoped, like its SOAP endpoint (the module is published with
> `requireSession = true`); the module page said otherwise and is corrected. Paths travel as the
> `path` query parameter; verbs follow idempotency; the domain's quiet failures map to 404/403/409;
> uploads and whole-file reads are bounded by `FSRESTModuleImpl.maxFileSize` (64 MiB; reads can
> page). The module is opt-in in the properties file, as the Link facade is.
>
> Tests: registry (11), facade against the real module on a temporary directory (37), Link (25).
> Follow-up ticket: extract the REST host from `RESTModuleImpl` so REST modules can be plain
> subclasses of a real abstract base.

### One lesson worth keeping

Review the commit, not the screenshot. Three rounds of this review ran on photographs of a diff and
produced patches against reconstructions of the author's tree; the fourth ran on the repository
itself and produced the exact diff in one pass. When the source can be made available, insist on it
before writing a line.
