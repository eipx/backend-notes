# Design: a REST facade for a stateless module

A design for the next change to the same Java agent service ("Remote Agent") reviewed in
[note 15](15-rest-facade-over-soap-service.md). That change gave the Link module a REST entry
point beside its SOAP (JAX-WS) one. This one does the same for the file-system module, and the
interesting part is that the obvious move — copy the Link pattern — would be wrong. Identifiers
are illustrative.

> **Revised the same day, after [note 17](17-rest-namespace-registration.md).** The first version
> of this design added a second handler interface (`RESTRootRouter`) beside the Link facade's
> `RESTSubRouter`. Note 17 explains why that was the wrong shape and unifies both into one
> `RESTNamespaceHandler` and one `registerNamespace(name, handler, requireSession)` call, mirroring
> the SOAP side's `addEndpoint(..., requireSession)`. The code below is the unified version; the
> design decisions are unchanged.

## What exists

- The agent's REST core (`RESTModuleImpl`) binds two HTTP contexts on the shared server,
  `/agent/sessions` and `/agent/admin`, and dispatches with a small generic router
  (`RESTRouter`). Modules whose resources are owned by a session register a `RESTSubRouter`
  under `/agent/sessions/{token}/{namespace}/...`; the core resolves the session once and
  delegates. The Link module uses this.
- `FSModuleImpl` is the SOAP file-system module: twelve operations on path strings —
  `makeDirectory`, `removeDirectory`, `directoryExists`, `listDirectory`, `readFile(path,
  length, offset)`, `readEntireFile`, `writeFile(path, bytes)`, `moveFile`, `copyFile`,
  `removeFile`, `fileExists`, `fileSize`. It extends the plain RPC base class, not the
  sessioned one: no call takes or resolves a session, and nothing is owned by one.
- Its error convention is "quiet": `false` for a precondition that did not hold (not a file,
  not writable, already exists), `null` for an unreadable or absent file or directory, `-1`
  for the size of an unreadable file; I/O failures throw and the SOAP layer turns them into
  faults.

## Three decisions that differ from the Link change

**1. No session.** Mounting file operations under `/agent/sessions/{token}/fs/...` would force
every client to open a session it does not need, and would invent ownership where the domain
has none. The namespace must be root-level: `/agent/fs/...`. That needs one small core
addition — the namespace registry gains a `requireSession` flag, and a namespace registered
without one is bound as its own HTTP context at `/agent/{namespace}`. Because the core strips the bound context before
splitting path segments, handlers see clean segments; and because the new context is handled
by the same `handle()` method, the request-forwarding header (`X-Forward-To`) and the 500
catch-all cover it for free. **No session-explicit overloads, and no change to `FSModuleImpl`
at all.**

**2. The path is a query parameter, URL-encoded.** Paths are opaque operating-system strings
(drive letters and backslashes on one platform, slashes on the other), so putting them in the
URL path is ambiguous and ugly; `?path=` matches SOAP's "a path string" exactly. This exposes a
pre-existing bug worth fixing in the same change: the core builds query parameters from
`getRequestURI().getQuery()` — which `java.net.URI` has *already* percent-decoded — and then
decodes again. A `+` or `%` in a file path would be corrupted, and a `%26` inside a value would
split the query. One token fixes it: `getRawQuery()`. Existing routes pass only numbers and
booleans, so nothing else changes.

**3. Methods chosen by safety and idempotency up front.** Reads and existence checks are GET.
Full overwrite and "make directory" are PUT (repeating them changes nothing). Deletes are
DELETE. Copy and move are POST actions, because repeating a copy can produce a different
outcome. (Note 15's follow-up found two dequeue operations wrongly exposed as GET; this time
the table was checked before the code.)

## Routes and status mapping

| Method | Path | Domain call | Success | Failure mapping |
|---|---|---|---|---|
| GET | `/agent/fs/entries?path=` | `fileExists`, `directoryExists`, `fileSize` | 200 `{path, exists, type, size}` | never 404: `exists:false` is an answer, as SOAP's `false` is |
| GET | `/agent/fs/files?path=[&offset=&length=]` | `readEntireFile` / `readFile` | 200 octet-stream | 404 not a file; 403 not readable; 413 larger than `maxFileSize` without `length` |
| PUT | `/agent/fs/files?path=` (octet-stream body) | `writeFile` | 201 created, 200 overwritten | 409 parent directory missing; 403 existing file not writable; 413 body too large |
| DELETE | `/agent/fs/files?path=` | `removeFile` | 200 | 404 not a file; 403 not writable |
| POST | `/agent/fs/files/copy` `{source, destination}` | `copyFile` | 201 + `Location` | 400 bad body; 404 source missing; 409 destination not writable |
| POST | `/agent/fs/files/move` `{source, destination}` | `moveFile` | 200 | as copy; also moves directories, as the domain does |
| GET | `/agent/fs/directories?path=` | `listDirectory` | 200 `{path, entries[]}` | 404 not a readable directory |
| PUT | `/agent/fs/directories?path=` | `makeDirectory` | 201 created, 200 already present | 409 parent missing or not writable |
| DELETE | `/agent/fs/directories?path=` | `removeDirectory` | 200 | 404 not a directory; 409 not empty or not writable |

