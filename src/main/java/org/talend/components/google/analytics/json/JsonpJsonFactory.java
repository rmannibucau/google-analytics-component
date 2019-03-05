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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParserFactory;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsonpJsonFactory extends JsonFactory {
    private final JsonParserFactory parserFactory;
    private final JsonGeneratorFactory generatorFactory;

    @Override
    public JsonParser createJsonParser(final InputStream inputStream) {
        return new JsonParserImpl(this, parserFactory.createParser(inputStream));
    }

    @Override
    public JsonParser createJsonParser(final InputStream inputStream, final Charset charset) {
        return new JsonParserImpl(this, parserFactory.createParser(inputStream, charset));
    }

    @Override
    public JsonParser createJsonParser(final String s) {
        return new JsonParserImpl(this, parserFactory.createParser(new StringReader(s)));
    }

    @Override
    public JsonParser createJsonParser(final Reader reader) {
        return new JsonParserImpl(this, parserFactory.createParser(reader));
    }

    @Override
    public JsonGenerator createJsonGenerator(final OutputStream outputStream, final Charset charset) {
        return new JsonGeneratorImpl(this, generatorFactory.createGenerator(outputStream, charset));
    }

    @Override
    public JsonGenerator createJsonGenerator(final Writer writer) {
        return new JsonGeneratorImpl(this, generatorFactory.createGenerator(writer));
    }
}
