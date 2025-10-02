/*
 * Copyright 2016-2025 Talsma ICT
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
package nl.talsmasoftware.indent4j.io;


import nl.talsmasoftware.indent4j.Indentation;

import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

import static java.util.Objects.requireNonNull;

/**
 * Writer implementation that will indent each new line with a specified indentation.
 * <p>
 * The writing itself can be delegated to any other {@link Appendable} implementation.
 *
 * @author Sjoerd Talsma
 */
public class IndentingWriter extends Writer {
    // TODO Make indentation a mutable field so the same writer can be reused?

    private final Appendable delegate;
    private Indentation indentation;
    private char lastWritten;

    protected IndentingWriter(Appendable delegate, Indentation indentation) {
        this(delegate, indentation, '\n');
    }

    private IndentingWriter(Appendable delegate, Indentation indentation, char lastWritten) {
        super(requireNonNull(delegate, "Delegate writer is required."));
        this.delegate = delegate;
        this.indentation = indentation == null ? Indentation.TABS : indentation;
        this.lastWritten = lastWritten;
    }

    /**
     * Returns an indenting writer around the given <code>delegate</code>.<br>
     * If the <code>delegate</code> writer is already an indenting writer, it will simply be returned
     * {@link #withIndentation(Indentation) with the specified indentation}.<br>
     * If the <code>delegate</code> writer is not yet an indending writer, a new indenting writer class will be created
     * to wrap the delegate using the specified <code>indentation</code>.
     *
     * @param delegate    The delegate to turn into an indenting writer.
     * @param indentation The indentation to use for the indenting writer.
     * @return The indenting delegate writer.
     */
    public static IndentingWriter wrap(Appendable delegate, Indentation indentation) {
        return new IndentingWriter(delegate, indentation);
    }

    protected Indentation getIndentation() {
        return indentation;
    }

    /**
     * Returns an indenting writer with the new indentation.
     *
     * <p>
     * Please note: Already written lines will not be modified to accomodate the new indentation.
     *
     * @param newIndentation The new indentation to apply to this writer (optional).
     * @return Either this writer if the indentation is already correct,
     * or a new IndentingWriter with the adapted indentation.
     */
    public IndentingWriter withIndentation(Indentation newIndentation) {
        if (newIndentation != null && !newIndentation.equals(this.indentation)) {
            synchronized (lock) {
                this.indentation = newIndentation;
            }
        }
        return this;
    }

    public IndentingWriter indent() {
        return withIndentation(getIndentation().indent());
    }

    public IndentingWriter unindent() {
        return withIndentation(getIndentation().unindent());
    }

    /**
     * Tests whether the character is an end-of-line character.
     *
     * @param ch The character to be tested.
     * @return <code>true</code> if the character was an end-of-line character, <code>false</code> otherwise.
     */
    private static boolean isEol(char ch) {
        return ch == '\r' || ch == '\n';
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len > 0) {
            char ch;
            synchronized (lock) {
                for (int i = 0; i < len; i++) {
                    ch = cbuf[off + i];
                    if (isEol(lastWritten) && !isEol(ch)) {
                        delegate.append(indentation);
                    }
                    delegate.append(ch);
                    lastWritten = ch;
                }
            }
        }
    }

    @Override
    public void flush() throws IOException {
        if (delegate instanceof Flushable) ((Flushable) delegate).flush();
    }

    @Override
    public void close() throws IOException {
        try {
            if (delegate instanceof AutoCloseable) {
                ((AutoCloseable) delegate).close();
            }
        } catch (IOException | RuntimeException rethrowable) {
            throw rethrowable;
        } catch (Exception e) {
            throw new IOException("Could not close " + this + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