Success bodies are `200 {"success": true}` where there is nothing else to return. `204` is the
textbook answer, but the two existing namespaces return `200` with a JSON body, and a surface
with three namespaces should not have one odd one out.

Two domain quirks the facade has to absorb, both found by reading the source rather than the
interface:

- The shared `readInputStream(stream)` helper returns `null` for an *empty* stream. So an
  empty file reads as `null`, exactly like a missing one. The read handler therefore checks
  existence first and treats `null` as "nothing to return", not "not found".
- `makeDirectory` returns `false` both for "already a directory" and for "mkdir failed", so
  the facade checks `directoryExists` first to tell 200 from 409. (The module page also says
  parents are created; the code calls `mkdir`, not `mkdirs`. Fix the page; do not change SOAP
  behaviour in a transport change.)

## The change, file by file

### 1. `rest/RESTNamespaceHandler.java` — one handler type for every REST module

```java
package io.github.eipx.remoteagent.rest;

import com.sun.net.httpserver.HttpExchange;
import io.github.eipx.remoteagent.http.Session;

/**
 * A domain module's REST entry point, registered under a namespace of the
 * shared REST endpoint with
 * {@code RESTModuleImpl.registerNamespace(namespace, handler, requireSession)}
 * — the REST counterpart of {@code SOAPRPC.addEndpoint(implementor, path,
 * requireSession)}. With {@code requireSession} the namespace is mounted under
 * {@code /agent/sessions/{sessionToken}/{namespace}/...} and the resolved
 * {@link Session} is passed (never {@code null}); without it the namespace is
 * bound as its own HTTP context, {@code /agent/{namespace}/...}, and
 * {@code session} is {@code null}. {@code segments} are relative to the
 * namespace in both cases.
 */
@FunctionalInterface
public interface RESTNamespaceHandler
{
    void handle(HttpExchange exchange, Session session, String[] segments) throws Throwable;
}
```

This replaces the Link facade's `RESTSubRouter` (same signature, so its handler body is
unchanged) and the `RESTRootRouter` the first draft of this note proposed.

### 2. `rest/RESTUtil.java` — fix double decoding, add a binary sender

```java
-            result = HTTPUtil.parseURLParameters(exchange.getRequestURI().getQuery());
+            // getQuery() is already percent-decoded by java.net.URI; decoding it again in
+            // parseURLParameters corrupts '+' and '%' in values (e.g. file paths).
+            result = HTTPUtil.parseURLParameters(exchange.getRequestURI().getRawQuery());
```

```java
+    /**
+     * Sends raw bytes with the given content type. An empty payload is sent with
+     * no body (-1) rather than a zero length, which the JDK server would treat as
+     * "chunked, unknown length".
+     */
+    public static void sendBinaryResponse(final HttpExchange exchange, final int statusCode,
+                                          final byte[] bytes, final String contentType) throws IOException
+    {
+        final byte[] body = (bytes != null) ? bytes : new byte[0];
+        exchange.getResponseHeaders().set("Content-Type", contentType);
+        exchange.sendResponseHeaders(statusCode, body.length == 0 ? -1 : body.length);
+        try(final OutputStream os = exchange.getResponseBody()) { os.write(body); }
+    }
```

`LinkRESTModuleImpl` drops its private copy of the binary sender and calls this one.

### 3. `modules/core/AbstractRESTModule.java` — one word

```java
-    private void removeContext(HttpContext httpContext)
+    protected void removeContext(HttpContext httpContext)
```

### 4. `modules/core/RESTModuleImpl.java` — one namespace registry for both kinds

The full code is in [note 17](17-rest-namespace-registration.md); the shape:

```java
    /** Prefix under which namespaces registered without a session are bound: {@code /agent/{namespace}}. */
    public static final String ROOT_CONTEXT_PREFIX = "/agent/";

    /** Names the core uses itself: processes under a session; sessions and admin at the root. */
    private static final Set<String> RESERVED_SESSION_NAMESPACES = new HashSet<String>(Collections.singletonList("processes"));
    private static final Set<String> RESERVED_ROOT_NAMESPACES    = new HashSet<String>(Arrays.asList("sessions", "admin"));

    /** A registered namespace: handler, whether a session is required, bound context if any. */
    private static final class Mount
    {
        final RESTNamespaceHandler handler;
        final boolean requireSession;
        volatile HttpContext context;
        Mount(final RESTNamespaceHandler handler, final boolean requireSession) { this.handler = handler; this.requireSession = requireSession; }
    }

    private final ConcurrentMap<String, Mount> mounts = new ConcurrentHashMap<String, Mount>();

    /** The REST counterpart of SOAPRPC.addEndpoint(implementor, path, requireSession). */
    public boolean registerNamespace(final String namespace, final RESTNamespaceHandler handler, final boolean requireSession)
    {
        Set<String> reserved = requireSession ? RESERVED_SESSION_NAMESPACES : RESERVED_ROOT_NAMESPACES;
        if(namespace == null || namespace.isEmpty() || namespace.contains("/") || "*".equals(namespace)
                || reserved.contains(namespace) || handler == null) return false;          // (logged)
        Mount mount = new Mount(handler, requireSession);
        if(mounts.putIfAbsent(namespace, mount) != null) return false;                       // (logged)
        if(!requireSession)
        {
            try { mount.context = addNamespaceContext(namespace); }                         // binds /agent/{namespace}
            catch(IllegalArgumentException e) { mounts.remove(namespace); return false; }  // path already bound (logged)
        }
        return true;
    }

    public boolean unregisterNamespace(final String namespace)
    {
        Mount mount = (namespace != null) ? mounts.remove(namespace) : null;
        if(mount != null && mount.context != null) removeNamespaceContext(mount.context);
        return mount != null;
    }
```

