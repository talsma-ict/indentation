# Indentation utilities for Java

## Indentation

The `Indentation` class is an immutable `CharSequence` to prepend at the beginning of new lines.

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

Indentations start at level 0 and can be _indented_ and _unindented_ or set to a random level using _atLevel_.
These operations return other Indentation instances, as the Indentation class is designed to be immutable.
This allows for safe caching and reuse without worrying about internal state changing.

### Caching

For efficiency reasons, the first indentation levels are cached.

Indentations created with `Indentation.of(..)` resolve to the defined 
constants (`EMPTY`, `TABS`, `TWO_SPACES` or `FOUR_SPACES`) if applicable.

### Serialization / deserialization

Indentations are `Serializable`.

To conserve space, indentations serialize a _proxy_ of the indentation _unit_ and _level_.  
Deserialization uses `Indentation.of(unit).atLevel(level)`, enabling efficient reuse of constants and caches.

## IndentingWriter

A writer that automatically applies a _current_ indentation to each line.

The current indentation can be changed by calling `indent()` or `unindent()`.
