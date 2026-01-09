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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IndentingWriterTest {
    @Test
    @DisplayName("new IndentingWriter without delegate: Must throw NullPointerException with sensible message.")
    void createWriterWithoutDelegate() {
        assertThatThrownBy(() -> new IndentingWriter(null, Indentation.EMPTY))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Delegate appendable is required.");
    }

    @Test
    @DisplayName("new IndentingWriter without indentation: Must throw NullPointerException with sensible message.")
    void createWriterWithoutIndentation() {
        assertThatThrownBy(() -> new IndentingWriter(System.out, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Indentation is required.");
    }

    @Test
    @DisplayName("getLastWritten: Must return null character for new writer.")
    void lastWrittenReturnsNullCharForNewWriter() {
        IndentingWriter subject = new IndentingWriter(System.out, Indentation.EMPTY); // System.out --> don't close.
        assertThat(subject.getLastWritten()).isEqualTo('\u0000');
    }

    @Test
    @DisplayName("getLastWritten: Must return last written character.")
    void lastWrittenReturnsLastWrittenChar() throws IOException {
        try (IndentingWriter subject = new IndentingWriter(new StringWriter(), Indentation.EMPTY)) {
            assertThat(subject.getLastWritten()).isEqualTo('\u0000');
            subject.write('a');
            assertThat(subject.getLastWritten()).isEqualTo('a');

            subject.write("abc");
            assertThat(subject.getLastWritten()).isEqualTo('c');
        }
    }

    @Test
    @DisplayName("getIndentation: Must return the current indentation.")
    void getIndentation() throws IOException {
        try (IndentingWriter subject = new IndentingWriter(new StringWriter(), Indentation.TABS)) {
            for (int i = 0; i < 100; i++) {
                final int level = i;
                subject.setIndentation(() -> subject.getIndentation().atLevel(level));
                assertThat(subject.getIndentation()).isEqualTo(Indentation.TABS.atLevel(level));
                subject.indent();
                assertThat(subject.getIndentation()).isEqualTo(Indentation.TABS.atLevel(level + 1));
                subject.unindent();
                assertThat(subject.getIndentation()).isEqualTo(Indentation.TABS.atLevel(level));
            }
        }
    }

    @Test
    @DisplayName("toString: Must return the delegate's toString.")
    void testToString() throws IOException {
        try (IndentingWriter subject = new IndentingWriter(new StringWriter(), Indentation.TWO_SPACES)) {
            assertThat(subject).hasToString("");

            subject.write("Hello,");
            subject.indent().write("\nworld!\n");
            subject.indent().write("It's time.");
            subject.indent().write("to\nsay");
            subject.unindent().write("\nGoodbye.");
            assertThat(subject).hasToString("Hello,\n  world!\n    It's time.to\n      say\n    Goodbye.");
        }
    }

    @Test
    @DisplayName("write: Empty lines should not be indented.")
    void testEmptyLines() throws IOException {
        try (IndentingWriter subject = new IndentingWriter(new StringWriter(), Indentation.TABS.atLevel(2))) {
            subject.write("Hello!\n\n\nThere.");
            assertThat(subject).hasToString("\t\tHello!\n\n\n\t\tThere.");
        }
    }

    @Test
    @DisplayName("write: Windows-style carriage return+newline must not be separated by indentation.")
    void testCRNL() throws IOException {
        try (IndentingWriter subject = new IndentingWriter(new StringWriter(), Indentation.TABS.atLevel(2))) {
            subject.write("Hello,\r\nthere!\r\n");
            assertThat(subject).hasToString("\t\tHello,\r\n\t\tthere!\r\n");
        }
    }

    @Test
    @DisplayName("write: Must not throw exception when writing empty array with zero length.")
    void writeEmpty() throws IOException {
        try (IndentingWriter subject = new IndentingWriter(new StringWriter(), Indentation.TABS)) {
            assertDoesNotThrow(() -> subject.write(new char[0], 0, 0));
            assertThat(subject).hasToString("");
        }
    }

    @Test
    @DisplayName("flush: Must delegate the flush call if delegate is flushable.")
    void flushFlushable() throws IOException {
        FlushableAutoCloseableAppendable delegate = new FlushableAutoCloseableAppendable();
        try (IndentingWriter subject = new IndentingWriter(delegate, Indentation.TABS)) {
            subject.flush();
        }
        assertThat(delegate.flushed).isTrue();
    }

    @Test
    @DisplayName("flush: Must not throw exception when using non-flushable appendable delegate.")
    void flushNonFlushable() throws IOException {
        Appendable nonFlushable = new StringBuilder();
        try (IndentingWriter subject = new IndentingWriter(nonFlushable, Indentation.TABS)) {
            assertDoesNotThrow(subject::flush);
        }
    }

    @Test
    @DisplayName("close: Must delegate the close call if delegate is AutoCloseable.")
    void closeAutoCloseable() {
        FlushableAutoCloseableAppendable delegate = new FlushableAutoCloseableAppendable();
        IndentingWriter subject = new IndentingWriter(delegate, Indentation.TABS);
        assertDoesNotThrow(subject::close);
        assertThat(delegate.closed).isTrue();
    }

    @Test
    @DisplayName("close: Must not throw exception when using non-closable appendable delegate.")
    void closeNonClosable() throws IOException {
        Appendable nonClosable = new StringBuilder();
        try (IndentingWriter subject = new IndentingWriter(nonClosable, Indentation.TABS)) {
            assertDoesNotThrow(subject::close);
        }
    }

    @Test
    @DisplayName("close: Must rethrow RuntimeExceptions.")
    void closeRethrowsRuntimeException() {
        RuntimeException runtimeException = new RuntimeException("A random RuntimeException thrown by the delegate.");
        Appendable closeFailingAppendable = new FlushableAutoCloseableAppendable() {
            @Override
            public void close() {
                throw runtimeException;
            }
        };

        IndentingWriter subject = new IndentingWriter(closeFailingAppendable, Indentation.TABS);
        assertThatThrownBy(subject::close).isSameAs(runtimeException);
    }

    @Test
    @DisplayName("close: Must rethrow IOExceptions.")
    void closeRethrowsIOException() {
        IOException ioException = new IOException("A random IOException thrown by the delegate.");
        Appendable closeFailingAppendable = new FlushableAutoCloseableAppendable() {
            @Override
            public void close() throws IOException {
                throw ioException;
            }
        };

        IndentingWriter subject = new IndentingWriter(closeFailingAppendable, Indentation.TABS);
        assertThatThrownBy(subject::close).isSameAs(ioException);
    }

    @Test
    @DisplayName("close: Must wrap checked exceptions in IOException.")
    void closeWrapsCheckedException() {
        Exception checkedException = new Exception("Checked exception that is not IOException.");
        Appendable closeFailingAppendable = new FlushableAutoCloseableAppendable() {
            @Override
            public void close() throws Exception {
                throw checkedException;
            }
        };

        IndentingWriter subject = new IndentingWriter(closeFailingAppendable, Indentation.TABS);
        assertThatThrownBy(subject::close)
                .isInstanceOf(IOException.class)
                .hasCause(checkedException);
    }
}