Session-scoped requests that miss the core routes look up `mounts.get(segments[1])` and delegate
when `requireSession` is set; requests arriving on a namespace's own context look up the same map
and delegate with a `null` session.

In `handle()`, the two-way split on context path becomes three-way:

```java
-                if(ADMIN_CONTEXT_PATH.equalsIgnoreCase(requestContext)) handleAdminRequest(exchange, method, segments);
-                else                                                    handleSessionRequest(exchange, method, segments);
+                if(ADMIN_CONTEXT_PATH.equalsIgnoreCase(requestContext))        handleAdminRequest(exchange, method, segments);
+                else if(SESSION_CONTEXT_PATH.equalsIgnoreCase(requestContext)) handleSessionRequest(exchange, method, segments);
+                else                                                           handleRootRequest(exchange, requestContext, segments);
```

```java
    private void handleRootRequest(final HttpExchange exchange, final String contextPath,
                                   final String[] segments) throws Throwable
    {
        Logging.Entry(logger, this, exchange, contextPath, segments);
        Mount mount = (contextPath != null && contextPath.startsWith(ROOT_CONTEXT_PREFIX))
                ? mounts.get(contextPath.substring(ROOT_CONTEXT_PREFIX.length())) : null;
        if(mount == null || mount.requireSession) RESTUtil.sendNotFound(exchange);
        else                                      mount.handler.handle(exchange, null, segments);
        Logging.Exit(logger, this);
    }
```

`unload()` unregisters every remaining mount, in case a dependent module was unloaded out of
order. The class Javadoc gains one line: `/agent/fs/... — file-system module (if loaded)`.

### 5. `modules/core/FSRESTModuleImpl.java` — new

It lives in the same module as `FSModuleImpl`, so there is no cross-jar version risk.

