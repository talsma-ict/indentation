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
package nl.talsmasoftware.indent4j.io;

import java.io.Flushable;
import java.util.concurrent.atomic.AtomicBoolean;

class FlushableAutoCloseableAppendable implements Flushable, AutoCloseable, Appendable {
    final AtomicBoolean flushed = new AtomicBoolean(false);
    final AtomicBoolean closed = new AtomicBoolean(false);
    final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void flush() {
        flushed.set(true);
    }

    @Override
    public void close() throws Exception {
        closed.set(true);
    }

    @Override
    public Appendable append(CharSequence csq) {
        stringBuilder.append(csq);
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) {
        stringBuilder.append(csq, start, end);
        return this;
    }

    @Override
    public Appendable append(char c) {
        stringBuilder.append(c);
        return this;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
