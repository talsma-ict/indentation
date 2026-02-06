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
package nl.talsmasoftware.indentation.io;


import nl.talsmasoftware.indentation.Indentation;

import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

/**
 * Writer implementation that indents each new line with a specified indentation.
 *
 * <p>
 * The writing itself can be delegated to any other {@link Appendable} implementation.
 *
 * <p>
 * The writer provides <strong>no buffering</strong>.
 * To buffer the output, make sure the delegate is a {@link java.io.BufferedWriter BufferedWriter}.
 *
 * @author Sjoerd Talsma
 */
public class IndentingWriter extends Writer {

    private final Appendable delegate;
    private final AtomicReference<Indentation> indentation;
    private volatile char lastWritten;

    public IndentingWriter(Appendable delegate, Indentation indentation) {
        super(requireNonNull(delegate, "Delegate appendable is required."));
        this.delegate = delegate;
        this.indentation = new AtomicReference<>(requireNonNull(indentation, "Indentation is required."));
        this.lastWritten = '\u0000'; // Initialize to null character.
    }

    /**
     * Increase the indentation level of this writer by one.
     *
     * @return This writer, for chaining, with increaded indentation level.
     * @see Indentation#indent()
     */
    public IndentingWriter indent() {
        indentation.getAndUpdate(Indentation::indent);
        return this;
    }

    /**
     * Decrease the indentation level of this writer by one.
     *
     * @return This writer, for chaining, with decreased indentation level.
     * @see Indentation#unindent()
     */
    public IndentingWriter unindent() {
        indentation.getAndUpdate(Indentation::unindent);
        return this;
    }

    /**
     * The last-written character.
     *
     * <p>
     * The last-written character is remembered to determine
     * whether indentation must be inserted before the next character.
     *
     * <p>
     * If no characters were written to this writer yet, the null-character ({@code '\u0000'}) is returned.
     *
     * @return The last-written character, or the null-character ({@code '\u0000'}) if no characters were written yet.
     */
    protected char getLastWritten() {
        return lastWritten;
    }

    /**
     * The current indentation.
     *
     * <p>
     * To obtain the current indentation <em>level</em>, use {@link Indentation#getLevel()} of the returned indentation.
     *
     * @return The current indentation, never {@code null}.
     * @see Indentation#getLevel()
     */
    protected Indentation getIndentation() {
        return indentation.get();
    }

    protected void setIndentation(Indentation indentation) {
        this.indentation.set(requireNonNull(indentation, "Indentation is required."));
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len > 0) {
            char ch;
            synchronized (lock) {
                for (int i = 0; i < len; i++) {
                    ch = cbuf[off + i];
                    if (isEolOrNullChar(lastWritten) && !isEolOrNullChar(ch)) {
                        delegate.append(indentation.get());
                    }
                    delegate.append(ch);
                    lastWritten = ch;
                }
            }
        }
    }

    @Override
    public void flush() throws IOException {
        if (delegate instanceof Flushable) {
            ((Flushable) delegate).flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (delegate instanceof AutoCloseable) {
            try {
                ((AutoCloseable) delegate).close();
            } catch (IOException | RuntimeException rethrowable) {
                throw rethrowable;
            } catch (Exception e) {
                throw new IOException("Could not close " + this + ": " + e.getMessage(), e);
            }
        }
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    /**
     * Tests whether the character is an end-of-line or null character.
     *
     * <p>
     * The null character is used as last-written for new, 'still empty' indenting writers.
     *
     * @param ch The character to be tested.
     * @return <code>true</code> if the character was an end-of-line or null character, <code>false</code> otherwise.
     */
    private static boolean isEolOrNullChar(char ch) {
        return ch == '\r' || ch == '\n' || ch == '\u0000';
    }

}