```java
package io.github.eipx.remoteagent.modules.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Logger;

import io.github.eipx.remoteagent.io.Config;
import io.github.eipx.remoteagent.io.IOUtil;
import io.github.eipx.remoteagent.logging.LogUtil;
import io.github.eipx.remoteagent.logging.Logging;
import io.github.eipx.remoteagent.modules.AbstractStoppableModule;
import io.github.eipx.remoteagent.modules.ModuleManager;
import io.github.eipx.remoteagent.http.Session;
import io.github.eipx.remoteagent.rest.RESTNamespaceHandler;
import io.github.eipx.remoteagent.rest.RESTRouter;
import io.github.eipx.remoteagent.rest.RESTRouter.Route;
import io.github.eipx.remoteagent.rest.RESTUtil;

/**
 * REST entry point for the file-system (FS) module. File-system operations are
 * not owned by a session, so this module registers the root namespace
 * {@code /agent/fs} on {@link RESTModuleImpl} (see
 * {@link RESTModuleImpl#registerNamespace}) and delegates every request to the
 * public methods of {@link FSModuleImpl} — the same domain logic used by the
 * SOAP (JAX-WS) entry point. Paths travel URL-encoded in the {@code path} query
 * parameter and are interpreted exactly as SOAP interprets them.
 *
 * <pre>
 * GET    /agent/fs/entries?path=                     fileExists / directoryExists / fileSize
 * GET    /agent/fs/files?path=[&offset=&length=]     readEntireFile / readFile
 * PUT    /agent/fs/files?path=                       writeFile        (body: application/octet-stream)
 * DELETE /agent/fs/files?path=                       removeFile
 * POST   /agent/fs/files/copy                        copyFile         (body: {"source","destination"})
 * POST   /agent/fs/files/move                        moveFile         (body: {"source","destination"})
 * GET    /agent/fs/directories?path=                 listDirectory
 * PUT    /agent/fs/directories?path=                 makeDirectory
 * DELETE /agent/fs/directories?path=                 removeDirectory
 * </pre>
 *
 * <p>Status mapping of the domain's quiet failures: 404 when the named entry is
 * not there (or not of the named kind), 403 when it is there but not readable or
 * writable, 409 when the operation cannot be applied to the current state
 * (parent missing, directory not empty, destination occupied), 413 above
 * {@code maxFileSize}. I/O exceptions propagate to the core's 500 handler, as
 * they become faults on SOAP.
 */
public class FSRESTModuleImpl extends AbstractStoppableModule implements BaseModule, RESTNamespaceHandler
{
	public static final Logger logger = Logger.getLogger(FSRESTModuleImpl.class);

	public static final String FS_NAMESPACE = "fs";
	public static final String CONTENT_TYPE_FILE = "application/octet-stream";

	/**
	 * Largest file payload accepted or returned whole, in bytes; larger requests
	 * receive {@code 413}. Reads can page past it with {@code offset}/{@code length}.
	 * Guards the shared request thread pool against a single huge transfer; the
	 * SOAP path has no such bound.
	 */
	protected static final long maxFileSize = Config.getProperty(FSRESTModuleImpl.class, "maxFileSize", 64L * 1024 * 1024);

	private RESTModuleImpl restModuleImpl = null;
	private FSModuleImpl fsModuleImpl = null;

	private final List<Route<Void>> routes = Arrays.asList(
			new Route<Void>("GET",    new String[] {"entries"},        (x, c, s) -> handleStat(x)),
			new Route<Void>("GET",    new String[] {"files"},          (x, c, s) -> handleReadFile(x)),
			new Route<Void>("PUT",    new String[] {"files"},          (x, c, s) -> handleWriteFile(x)),
			new Route<Void>("DELETE", new String[] {"files"},          (x, c, s) -> handleRemoveFile(x)),
			new Route<Void>("POST",   new String[] {"files", "copy"},  (x, c, s) -> handleCopyFile(x)),
			new Route<Void>("POST",   new String[] {"files", "move"},  (x, c, s) -> handleMoveFile(x)),
			new Route<Void>("GET",    new String[] {"directories"},    (x, c, s) -> handleListDirectory(x)),
			new Route<Void>("PUT",    new String[] {"directories"},    (x, c, s) -> handleMakeDirectory(x)),
			new Route<Void>("DELETE", new String[] {"directories"},    (x, c, s) -> handleRemoveDirectory(x))
	);

	public FSRESTModuleImpl()
	{
		super(FSRESTModuleImpl.class);
		Logging.Entry(logger, this);
		if(!dependencies.add(RESTModuleImpl.class.getName())) logger.warn("Unable to add RESTModuleImpl dependency!");
		if(!dependencies.add(FSModuleImpl.class.getName()))   logger.warn("Unable to add FSModuleImpl dependency!");
		Logging.Exit(logger, this);
	}

	// Module Members

	@Override
	public boolean load(final ModuleManager moduleManager)
	{
		Logging.Entry(logger, this, moduleManager);
		boolean result = super.load(moduleManager);
		if(result)
		{
			restModuleImpl = moduleManager.getModule(RESTModuleImpl.class);
			fsModuleImpl   = moduleManager.getModule(FSModuleImpl.class);
			if(restModuleImpl == null || fsModuleImpl == null)
			{
				logger.error(String.format("Required dependency not loaded: RESTModuleImpl=%s, FSModuleImpl=%s",
						restModuleImpl, fsModuleImpl));
				return Logging.Exit(logger, this, false);
			}
			result = restModuleImpl.registerNamespace(FS_NAMESPACE, this, false);
			if(!result) logger.error(String.format("Unable to register REST root namespace: %s", FS_NAMESPACE));
		}
		return Logging.Exit(logger, this, result);
	}

	@Override
	public boolean unload(final ModuleManager moduleManager)
	{
		Logging.Entry(logger, this, moduleManager);
		if(restModuleImpl != null) restModuleImpl.unregisterNamespace(FS_NAMESPACE);
		restModuleImpl = null;
		fsModuleImpl = null;
		return Logging.Exit(logger, this, super.unload(moduleManager));
	}

	// RESTNamespaceHandler Members

	@Override
	public void handle(final HttpExchange exchange, final Session session, final String[] segments) throws Throwable
	{
		Logging.Entry(logger, this, exchange, segments);
		final String method = exchange.getRequestMethod().toUpperCase();
		if(!RESTRouter.dispatch(exchange, method, segments, routes, null)) RESTUtil.sendNotFound(exchange);
		Logging.Exit(logger, this);
	}

	@Override
	public String getStatus()
	{
		Logging.Entry(logger, this);
		final String result = String.format("<label>REST namespace:</label> %s%s<br>", RESTModuleImpl.ROOT_CONTEXT_PREFIX, FS_NAMESPACE);
		return Logging.Exit(logger, this, result);
	}

	// Route Handlers

	/** {@code GET /fs/entries?path=} — existence, type and size in one safe call; never 404. */
	protected void handleStat(final HttpExchange exchange) throws Throwable
	{
		Logging.Entry(logger, this, exchange);
		final String path = requirePath(exchange);
		if(path == null) { Logging.Exit(logger, this); return; }

		final boolean isFile = fsModuleImpl.fileExists(path);
		final boolean isDirectory = !isFile && fsModuleImpl.directoryExists(path);

		final Map<String, Object> body = new LinkedHashMap<String, Object>();
		body.put("path", path);
		body.put("exists", isFile || isDirectory);
		body.put("type", isFile ? "file" : isDirectory ? "directory" : "none");
		if(isFile)
		{
			final long size = fsModuleImpl.fileSize(path);   // -1 when the file is not readable
			body.put("readable", size >= 0);
			if(size >= 0) body.put("size", size);
		}
		RESTUtil.sendJsonResponse(exchange, 200, body);
		Logging.Exit(logger, this);
	}

	/** {@code GET /fs/files?path=[&offset=&length=]} — the whole file, or a window of it. */
	protected void handleReadFile(final HttpExchange exchange) throws Throwable
	{
		Logging.Entry(logger, this, exchange);
		final String path = requirePath(exchange);
		if(path == null) { Logging.Exit(logger, this); return; }

		final Properties params = RESTUtil.getQueryParameters(exchange);
		final Integer offset = parseIntParameter(exchange, params, "offset", 0, 0);
		if(offset == null) { Logging.Exit(logger, this); return; }
		final Integer length = parseIntParameter(exchange, params, "length", -1, 1);
		if(length == null) { Logging.Exit(logger, this); return; }

		if(!fsModuleImpl.fileExists(path))
		{
			RESTUtil.sendNotFound(exchange);
			Logging.Exit(logger, this); return;
		}
		final long size = fsModuleImpl.fileSize(path);
		if(size < 0)
		{
			RESTUtil.sendErrorResponse(exchange, 403, String.format("File is not readable: %s", path));
			Logging.Exit(logger, this); return;
		}
		if(length < 0 && size > maxFileSize)
		{
			RESTUtil.sendErrorResponse(exchange, 413,
					String.format("File exceeds maxFileSize (%d); read it with offset/length", maxFileSize));
			Logging.Exit(logger, this); return;
		}

		// readInputStream() returns null for an empty stream, so null here means
		// "nothing to return" — existence and readability were established above.
		final byte[] content = (length < 0) ? fsModuleImpl.readEntireFile(path)
		                                    : fsModuleImpl.readFile(path, length, offset);
		RESTUtil.sendBinaryResponse(exchange, 200, content, CONTENT_TYPE_FILE);
		Logging.Exit(logger, this);
	}

	/** {@code PUT /fs/files?path=} — full overwrite (idempotent); 201 when the file did not exist. */
	protected void handleWriteFile(final HttpExchange exchange) throws Throwable
	{
		Logging.Entry(logger, this, exchange);
		final String path = requirePath(exchange);
		if(path == null) { Logging.Exit(logger, this); return; }

		final String parent = new File(path).getParent();
		if(parent != null && !fsModuleImpl.directoryExists(parent))
		{
			RESTUtil.sendErrorResponse(exchange, 409, String.format("Parent directory does not exist: %s", parent));
			Logging.Exit(logger, this); return;
		}

		final byte[] content = readBoundedBody(exchange);   // empty body = empty file, as SOAP allows
		if(content == null) { Logging.Exit(logger, this); return; }   // 400 or 413 already sent

		final boolean existed = fsModuleImpl.fileExists(path);
		if(!fsModuleImpl.writeFile(path, content))
		{
			if(existed) RESTUtil.sendErrorResponse(exchange, 403, String.format("File is not writable: %s", path));
			else        RESTUtil.sendErrorResponse(exchange, 409, String.format("Cannot create file: %s", path));
			Logging.Exit(logger, this); return;
		}
		final Map<String, Object> body = success();
		body.put("bytes", content.length);
		RESTUtil.sendJsonResponse(exchange, existed ? 200 : 201, body);
		Logging.Exit(logger, this);
	}

	/** {@code DELETE /fs/files?path=} */
	protected void handleRemoveFile(final HttpExchange exchange) throws Throwable
	{
		Logging.Entry(logger, this, exchange);
		final String path = requirePath(exchange);
		if(path == null) { Logging.Exit(logger, this); return; }

		if(!fsModuleImpl.fileExists(path))      RESTUtil.sendNotFound(exchange);
		else if(!fsModuleImpl.removeFile(path)) RESTUtil.sendErrorResponse(exchange, 403, String.format("File is not writable or is in use: %s", path));
		else                                    RESTUtil.sendJsonResponse(exchange, 200, success());
		Logging.Exit(logger, this);
	}

	/** {@code POST /fs/files/copy} with {@code {"source","destination"}} — 201 with the copy's Location. */
	protected void handleCopyFile(final HttpExchange exchange) throws Throwable
	{
		Logging.Entry(logger, this, exchange);
		final String[] pair = requireSourceDestination(exchange);
		if(pair == null) { Logging.Exit(logger, this); return; }

		if(!fsModuleImpl.fileExists(pair[0])) RESTUtil.sendNotFound(exchange);
		else if(!fsModuleImpl.copyFile(pair[0], pair[1]))
			RESTUtil.sendErrorResponse(exchange, 409, String.format("Destination exists and is not writable: %s", pair[1]));
		else
		{
			exchange.getResponseHeaders().set("Location", String.format("%s%s/files?path=%s",
					RESTModuleImpl.ROOT_CONTEXT_PREFIX, FS_NAMESPACE, URLEncoder.encode(pair[1], "UTF-8")));
			RESTUtil.sendJsonResponse(exchange, 201, success());
		}
		Logging.Exit(logger, this);
	}

	/** {@code POST /fs/files/move} with {@code {"source","destination"}}; moves directories too, as the domain does. */
	protected void handleMoveFile(final HttpExchange exchange) throws Throwable
	{
		Logging.Entry(logger, this, exchange);
		final String[] pair = requireSourceDestination(exchange);
		if(pair == null) { Logging.Exit(logger, this); return; }

		if(!fsModuleImpl.fileExists(pair[0]) && !fsModuleImpl.directoryExists(pair[0])) RESTUtil.sendNotFound(exchange);
		else if(!fsModuleImpl.moveFile(pair[0], pair[1]))
			RESTUtil.sendErrorResponse(exchange, 409, String.format("Cannot move to destination: %s", pair[1]));
		else RESTUtil.sendJsonResponse(exchange, 200, success());
		Logging.Exit(logger, this);
	}

	/** {@code GET /fs/directories?path=} — entries as a JSON array (SOAP returns newline-separated text). */
	protected void handleListDirectory(final HttpExchange exchange) throws Throwable
	{
		Logging.Entry(logger, this, exchange);
		final String path = requirePath(exchange);
		if(path == null) { Logging.Exit(logger, this); return; }

		final String listing = fsModuleImpl.listDirectory(path);   // null: not a readable directory
		if(listing == null) { RESTUtil.sendNotFound(exchange); Logging.Exit(logger, this); return; }

		final List<String> entries = new ArrayList<String>();
		for(final String name : listing.split(IOUtil.newline)) if(!name.isEmpty()) entries.add(name);

		final Map<String, Object> body = new LinkedHashMap<String, Object>();
		body.put("path", path);
		body.put("entries", entries);
		RESTUtil.sendJsonResponse(exchange, 200, body);
		Logging.Exit(logger, this);
	}

	/** {@code PUT /fs/directories?path=} — 201 created, 200 already present (idempotent). */
	protected void handleMakeDirectory(final HttpExchange exchange) throws Throwable
	{
		Logging.Entry(logger, this, exchange);
		final String path = requirePath(exchange);
		if(path == null) { Logging.Exit(logger, this); return; }

		// The domain returns false both for "already a directory" and for a failed
		// mkdir, so existence is checked first to keep the two apart.
		if(fsModuleImpl.directoryExists(path))    RESTUtil.sendJsonResponse(exchange, 200, success());
		else if(fsModuleImpl.makeDirectory(path)) RESTUtil.sendJsonResponse(exchange, 201, success());
		else RESTUtil.sendErrorResponse(exchange, 409, String.format("Cannot create directory (parent missing or not writable): %s", path));
		Logging.Exit(logger, this);
	}

	/** {@code DELETE /fs/directories?path=} */
	protected void handleRemoveDirectory(final HttpExchange exchange) throws Throwable
	{
		Logging.Entry(logger, this, exchange);
		final String path = requirePath(exchange);
		if(path == null) { Logging.Exit(logger, this); return; }

		if(!fsModuleImpl.directoryExists(path))      RESTUtil.sendNotFound(exchange);
		else if(!fsModuleImpl.removeDirectory(path)) RESTUtil.sendErrorResponse(exchange, 409, String.format("Directory is not empty or not writable: %s", path));
		else                                         RESTUtil.sendJsonResponse(exchange, 200, success());
		Logging.Exit(logger, this);
	}

	// Private Members

	/** Returns the {@code path} query parameter, or sends 400 and returns null. */
	private String requirePath(final HttpExchange exchange) throws IOException
	{
		Logging.Entry(logger, this, exchange);
		final String path = RESTUtil.getQueryParameters(exchange).getProperty("path");
		if(path == null || path.trim().isEmpty())
		{
			RESTUtil.sendErrorResponse(exchange, 400, "Query parameter 'path' is required");
			return Logging.Exit(logger, this, null);
		}
		return Logging.Exit(logger, this, path);
	}

	/** Returns {@code {source, destination}} from the JSON body, or sends 400 and returns null. */
	private String[] requireSourceDestination(final HttpExchange exchange) throws IOException
	{
		Logging.Entry(logger, this, exchange);
		Map<String, Object> json = null;
		try { json = RESTUtil.parseJsonObject(RESTUtil.readRequestBody(exchange)); }
		catch(final Exception e) { /* handled below */ }
		final Object source      = (json != null) ? json.get("source")      : null;
		final Object destination = (json != null) ? json.get("destination") : null;
		if(source == null || destination == null
				|| source.toString().trim().isEmpty() || destination.toString().trim().isEmpty())
		{
			RESTUtil.sendErrorResponse(exchange, 400, "JSON body with 'source' and 'destination' is required");
			return Logging.Exit(logger, this, null);
		}
		return Logging.Exit(logger, this, new String[] { source.toString(), destination.toString() });
	}

	/** Parses an integer query parameter: absent → default; invalid or below minimum → 400 and null. */
	private Integer parseIntParameter(final HttpExchange exchange, final Properties params, final String name,
	                                  final int defaultValue, final int minimum) throws IOException
	{
		Logging.Entry(logger, this, exchange, name, defaultValue, minimum);
		final String raw = params.getProperty(name);
		if(raw == null) return Logging.Exit(logger, this, Integer.valueOf(defaultValue));
		try
		{
			final int value = Integer.parseInt(raw);
			if(value < minimum) throw new NumberFormatException();
			return Logging.Exit(logger, this, Integer.valueOf(value));
		}
		catch(final NumberFormatException nfe)
		{
			RESTUtil.sendErrorResponse(exchange, 400,
					String.format("Invalid %s parameter (integer >= %d expected): %s", name, minimum, raw));
			return Logging.Exit(logger, this, null);
		}
	}

	/**
	 * Reads the whole request body up to {@code maxFileSize}. Sends 400 for a
	 * malformed {@code Content-Length}, 413 above the limit (checked against the
	 * declared length first, then enforced while reading), and returns null in
	 * both cases. An empty body yields an empty array.
	 * <p>
	 * Note: the shared {@code readInputStream(stream, length)} helper performs a
	 * single {@code read()} and may return a partial chunk, so it is not used
	 * here; the body is read to end-of-stream.
	 */
	private byte[] readBoundedBody(final HttpExchange exchange) throws IOException
	{
		Logging.Entry(logger, this, exchange);
		final String declared = exchange.getRequestHeaders().getFirst("Content-Length");
		if(declared != null)
		{
			try
			{
				if(Long.parseLong(declared.trim()) > maxFileSize)
				{
					RESTUtil.sendErrorResponse(exchange, 413, String.format("Body exceeds maxFileSize (%d)", maxFileSize));
					return Logging.Exit(logger, this, null);
				}
			}
			catch(final NumberFormatException nfe)
			{
				RESTUtil.sendErrorResponse(exchange, 400, String.format("Invalid Content-Length: %s", declared));
				return Logging.Exit(logger, this, null);
			}
		}

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buffer = new byte[64 * 1024];
		try(final InputStream in = exchange.getRequestBody())
		{
			int read;
			while((read = in.read(buffer)) != -1)
			{
				if(out.size() + read > maxFileSize)
				{
					RESTUtil.sendErrorResponse(exchange, 413, String.format("Body exceeds maxFileSize (%d)", maxFileSize));
					return Logging.Exit(logger, this, null);
				}
				out.write(buffer, 0, read);
			}
		}
		return Logging.Exit(logger, this, out.toByteArray());
	}

	private static Map<String, Object> success()
	{
		final Map<String, Object> body = new LinkedHashMap<String, Object>();
		body.put("success", true);
		return body;
	}

	// BaseModule Members

	@Override
	public String getName()
	{
		Logging.Entry(logger, this);
		return Logging.Exit(logger, this, LogUtil.getUnqualifyClassName(FSRESTModuleImpl.class));
	}

	@Override
	public String getVersion()
	{
		Logging.Entry(logger, this);
		return Logging.Exit(logger, this, LogUtil.getVersion(FSRESTModuleImpl.class));
	}

	// Object Members

	@Override
	public String toString()
	{
		return LogUtil.getUnqualifyClassName(this);
	}
}
```

