/*
 * Copyright 2025-2026 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.indentation;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

/// Indentation to prepend to characters whenever printing on a new line.
///
/// Indentation implements an immutable [CharSequence].
/// Methods like `indent()`, `unindent()` and `atLevel(..)` return _other_ instances
/// and will not modify the original indentation itself.
///
/// Indentation is serializable and will serialize minimally by retaining only the indentation unit and level.
/// Deserialization uses `Indentation.of(unit).atLevel(level)` to enable constant and cache re-use.
///
/// @author Sjoerd Talsma
public final class Indentation implements CharSequence, Serializable {
    /// Empty indentation, at level `0`.
    ///
    /// Although the indentation level is maintained, the indentation itself remains empty at all levels.
    ///
    /// Calling [Indentation.of(`""``)][#of(CharSequence)] returns this constant for efficiency.
    public static final Indentation EMPTY = createIndentation("", 2);

    /// Indentation using tab characters, at level `0`.
    ///
    /// Calling [Indentation.of(`"\t"`)][#of(CharSequence)] returns this constant for efficiency.
    public static final Indentation TABS = createIndentation("\t", 20);

    /// Indentation by two spaces, at level `0`.
    ///
    /// Calling [Indentation.of(...)][#of(CharSequence)] with _two spaces_ returns this constant for efficiency.
    public static final Indentation TWO_SPACES = createIndentation("  ", 20);

    /// Indentation by four spaces, at level `0`.
    ///
    /// Calling [Indentation.of(...)][#of(CharSequence)] with _four spaces_ returns this constant for efficiency.
    public static final Indentation FOUR_SPACES = createIndentation("    ", 20);

    private final int level;
    private final String value;
    private final Indentation[] cache;

    private Indentation(int level, String value, Indentation[] cache) {
        this.level = level;
        this.value = value;
        this.cache = cache;
    }

    /// Return an indentation, initialized at level 0.
    ///
    /// Common indentations are available as static constants: [#TABS], [#TWO_SPACES], [#FOUR_SPACES].
    ///
    /// @param unit The indentation unit that will be repeated for each level of indentation.
    /// @return The indentation.
    public static Indentation of(CharSequence unit) {
        if (unit instanceof Indentation) {
            return ((Indentation) unit).atLevel(0);
        }
        final int len = requireNonNull(unit, "Indentation unit cannot be null.").length();
        if (len == 0) {
            return EMPTY;
        } else if (len == 1 && unit.charAt(0) == '\t') {
            return TABS;
        } else if (len == 2 && unit.charAt(0) == ' ' && unit.charAt(1) == ' ') {
            return TWO_SPACES;
        } else if (len == 4 && unit.charAt(0) == ' ' && unit.charAt(1) == ' ' && unit.charAt(2) == ' ' && unit.charAt(3) == ' ') {
            return FOUR_SPACES;
        }

        return createIndentation(unit.toString(), 10);
    }

    /// Creates a new indentation at level `0`, initializing a cache of specified size.
    ///
    /// @param unit      The indentation unit. This is the indentation at level `1`.
    /// @param cacheSize The number of Indentations to cache. If less than 2 is specified, a minimal cache containing levels `0` (empty) and `1` is created.
    /// @return The new indentation (with cache) that was created, initially at level `0`.
    private static Indentation createIndentation(final String unit, final int cacheSize) {
        final Indentation[] cache = new Indentation[Math.max(2, cacheSize)]; // getUnit requires cache size >= 2
        final StringBuilder buffer = new StringBuilder(unit.length() * cache.length);
        cache[0] = new Indentation(0, "", cache);
        for (int level = 1; level < cache.length; level++) {
            buffer.append(unit);
            cache[level] = new Indentation(level, buffer.toString(), cache);
        }
        return cache[0];
    }

    /// Increase the indentation level by one.
    ///
    /// @return The indentation with increased level.
    /// @implSpec The original indentation object is not modified, instead another indentation instance is returned.
    public Indentation indent() {
        return atLevel(level + 1);
    }

    /// Decrease the indentation level by one.
    ///
    /// The level will never become negative. Calling `unindent()` on an indentation at level 0
    /// will simply return the same indentation.
    ///
    /// @return The indentation with decreased level.
    public Indentation unindent() {
        return level == 0 ? this : atLevel(level - 1);
    }

    /// The level of this indentation
    ///
    /// This is the number of [units][#getUnit()] this indentation represents.
    ///
    /// @return The level of this indentation
    public int getLevel() {
        return level;
    }

    /// The 'unit' of this indentation.
    ///
    /// By definition, the indentation Unit is equal to the [#toString()] representation
    /// of this indentation [at level][#atLevel(int)] 1.
    ///
    /// @return The unit of this indentation.
    public String getUnit() {
        return cache[1].value;
    }

    /// Returns this indentation at the specified level.
    ///
    /// @param level The indentation level.
    /// @return This indentation at the specified level.
    /// @implNote This will return cached `Indentation` instances if available.
    public Indentation atLevel(final int level) {
        if (level == this.level) {
            return this;
        } else if (level < cache.length) {
            if (level < 0) {
                throw new IllegalArgumentException("Indentation level may not be negative: " + level);
            }
            return cache[level];
        }

        final int unitLength = cache[1].length();
        if (unitLength == 0) {
            return new Indentation(level, "", cache);
        }

        // Copy larger of: 'this.value' or last-cached repeatedly until the buffer is filled.
        int remaining = level * unitLength;
        final StringBuilder buffer = new StringBuilder(remaining);
        final String block = this.level >= cache.length ? this.value : cache[cache.length - 1].value;
        while (remaining > block.length()) {
            buffer.append(block);
            remaining -= block.length();
        }
        buffer.append(block, 0, remaining);
        return new Indentation(level, buffer.toString(), cache);
    }

    /// The length of this indentation.
    ///
    /// By definition, this must be equal to `getLevel() * getUnit().length()`.
    ///
    /// @return The length of this indentation in numbers of characters.
    @Override
    public int length() {
        return value.length();
    }

    /// Returns the char value at the specified index.
    ///
    /// @param index The index of the character to be returned.
    /// @return The character at the specified index in this indentation.
    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.substring(start, end);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Indentation
                && this.level == ((Indentation) other).level)
                && this.getUnit().equals(((Indentation) other).getUnit());
    }

    @Override
    public String toString() {
        return value;
    }

    /// Returns a compact serialization proxy for this indentation.
    ///
    /// The proxy only contains the indentation [unit][#getUnit()] and [level][#getLevel()].
    ///
    /// Deserialization of this proxy calls `Indentation.of(unit).atLevel(level)`
    /// allowing cached Indentation instances to be restored.
    ///
    /// @return A serialization proxy for this indentation.
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    ///  Serialization proxy containing _unit_ and _level_.
    private static final class SerializationProxy implements Serializable {
        private final String unit;
        private final int level;

        private SerializationProxy(Indentation indentation) {
            this.unit = indentation.getUnit();
            this.level = indentation.getLevel();
        }

        /// Recreates an equal Indentation to the original by calling
        /// `Indentation.of(unit).atLevel(level)` enabling efficient reuse of constants and caches.
        ///
        /// @return Resolved Indentation instance.
        private Object readResolve() {
            return Indentation.of(unit).atLevel(level);
        }
    }
}
