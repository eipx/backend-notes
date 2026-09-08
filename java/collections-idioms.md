# Java collections idioms (Java 8 baseline)

## Things that fail to compile on Java 8
- `var` does not exist.
- Diamond with an anonymous class: `new LinkedHashMap<>() { ... }` is
  Java 9+. Write the type arguments: `new LinkedHashMap<K, V>() { ... }`.
- `List.of`, `Map.of`, `Set.of` are Java 9+. Use `Arrays.asList` or build
  the collection.
- Records, text blocks, switch expressions: not available.

## Declared type decides the methods
`Map<K,V> m = new TreeMap<>()` has no `lastKey()`, `floorEntry()`,
`ceilingKey()`. Declare `TreeMap<K,V>` or `NavigableMap<K,V>` to use them.

## Counting
```java
counts.merge(key, 1, Integer::sum);          // add one
int c = counts.getOrDefault(key, 0);         // read with default
```
`getOrDefault` does not store the default. If you need a container to exist,
use `computeIfAbsent(key, k -> new ArrayList<>())` and then mutate it.

## Boxed numbers
Never `==` or `!=` between two `Integer` values. The cache covers -128..127
only; 200 == 200 can be false. Use `equals`, or `Integer.compare`, or unbox
to `int` first.

## Comparators
`Integer.compare(a, b)`, never `a - b` (overflow flips the sign).
`Comparator.comparingInt(...).thenComparing(...)` for multi-key sorts.
Reversed direction: `.reversed()`.

## Heaps
`PriorityQueue` is a **min-heap** by default. Max-heap:
`new PriorityQueue<>(Collections.reverseOrder())`.
Removing by value is O(n); if you need that, use a `TreeMap` as a counting
multiset (add: `merge(v, 1, Integer::sum)`; remove: decrement and drop the
key at zero; max: `lastKey()`; min: `firstKey()`).

## Queues and stacks
`ArrayDeque` for both. `offer/poll/peek` for queue behavior,
`push/pop/peek` for stack behavior. It rejects `null`. Do not use
`java.util.Stack`.

## LRU in ten lines
```java
new LinkedHashMap<K, V>(capacity, 0.75f, true) {
    @Override protected boolean removeEldestEntry(Map.Entry<K, V> e) {
        return size() > capacity;
    }
};
```
The third constructor argument `true` is access order: `get` and `put`
move the key to the tail. Without it the map is insertion order and behaves
as FIFO.

## Strings
`String.join(", ", parts)` for delimiters. `StringBuilder.setLength(len - 2)`
to drop a trailing separator. `String.length()` is a method,
`array.length` is a field, `Collection.size()` is a method.

## Nested classes
A helper class inside another class should be `static` unless it needs the
outer instance. Non-static inner classes carry a hidden outer reference.

## Iteration safety
Removing while iterating: `collection.removeIf(predicate)`. Streams never
mutate their source.