### 6. `FSModuleImpl.java` — unchanged

Worth stating in the pull request: the facade calls only existing public methods, so SOAP
behaviour and the module are untouched. The one domain-side item is the documentation line
claiming parent directories are created.

### 7. Tests

`FSRESTModuleImplTest`: mock only the `HttpExchange`; run the *real* `FSModuleImpl` against a
JUnit `TemporaryFolder`, injected by reflection the same way the Link facade's tests inject
their mock. The point is to prove the status mapping against real `java.io.File` behaviour,
which is where the risk is.

- stat: file, directory, missing (200 with `exists:false`); missing `path` → 400
- read: contents and `Content-Type`; empty file → 200 with empty body; `offset`/`length`
  window; `offset=-1` → 400; `length=0` → 400; missing file → 404; unreadable → 403; over
  `maxFileSize` → 413
- write: new → 201; overwrite → 200 and contents replaced; empty body → empty file; a body
  larger than one TCP read is written whole (the single-read bug this test exists for);
  declared length over the limit → 413; malformed `Content-Length` → 400; parent missing → 409
- remove file: 200 then 404 on repeat
- copy: 201 with `Location`; missing source → 404; bad JSON → 400; move: 200 and source gone;
  move of a directory works
- list: entries array; empty directory → `[]`; a file as `path` → 404
- make directory: 201 then 200 on repeat; parent missing → 409
- remove directory: 200; non-empty → 409; missing → 404
- routing: unknown sub-path → 404; `POST /files` → 405
- query decoding: `path=%2Bx` resolves to `+x` (the raw-query fix)

