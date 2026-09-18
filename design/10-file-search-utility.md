# Command-line file search utility

## Requirements
- Search a directory tree by name, size, extension, and modified time.
- Combine any number of filters with AND, OR, and NOT.
- Accept a query as a short expression string from the command line, and support adding a new attribute filter without touching existing filter classes.

## Core abstractions
- `FileFilter` (the specification): `boolean matches(FileInfo file)`.
- `AndFilter`, `OrFilter`, `NotFilter`: composite filters over other `FileFilter`s.
- `NameFilter`, `SizeFilter`, `ExtensionFilter`, `ModifiedTimeFilter`: leaf filters, one per attribute.
- `QueryParser`: turns a query string into a `FileFilter` tree.
- `FileVisitor` and a traverser: walk the directory tree and hand each file to the filter.

## Java 8 skeleton
```java
public final class FileInfo {
    public final String path;
    public final String name;
    public final long sizeBytes;
    public final long modifiedMillis;
    public FileInfo(String path, String name, long sizeBytes, long modifiedMillis) {
        this.path = path; this.name = name;
        this.sizeBytes = sizeBytes; this.modifiedMillis = modifiedMillis;
    }
}

public interface FileFilter { boolean matches(FileInfo file); }

public class AndFilter implements FileFilter {
    private final List<FileFilter> filters;
    public AndFilter(List<FileFilter> filters) { this.filters = filters; }
    public boolean matches(FileInfo file) {
        for (FileFilter filter : filters) if (!filter.matches(file)) return false;
        return true;
    }
}

public class OrFilter implements FileFilter {
    private final List<FileFilter> filters;
    public OrFilter(List<FileFilter> filters) { this.filters = filters; }
    public boolean matches(FileInfo file) {
        for (FileFilter filter : filters) if (filter.matches(file)) return true;
        return false;
    }
}

public class NotFilter implements FileFilter {
    private final FileFilter delegate;
    public NotFilter(FileFilter delegate) { this.delegate = delegate; }
    public boolean matches(FileInfo file) { return !delegate.matches(file); }
}

public class NameFilter implements FileFilter {
    private final String substring;
    public NameFilter(String substring) { this.substring = substring; }
    public boolean matches(FileInfo file) { return file.name.contains(substring); }
}

public class SizeFilter implements FileFilter {
    public enum Comparison { GREATER_THAN, LESS_THAN }
    private final long thresholdBytes;
    private final Comparison comparison;
    public SizeFilter(long thresholdBytes, Comparison comparison) {
        this.thresholdBytes = thresholdBytes; this.comparison = comparison;
    }
    public boolean matches(FileInfo file) {
        return comparison == Comparison.GREATER_THAN
                ? file.sizeBytes > thresholdBytes : file.sizeBytes < thresholdBytes;
    }
}

public class ExtensionFilter implements FileFilter {
    private final String extension;
    public ExtensionFilter(String extension) { this.extension = extension; }
    public boolean matches(FileInfo file) { return file.name.endsWith("." + extension); }
}

public interface FileVisitor { void visit(FileInfo file); }

public class FilteringVisitor implements FileVisitor {
    private final FileFilter filter;
    private final List<FileInfo> matches = new ArrayList<FileInfo>();
    public FilteringVisitor(FileFilter filter) { this.filter = filter; }
    public void visit(FileInfo file) { if (filter.matches(file)) matches.add(file); }
    public List<FileInfo> getMatches() { return matches; }
}
```

## Design patterns used and why
Each leaf filter is the **Specification** pattern: a self-contained predicate object that can be combined instead of a single method with a growing parameter list. `AndFilter`, `OrFilter`, and `NotFilter` are the **Composite** pattern over that specification; a tree of filters implements the same `FileFilter` interface as a leaf, so the traversal code never needs to know whether it holds one filter or a hundred combined with boolean logic. Walking the tree with a `FileVisitor` is the **Visitor** pattern, keeping traversal (recursing into directories) separate from what happens to each file found.

## Extension points
A new attribute (owner, permissions, a content hash) is a new class implementing `FileFilter`, wired into the parser's keyword table; no change to `AndFilter`, `OrFilter`, `NotFilter`, or the traversal code, since they only depend on the `FileFilter` interface. A new combination rule (an exclusive-or, an "at least N of these match") is another composite implementing the same interface.

## Query parsing
A small recursive-descent parser turns a string like `ext:log AND (size>10MB OR modified<7d) AND NOT name:archive` into a `FileFilter` tree: tokenize into keywords, comparators, and values; parse `NOT` (highest precedence), then `AND`, then `OR`, or require explicit parentheses to sidestep precedence questions entirely, the simpler and more debuggable choice for a small utility.

## Testing approach
Unit test each leaf filter directly against constructed `FileInfo` instances, including boundary values (a file exactly at the size threshold). Test each composite against fakes: an `AndFilter` of an always-true and an always-false filter is false, `OrFilter` the opposite, `NotFilter` inverts. Test the parser by asserting the shape of the returned filter tree for a handful of representative queries, and separately test that a parsed tree applied to a small in-memory file list returns the expected matches, so parsing bugs and filter-logic bugs fail independently.

## Follow-up questions
1. Why specification instead of one big filter method with flags? Flags multiply combinatorially as attributes grow; specification objects compose instead of requiring a new branch for every new combination.
2. How would you support a query editor with live validation? Parse to the same `FileFilter` tree but surface parser errors with a position in the string, rather than only accept or reject.
3. How do you keep traversal from descending into excluded directories for performance? Let the traverser ask a separate directory-level filter before recursing, distinct from the file-level `FileFilter`, so pruning does not require evaluating every file underneath first.
4. What changes for a very large tree that does not fit in memory at once? Stream matches out as they are found instead of collecting them in `FilteringVisitor`, turning the visitor into a callback that writes directly to output.
5. How do you add case-insensitive name matching without a new class? Add a constructor flag to `NameFilter` for this narrow variant, but prefer a new class once the behavior diverges enough to need its own tests.
