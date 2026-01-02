# Indent4J - Indentation utilities for Java

## Indentation

Core of this libary is the `Indentation` class.  
Indentation is a `CharSequence` that can be used to prefix lines in structured text.

Indentations consist of:
- Indentation _unit_: sequence of characters that are repeated for each indentation _level_.
- Indentation _level_: a non-negative number indicating the level of indentation, 0 meaning no indentation.

### Obtaining Indentation instances

The following common indentation styles are provided as constants:

- `Indentation.EMPTY` - No actual indentation, but still maintains the indentation level.
- `Indentation.TABS` - Indentation using tabs.
- `Indentation.TWO_SPACES` - Indentation using two spaces.
- `Indentation.FOUR_SPACES` - Indentation using four spaces.

To create another `Indentation` instance, use the `Indentation.of(CharSequence)` method.
This returns an `Indentation` instance that uses the given `CharSequence` as indentation unit.

Indentations start at level 0 and can be _indented_ and _unindented_.
Both operations return other Indentation instances, because indentation objects are immutable.
This is by design so they can be safely cached and reused without worrying about their state changing.

### Caching

For efficiency reasons, the first twenty indentation levels are cached for the
constants `EMPTY`, `TABS`, `TWO_SPACES` and `FOUR_SPACES`.  
Indentations created with `Indentation.of(CharSequence)` will cache
the first five levels of indentation.

### Serialization / deserialization

Indentations are `Serializable`.  
To conserve space, indentations will **not** serialize the entire character sequence,
but a serialization _proxy_ of the indentation _unit_ and _level_.  
Deserialization uses `Indentation.of(unit).atLevel(level)`
enabling efficient reuse of indentation constants and caches.