`RESTModuleImplNamespaceTest` (11 cases): both kinds register through the one API; a session-scoped
namespace binds no context and a root one binds exactly one; `processes` is refused under a
session and `sessions`/`admin` at the root; malformed names, null handler and duplicates across
kinds are refused; unregistering frees the name for either kind and unbinds only what was bound.

### 8. Documentation and the interactive API page

- A module page for `FSRESTModuleImpl` in the shape of the Link one (routes, status table,
  `maxFileSize`), a sidebar entry, nine cards under "File System Endpoints", nine rows in the
  route summary.
- Endpoint map entries for the interactive page. The query template already exists for the
  process routes; confirm the substitution applies `encodeURIComponent`, because paths with
  backslashes depend on it.

```js
'fs-stat':   { method: 'GET',    path: '/agent/fs/entries',     queryTemplate: '?path={path}' },
'fs-read':   { method: 'GET',    path: '/agent/fs/files',       queryTemplate: '?path={path}', optionalQuery: ['offset', 'length'] },
'fs-write':  { method: 'PUT',    path: '/agent/fs/files',       queryTemplate: '?path={path}', contentType: 'application/octet-stream' },
'fs-delete': { method: 'DELETE', path: '/agent/fs/files',       queryTemplate: '?path={path}' },
'fs-copy':   { method: 'POST',   path: '/agent/fs/files/copy' },
'fs-move':   { method: 'POST',   path: '/agent/fs/files/move' },
'fs-list':   { method: 'GET',    path: '/agent/fs/directories', queryTemplate: '?path={path}' },
'fs-mkdir':  { method: 'PUT',    path: '/agent/fs/directories', queryTemplate: '?path={path}' },
'fs-rmdir':  { method: 'DELETE', path: '/agent/fs/directories', queryTemplate: '?path={path}' }
```

