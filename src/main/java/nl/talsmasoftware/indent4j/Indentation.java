/*
 * Copyright 2025 Talsma ICT
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
package nl.talsmasoftware.indent4j;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

/**
 * Indentation to prepend to characters whenever printing on a new line.
 *
 * <p>
 * Indentation implements an immutable {@link CharSequence}.
 * Indenting and unindenting returns <em>other</em> object instances
 * and will <strong>not</strong> modify the original indentation itself.
 *
 * <p>
 * Indentation is serializable and will serialize minimally by retaining only the indentation unit and level.
 * Deserialization uses {@code Indentation.of(unit).atLevel(level)} to enable cache re-use for common indentations.
 *
 * @author Sjoerd Talsma
 */
public final class Indentation implements CharSequence, Serializable {
    /**
     * Empty indentation, initialized at level 0. The indentation level is maintained and remains empty at all levels.
     */
    public static final Indentation EMPTY = createIndentation("", 2);
    /**
     * Indentation by tab characters, initialized at level 0.
     */
    public static final Indentation TABS = createIndentation("\t", 20);
    /**
     * Indentation by two spaces, initialized at level 0.
     */
    public static final Indentation TWO_SPACES = createIndentation("  ", 20);
    /**
     * Indentation by four spaces, initialized at level 0.
     */
    public static final Indentation FOUR_SPACES = createIndentation("    ", 20);

    private final transient int level;
    private final transient String value;
    private final transient Indentation[] cache;

    private Indentation(int level, String value, Indentation[] cache) {
        this.level = level;
        this.value = value;
        this.cache = cache;
    }

    /**
     * Return an indentation, initialized at level 0.
     *
     * <p>
     * Common indentations are available as static constants: {@link #TABS}, {@link #TWO_SPACES}, {@link #FOUR_SPACES}.
     *
     * @param unit The indentation unit that will be repeated for each level of indentation.
     * @return The indentation.
     */
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

        return createIndentation(unit.toString(), 5);
    }

    private static Indentation createIndentation(final String unit, final int cacheSize) {
        Indentation[] cache = new Indentation[Math.max(2, cacheSize)]; // getUnit requires cache size >= 2
        cache[0] = new Indentation(0, "", cache);
        for (int i = 1; i < cache.length; i++) {
            cache[i] = new Indentation(i, cache[i - 1].value + unit, cache);
        }
        return cache[0];
    }

    /**
     * Increase the indentation level by one.
     *
     * @return The indentation with increased level.
     * @implSpec The original indentation object is not modified, instead another indentation instance is returned.
     */
    public Indentation indent() {
        return atLevel(level + 1);
    }

    /**
     * Decrease the indentation level by one.
     *
     * <p>
     * The level will never become negative. Calling {@code unindent()} on an indentation at level 0
     * will simply return the same indentation.
     *
     * @return The indentation with decreased level.
     */
    public Indentation unindent() {
        return level == 0 ? this : atLevel(level - 1);
    }

    public int getLevel() {
        return level;
    }

    public String getUnit() {
        return cache[1].value;
    }

    public Indentation atLevel(int level) {
        if (level < 0) {
            throw new IllegalArgumentException("Indentation level may not be negative: " + level);
        } else if (level == this.level) {
            return this;
        } else if (level < cache.length) {
            return cache[level];
        }

        final String unit = getUnit();
        if (unit.isEmpty()) {
            return new Indentation(level, "", cache);
        }
        final StringBuilder indentationBuilder = new StringBuilder(level * unit.length());
        indentationBuilder.append(cache[cache.length - 1].value);
        for (int i = level - cache.length; i >= 0; i--) {
            indentationBuilder.append(unit);
        }
        return new Indentation(level, indentationBuilder.toString(), cache);
    }

    @Override
    public int length() {
        return value.length();
    }

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

    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    private static final class SerializationProxy implements Serializable {
        private final String unit;
        private final int level;

        private SerializationProxy(Indentation indentation) {
            this.unit = indentation.getUnit();
            this.level = indentation.getLevel();
        }

        private Object readResolve() {
            return Indentation.of(unit).atLevel(level);
        }
    }
}
