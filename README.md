# Indentation utilities for Java

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

Indentations start at level 0 and can be _indented_ and _unindented_ or set to a random level using _atLevel_.
These operations return other Indentation instances, as the Indentation class is designed to be immutable.
This allows for safe caching and reuse without worrying about internal state changing.

### Caching

For efficiency reasons, the constants `EMPTY`, `TABS`, `TWO_SPACES` and `FOUR_SPACES` cache the first _twenty_ levels of
indentation.
Indentations created with `Indentation.of(..)` resolve to these constants if applicable.
Other instances created with `Indentation.of(..)` cache the first _ten_ indentation levels.

### Serialization / deserialization

Indentations are `Serializable`.

To conserve space, indentations serialize a _proxy_ of the indentation _unit_ and _level_.  
Deserialization uses `Indentation.of(unit).atLevel(level)`, enabling efficient reuse of constants and caches.