- The configuration reference gains `FSRESTModuleImpl.maxFileSize` (long, default 67108864).
- `agent.properties`: `FSModuleImpl` is already in the default `Agent.modules` list, so the
  REST facade joins it — the namespace is additive and this is a core module. The Link facade
  stays opt-in because the Link module is.
- A smoke script against a live agent. Unlike the Link one, every assertion here can be
  strict: write a file, read it back byte for byte, list, copy, move, delete, and check each
  error code.

### 9. Follow-ups, deliberately not in this change

- A REST twin in the Python client library (it uses only write, read and read-entire).
- A streaming `writeFile(path, InputStream)` domain overload if files above the cap matter.
- Whether `makeDirectory` should create parents — a product decision, not a transport one.

Rough size: about 900 lines across 14 files, of which the facade is 450 and the core change
about 60. Smaller than the Link change, because the domain needs no overloads.

## Self-review: what changed between the first draft and this version

Writing the design is not reviewing it. A pass over the first draft against the actual
helpers it called found four things, all fixed above:

1. **A real bug.** The draft read the upload body with the shared `readInputStream(stream,
   length)`, whose Javadoc says "at most `length` bytes" — because it performs exactly one
   `read()`. Over a socket that returns one TCP segment, so a large upload would have been
   silently truncated and written as a short file with a 201. The handler now reads to
   end-of-stream with its own bounded loop, and the test list has a case for a body larger
   than one read.
