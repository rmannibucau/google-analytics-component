/**
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.talend.components.google.analytics.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsonGeneratorImpl extends JsonGenerator {
    private final JsonFactory factory;
    private final javax.json.stream.JsonGenerator delegate;

    // to manage the diff between write(key, value) and writeKey()writeValue() APIs
    private final LinkedList<String> keys = new LinkedList<>();
    private final LinkedList<Boolean> containerIsArray = new LinkedList<>();

    @Override
    public JsonFactory getFactory() {
        return factory;
    }

    @Override
    public void flush() {
        delegate.flush();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void writeStartArray() {
        if (containerIsArray.getLast()) {
            delegate.writeStartArray();
        } else {
            delegate.writeStartArray(keys.removeLast());
        }
        containerIsArray.add(true);
    }

    @Override
    public void writeEndArray() {
        delegate.writeEnd();
        containerIsArray.removeLast();
    }

    @Override
    public void writeStartObject() {
        if (containerIsArray.isEmpty()) {
            delegate.writeStartObject();
        } else if (containerIsArray.getLast()) {
            delegate.writeStartObject();
        } else {
            delegate.writeStartArray(keys.removeLast());
        }
        containerIsArray.add(false);
    }

    @Override
    public void writeEndObject() {
        delegate.writeEnd();
        containerIsArray.removeLast();
    }

    @Override
    public void writeFieldName(final String name) {
        keys.add(name);
    }

    @Override
    public void writeNull() {
        if (containerIsArray.getLast()) {
            delegate.writeNull();
        } else {
            delegate.writeNull(keys.removeLast());
        }
    }

    @Override
    public void writeString(final String value) {
        if (containerIsArray.getLast()) {
            delegate.write(value);
        } else {
            delegate.write(keys.removeLast(), value);
        }
    }

    @Override
    public void writeBoolean(final boolean state) {
        if (containerIsArray.getLast()) {
            delegate.write(state);
        } else {
            delegate.write(keys.removeLast(), state);
        }
    }

    @Override
    public void writeNumber(final int v) {
        if (containerIsArray.getLast()) {
            delegate.write(v);
        } else {
            delegate.write(keys.removeLast(), v);
        }
    }

    @Override
    public void writeNumber(final long v) {
        if (containerIsArray.getLast()) {
            delegate.write(v);
        } else {
            delegate.write(keys.removeLast(), v);
        }
    }

    @Override
    public void writeNumber(final BigInteger v) {
        if (containerIsArray.getLast()) {
            delegate.write(v);
        } else {
            delegate.write(keys.removeLast(), v);
        }
    }

    @Override
    public void writeNumber(final float v) {
        if (containerIsArray.getLast()) {
            delegate.write(v);
        } else {
            delegate.write(keys.removeLast(), v);
        }
    }

    @Override
    public void writeNumber(final double v) {
        if (containerIsArray.getLast()) {
            delegate.write(v);
        } else {
            delegate.write(keys.removeLast(), v);
        }
    }

    @Override
    public void writeNumber(final BigDecimal v) {
        if (containerIsArray.getLast()) {
            delegate.write(v);
        } else {
            delegate.write(keys.removeLast(), v);
        }
    }

    @Override
    public void writeNumber(final String encodedValue) {
        if (containerIsArray.getLast()) {
            delegate.write(encodedValue);
        } else {
            delegate.write(keys.removeLast(), encodedValue);
        }
    }
}
