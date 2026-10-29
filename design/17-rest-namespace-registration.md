# Review: one way to mount a REST namespace

A follow-up to [note 15](15-rest-facade-over-soap-service.md) (the review of the Link module's
REST facade) and [note 16](16-rest-facade-for-a-stateless-module.md) (the design for the
file-system module's). The author of the Link change asked the right question after reading the
second design: *"RESTRouter, RESTSubRouter and now RESTRootRouter — is my hierarchy wrong from
the beginning?"* This note answers it, makes the small fix that follows, and embeds the code.
Identifiers are illustrative.

## The question, answered honestly

Partly, yes — and the discomfort has a precise cause. Put the REST side next to the SOAP side
of the same agent, because the SOAP side got this right.

**SOAP.** The RPC module is *infrastructure*: it owns the shared HTTP server, binds endpoints,
attaches the session filter when asked. Every SOAP module — file system, Link, sessions — extends
one base class and mounts itself with one call:

```java
rpcServer.addEndpoint(this, "/FSModule", requireSession);
```

Modules are peers. "Needs a session or not" is a boolean on the mount, not a type.

**REST, as it grew.** `RESTModuleImpl` was built as the one and only REST module, so it is two
things fused: the *host* (contexts on the shared server, path parsing, the 500 catch-all, later
the request forwarder) and a *domain module* (sessions and processes). `AbstractRESTModule`,
which by its name should be the peer of the SOAP base class, hard-codes `/agent/admin` and
`/agent/sessions` in `load()` — so it can only ever have one subclass and cannot be the base for
another REST module. That was fine while there was one REST surface.

It stopped being fine the day the Link facade arrived — the first second REST module. The clean
move then was to pull the host out of `RESTModuleImpl` so the Link module could mount as a peer.
What happened instead was a registry *inside* the one module (`RESTSubRouter`, session-scoped),
and what the file-system design added was a *second* registry beside it (`RESTRootRouter`,
no session). So the SOAP side's one boolean had become two interfaces, two registries, two
registration method pairs, a reserved-names set to keep the core's own paths out of the way, and
a test seam that only existed because binding was tangled into the module. The naming the author
disliked is the surface of that: there is nothing to call those things cleanly because the
structure has no clean concept for them.

What was right from the start, so it is not thrown away: extracting `RESTRouter` as a generic
dispatcher; one thin facade per module; the session-explicit overloads on the domain classes.

## Two fixes, and which one now

**Small — before either branch merges.** Collapse the duality into the SOAP shape: one handler
type, one mount API, a flag. The Link facade registers with `true` (mounted under
`/agent/sessions/{token}/link`, session resolved by the host); the file-system facade with
`false` (mounted at `/agent/fs`). `RESTSubRouter`, `RESTRootRouter`, both registries and both
method pairs disappear; `RESTRouter` keeps its name because it is now the only thing that routes.
It mirrors `addEndpoint(handler, path, requireSession)` exactly, which is also the argument that
wins the review.

> **Corrected 2026-10-29.** The file-system module's SOAP endpoint turned out to be published with
> `requireSession = true` as well, so its facade now also registers with `true`; see the follow-up
> in [note 16](16-rest-facade-for-a-stateless-module.md). The `false` kind stays in the registry,
> tested, with no user yet.

**Right — its own ticket, after.** Finish the SOAP symmetry: extract a `RESTServer` (the REST
twin of the RPC server) that owns contexts, dispatch, the forwarder and the catch-all; make
`AbstractRESTModule` a real base — constructor takes the namespace and `requireSession`,
`load()` mounts, `unload()` unmounts — and split `RESTModuleImpl` into the peers it already
contains: a sessions module, a processes module, an admin module. Then every REST module,
including the core ones, is a few hundred lines of routes and nothing else.

The small one goes in now because the Link branch has been through seven review rounds; a
structural refactor inside it would restart the review for a change nobody asked for. The small
fix changes the API the reviewers have already read in exactly one place and makes it *smaller*.

## The change

Two commits, so each branch gets what it needs: the first is pure infrastructure and applies to
the Link branch as a follow-up; the second is the file-system feature from note 16 rebased on it.
Patches: [`17-rest-namespace-registration.patch`](17-rest-namespace-registration.patch) and
[`16-rest-facade-for-a-stateless-module.patch`](16-rest-facade-for-a-stateless-module.patch).

### 1. One handler type — `rest/RESTNamespaceHandler.java` (new; replaces two interfaces)

```java
package io.github.eipx.remoteagent.rest;

import com.sun.net.httpserver.HttpExchange;
import io.github.eipx.remoteagent.http.Session;

/**
 * A domain module's REST entry point, registered under a namespace of the
 * shared REST endpoint with
 * {@code RESTModuleImpl.registerNamespace(namespace, handler, requireSession)}.
 * <p>
 * This is the REST counterpart of {@code SOAPRPC.addEndpoint(implementor,
 * path, requireSession)}: where a SOAP module publishes a JAX-WS endpoint on
 * the shared RPC server, a REST module registers a namespace handler on the
 * shared REST root resource, and the same boolean decides whether a session
 * is required.
 * <ul>
 *   <li>{@code requireSession = true}: the namespace is mounted under
 *       {@code /agent/sessions/{sessionToken}/{namespace}/...}; the core
 *       resolves the session token once and passes the {@link Session}
 *       (never {@code null}).</li>
 *   <li>{@code requireSession = false}: the namespace is mounted as its own
 *       HTTP context, {@code /agent/{namespace}/...}; {@code session} is
 *       {@code null}.</li>
 * </ul>
 * In both cases {@code segments} are the path segments <strong>relative to
 * the namespace</strong>.
 */
@FunctionalInterface
public interface RESTNamespaceHandler
{
    void handle(HttpExchange exchange, Session session, String[] segments) throws Throwable;
}
```

`RESTSubRouter` and `RESTRootRouter` are deleted. The signature is the one the session-scoped
interface already had, so the Link facade's handler body does not change at all.

### 2. One registry — `RESTModuleImpl` (replaces both registries)

```java
    /**
     * Prefix under which namespaces registered without a session are bound
     * as their own HTTP context: {@code /agent/{namespace}}.
     */
    public static final String ROOT_CONTEXT_PREFIX = "/agent/";

    /**
     * Namespace names the core REST module uses itself: {@code processes} is
     * a core route under a session; {@code sessions} and {@code admin} are
     * the core's own root contexts. Neither kind may be registered.
     */
    private static final Set<String> RESERVED_SESSION_NAMESPACES = new HashSet<String>(Collections.singletonList("processes"));
    private static final Set<String> RESERVED_ROOT_NAMESPACES    = new HashSet<String>(Arrays.asList("sessions", "admin"));

    /**
     * A registered namespace: its handler, whether a session is required and,
     * for namespaces mounted as their own context, the bound HTTP context.
     */
    private static final class Mount
    {
        final RESTNamespaceHandler handler;
        final boolean requireSession;
        volatile HttpContext context;

        Mount(final RESTNamespaceHandler handler, final boolean requireSession)
        {
            this.handler = handler;
            this.requireSession = requireSession;
        }
    }

    /**
     * Namespaces registered by other modules (e.g. {@code "link"},
     * {@code "fs"}), keyed by name. One name is one mount, of either kind.
     */
    private final ConcurrentMap<String, Mount> mounts = new ConcurrentHashMap<String, Mount>();
```

```java
    /**
     * Registers a domain module's REST entry point under a namespace — the
     * REST counterpart of {@code SOAPRPC.addEndpoint(implementor, path,
     * requireSession)}.
     * <p>
     * With {@code requireSession}, requests matching
     * {@code /agent/sessions/{sessionToken}/{namespace}/...} that are not
     * handled by a core route are delegated to the handler with the resolved
     * session and the path segments relative to the namespace. Without it,
     * {@code /agent/{namespace}} is bound as its own HTTP context on the shared
     * server and requests under it are delegated with a {@code null} session.
     *
     * @return {@code true} if the namespace was registered, {@code false} if
     *         the name is invalid, reserved, already registered by either
     *         kind, or its context is already bound by another handler.
     */
    public boolean registerNamespace(final String namespace, final RESTNamespaceHandler handler, final boolean requireSession)
    {
        Logging.Entry(logger, this, namespace, handler, requireSession);

        Set<String> reserved = requireSession ? RESERVED_SESSION_NAMESPACES : RESERVED_ROOT_NAMESPACES;
        if(namespace == null || namespace.isEmpty() || namespace.contains("/") || "*".equals(namespace)
                || reserved.contains(namespace) || handler == null)
        {
            logger.warn(String.format("Invalid REST namespace: %s", namespace));
            return Logging.Exit(logger, this, false);
        }

        Mount mount = new Mount(handler, requireSession);
        if(mounts.putIfAbsent(namespace, mount) != null)
        {
            logger.warn(String.format("REST namespace already registered: %s", namespace));
            return Logging.Exit(logger, this, false);
        }

        if(!requireSession)
        {
            try
            {
                mount.context = addNamespaceContext(namespace);
            }
            catch(IllegalArgumentException e)   // HttpServer: path already bound by another handler
            {
                mounts.remove(namespace);
                logger.error(String.format("Unable to bind REST namespace context: %s", namespace), e);
                return Logging.Exit(logger, this, false);
            }
        }

        logger.debug(String.format("Registered REST namespace: %s (%s, requireSession=%s)", namespace, handler, requireSession));
        return Logging.Exit(logger, this, true);
    }

    public boolean unregisterNamespace(final String namespace)
    {
        Logging.Entry(logger, this, namespace);
        Mount mount = (namespace != null) ? mounts.remove(namespace) : null;
        if(mount != null && mount.context != null) removeNamespaceContext(mount.context);
        return Logging.Exit(logger, this, mount != null);
    }

    /** Kept separate so that tests can stub the server binding. */
    protected HttpContext addNamespaceContext(final String namespace)
    {
        return addContext(ROOT_CONTEXT_PREFIX + namespace);
    }

    protected void removeNamespaceContext(final HttpContext httpContext)
    {
        removeContext(httpContext);
    }
```

Dispatch is a three-way split on the bound context path, and both delegation sites read the
same map:

```java
                if(ADMIN_CONTEXT_PATH.equalsIgnoreCase(requestContext))        handleAdminRequest(exchange, method, segments);
                else if(SESSION_CONTEXT_PATH.equalsIgnoreCase(requestContext)) handleSessionRequest(exchange, method, segments);
                else                                                           handleRootRequest(exchange, requestContext, segments);
```

```java
        // in handleSessionRequest, after the core routes did not match:
        if(segments.length >= 2)
        {
            Mount mount = mounts.get(segments[1]);
            if(mount != null && mount.requireSession)
            {
                Session session = resolveSession(exchange, segments[0]);
                if(session == null) { Logging.Exit(logger, this); return; }
                mount.handler.handle(exchange, session, Arrays.copyOfRange(segments, 2, segments.length));
                Logging.Exit(logger, this);
                return;
            }
        }
```

```java
    private void handleRootRequest(final HttpExchange exchange, final String contextPath, final String[] segments) throws Throwable
    {
        Logging.Entry(logger, this, exchange, contextPath, segments);
        Mount mount = (contextPath != null && contextPath.startsWith(ROOT_CONTEXT_PREFIX))
                ? mounts.get(contextPath.substring(ROOT_CONTEXT_PREFIX.length())) : null;
        if(mount == null || mount.requireSession) RESTUtil.sendNotFound(exchange);
        else                                      mount.handler.handle(exchange, null, segments);
        Logging.Exit(logger, this);
    }
```

`unload()` unregisters every remaining mount, so a dependent module unloaded out of order cannot
leave a dangling context.

### 3. The two facades — one line each

```java
// LinkRESTModuleImpl (session-scoped)
public class LinkRESTModuleImpl extends AbstractStoppableModule implements BaseModule, RESTNamespaceHandler
...
            result = restModuleImpl.registerNamespace(LINK_NAMESPACE, this, true);

// FSRESTModuleImpl (no session)
public class FSRESTModuleImpl extends AbstractStoppableModule implements BaseModule, RESTNamespaceHandler
...
            result = restModuleImpl.registerNamespace(FS_NAMESPACE, this, false);
...
    @Override
    public void handle(final HttpExchange exchange, final Session session, final String[] segments) throws Throwable
    // session is always null here and is not used
```

_Corrected 2026-10-29: the file-system facade now registers with `true` too; see note 16's follow-up._

Everything else in both facades — routes, handlers, status mapping — is untouched. The
file-system design in note 16 is otherwise unchanged; its code blocks were updated to these
names.

### 4. Registry tests — `RESTModuleImplNamespaceTest` (replaces the root-router test)

```java
public class RESTModuleImplNamespaceTest
{
    private RESTModuleImpl module;
    private final RESTNamespaceHandler handler = (exchange, session, segments) -> {};
    private final List<String> bound = new ArrayList<String>();
    private final List<HttpContext> unbound = new ArrayList<HttpContext>();

    @Before
    public void setUp()
    {
        module = new RESTModuleImpl()
        {
            @Override protected HttpContext addNamespaceContext(final String namespace)
            { bound.add(namespace); return mock(HttpContext.class); }
            @Override protected void removeNamespaceContext(final HttpContext httpContext)
            { unbound.add(httpContext); }
        };
    }

    @Test public void registersSessionScopedNamespaceWithoutBindingAContext()
    { assertTrue(module.registerNamespace("link", handler, true)); assertTrue(bound.isEmpty()); }

    @Test public void registersRootNamespaceAndBindsItsContext()
    { assertTrue(module.registerNamespace("fs", handler, false)); assertEquals("fs", bound.get(0)); }

    @Test public void rejectsReservedSessionScopedNamespace()
    { assertFalse(module.registerNamespace("processes", handler, true)); }

    @Test public void rejectsReservedRootNamespaces()
    { assertFalse(module.registerNamespace("sessions", handler, false)); assertFalse(module.registerNamespace("admin", handler, false)); }

    @Test public void rejectsMalformedNamespaces()
    { assertFalse(module.registerNamespace(null, handler, false)); assertFalse(module.registerNamespace("", handler, true));
      assertFalse(module.registerNamespace("a/b", handler, false)); assertFalse(module.registerNamespace("*", handler, true)); }

    @Test public void rejectsNullHandler()
    { assertFalse(module.registerNamespace("fs", null, false)); }

    @Test public void rejectsDuplicateRegistrationAcrossKinds()
    { assertTrue(module.registerNamespace("x", handler, true));
      assertFalse(module.registerNamespace("x", handler, false)); assertFalse(module.registerNamespace("x", handler, true)); }

    @Test public void unregisterRemovesTheContextOfARootNamespace()
    { assertTrue(module.registerNamespace("fs", handler, false)); assertTrue(module.unregisterNamespace("fs")); assertEquals(1, unbound.size()); }

    @Test public void unregisterOfASessionScopedNamespaceUnbindsNothing()
    { assertTrue(module.registerNamespace("link", handler, true)); assertTrue(module.unregisterNamespace("link")); assertTrue(unbound.isEmpty()); }

    @Test public void unregisterFreesTheNameForEitherKind()
    { assertTrue(module.registerNamespace("x", handler, false)); assertTrue(module.unregisterNamespace("x")); assertTrue(module.registerNamespace("x", handler, true)); }

    @Test public void unregisterUnknownReturnsFalse()
    { assertFalse(module.unregisterNamespace("never-registered")); assertFalse(module.unregisterNamespace(null)); }
}
```

(The published patch has the same cases in the project's usual one-statement-per-line style.)

### 5. Also in the infrastructure commit

Two generic fixes that the file-system design needed and the Link facade benefits from, moved
here so the feature commit is purely the feature: the core now decodes the raw query string
exactly once (`getRequestURI().getQuery()` is already percent-decoded by `java.net.URI`, so the
old code decoded twice and corrupted `+` and `%` in values); and a shared `sendBinaryResponse`
that sends an empty payload with no body rather than a zero length, which the Link facade now
uses instead of its private copy. The module page of the Link facade and the explorer wording
were updated to the new names.

## Verification

- Compiled with `javac` against the real project sources, third-party APIs stubbed: zero errors.
- Executed, not just written: the file-system facade tests (35), the registry tests (11) and
  the Link facade's existing tests (25) all run against the unified API: **70 passed, 0 failed,
  1 skipped** (an unreadable-file case the host OS cannot simulate).
- The restored infrastructure patch applies cleanly to the Link branch, and the restored feature
  patch applies cleanly on top of it, checked in a throwaway worktree.
- Not done: the project's own build pipeline; a run against a live agent.

## What this does not fix, deliberately

The host and the sessions/processes module are still one class, `AbstractRESTModule` still
hard-codes two contexts, and the reserved-names check still exists because the core's own
routes live in the same namespace space as everyone else's. Those are the symptoms of the fused
design, and they go away only with the extraction described above. The small fix is honest
about that: it makes the public API match the SOAP side today, and leaves the structural debt
named and ticketed rather than hidden behind better names.

### One lesson worth keeping

When a second instance of something arrives, the question is not "how do I register it with the
first one" but "what is the thing both of them are instances of". The Link facade was the second
REST module; the answer was "a REST module", and the host should have been extracted then. Doing
it later costs a review round for every module that mounted in between.
