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

/// Writer implementation that prepends each first character on a new line with a specified [Indentation].
///
/// The writing itself can be delegated to any other [Appendable] implementation.
///
/// This writer provides **no buffering** out of the box.
/// To buffer the output, make sure the delegate is some type of [BufferedWriter][java.io.BufferedWriter]
///
/// @author Sjoerd Talsma
public class IndentingWriter extends Writer {

    private final Appendable delegate;
    private final AtomicReference<Indentation> indentation;
    private volatile char lastWritten;

    /// Constructor for a new indenting writer.
    ///
    /// Writes output to the specified `delegate`, applying the specified `indentation` before each
    /// first character on a new line.
    ///
    /// @param delegate    The delegate to write the output to.
    /// @param indentation The initial indentation to apply to this writer.
    public IndentingWriter(Appendable delegate, Indentation indentation) {
        super(requireNonNull(delegate, "Delegate appendable is required."));
        this.delegate = delegate;
        this.indentation = new AtomicReference<>(requireNonNull(indentation, "Indentation is required."));
        this.lastWritten = '\u0000'; // Initialize to null character.
    }

    /// Increase the indentation level of this writer by one.
    ///
    /// @return Reference to this writer for method chaining purposes.
    /// @see #unindent()
    /// @see Indentation
    public IndentingWriter indent() {
        indentation.getAndUpdate(Indentation::indent);
        return this;
    }

    /// Decrease the indentation level of this writer by one.
    ///
    /// Unindenting cannot result in a negative indentation level.
    /// In other words, if the indentation level is already `0`, calling `unindent()` will have no effect.
    ///
    /// @return Reference to this writer for method chaining purposes.
    /// @see #indent()
    /// @see Indentation
    public IndentingWriter unindent() {
        indentation.getAndUpdate(Indentation::unindent);
        return this;
    }

    /// The last-written character.
    ///
    /// The writer remembers the last-written character
    /// to determine whether indentation should be applied before the next character is written.
    ///
    /// If no characters were written to this writer yet, the null-character (`\u0000`) is returned.
    ///
    /// @return The last-written character, or the null-character (`\u0000`) if no characters were written yet.
    protected char getLastWritten() {
        return lastWritten;
    }

    /// The current indentation for this writer.
    ///
    /// To get the current indentation _level_, use [getLevel()][Indentation#getLevel()] on the returned indentation.
    ///
    /// Please beware that it may be possible to create _race conditions_ using [#getIndentation()]
    /// and [#setIndentation(Indentation)] if the indentation somehow changes between these calls.
    /// The [#indent()] / [#unindent()] operations are always applied atomically.
    ///
    /// @return The current indentation (never `null`).
    /// @see #indent()
    /// @see #unindent()
    protected Indentation getIndentation() {
        return indentation.get();
    }

    /// Sets the current indentation for this writer.
    ///
    /// This new indentation will be applied before each first character on a new line.
    ///
    /// Please beware that it may be possible to create _race conditions_ using [#getIndentation()]
    /// and [#setIndentation(Indentation)] if the indentation somehow changes between these calls.
    /// The [#indent()] / [#unindent()] operations are always applied atomically.
    ///
    /// @param indentation The new indentation to use for this writer.
    /// @see #indent()
    /// @see #unindent()
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

    /// Test whether the character is an end-of-line or null character.
    ///
    /// The null character (`\u0000`) is used as initial value for 'last-written character' in new,
    /// 'still empty' indenting writers.
    ///
    /// @param ch The character to be tested.
    /// @return `true` if the character was an end-of-line or null character, `false` otherwise.
    private static boolean isEolOrNullChar(char ch) {
        return ch == '\r' || ch == '\n' || ch == '\u0000';
    }

}