2. **Permission versus state.** The draft used 409 for everything the domain reported as
   `false`. The domain does expose readability (`fileSize` is `-1` for an unreadable file) and
   existence, so the facade can say 403 for "there but not readable/writable" and keep 409 for
   "cannot be applied to the current state". Where the domain genuinely cannot tell (a
   non-empty versus a read-only directory), one code with an honest message is better than a
   guess.
3. **A malformed `Content-Length`** would have thrown inside the handler and surfaced as a
   500; it is a client error and now returns 400.
4. **House style.** Every helper and `BaseModule` method now logs entry and exit like the rest
   of the codebase; a facade that is 90 percent consistent is harder to read than one that is
   either.

### One lesson worth keeping

The Link change taught "the pattern"; this change is where the pattern would have led astray.
Before copying a sub-router under a session, ask what owns the resource. If nothing does, the
URL should not pretend otherwise.

## The patch

The complete change is beside this note as two patches, so each branch gets what it needs:
[`17-rest-namespace-registration.patch`](17-rest-namespace-registration.patch) is the
infrastructure (the unified handler and registry, the raw-query fix, the shared binary sender,
the Link facade's one-line switch, the registry tests) and applies to the Link branch as a
follow-up; [`16-rest-facade-for-a-stateless-module.patch`](16-rest-facade-for-a-stateless-module.patch)
is this design (the facade, its 35 tests, the module page, nine explorer cards and endpoint-map
entries, route summary, configuration reference, the default module list, and the one-line
correction to the file-system module's page) and applies on top of it. Both are unified diffs
with two lines of context.

Identifiers in the patch are renamed consistently (package root, product and module names), so
its paths will not match any tree you have; it is meant to be read with the note, not applied as
is.

What was verified before publishing, and what was not:

- Compiled with `javac` against the real project sources, with the third-party APIs (logging,
  JAX-WS annotations, JUnit) replaced by minimal stubs: zero errors across 146 classes.
- The tests were executed against the real file-system module on a temporary directory, together
  with the registry tests and the Link facade's existing tests on the unified API: 70 passed,
  0 failed, 1 skipped — the unreadable-file case, which the host operating system could not
  simulate. The test for a body delivered in small chunks is the one that would have caught the
  first draft's single-read bug; the test for `+` and `%` in a path is the one that exercises the
  raw-query fix end to end.
- Not done: a build through the project's own pipeline, and a run against a live agent. A
  smoke script against a running instance is the natural next step and can be strict for every
  route, since file operations are deterministic.
