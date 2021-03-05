/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package io.smallrye.asyncapi.reactivemessaging.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;
import org.jboss.logging.Logger;
import org.json.JSONException;
import org.junit.After;
import org.skyscreamer.jsonassert.JSONAssert;

import io.smallrye.asyncapi.core.api.AsyncApiConfig;
import io.smallrye.asyncapi.core.api.AsyncApiConfigImpl;
import io.smallrye.asyncapi.core.api.models.AsyncAPIImpl;
import io.smallrye.asyncapi.core.api.models.ComponentsImpl;
import io.smallrye.asyncapi.core.runtime.AsyncApiFormat;
import io.smallrye.asyncapi.core.runtime.io.AsyncApiSerializer;
import io.smallrye.asyncapi.core.runtime.scanner.AsyncApiAnnotationScanner;
import io.smallrye.asyncapi.core.runtime.scanner.SchemaRegistry;
import io.smallrye.asyncapi.spec.models.AsyncAPI;
import io.smallrye.asyncapi.spec.models.schema.Schema;

public class IndexScannerTestBase {
    private static final Logger LOG = Logger.getLogger(IndexScannerTestBase.class);

    @After
    public void removeSchemaRegistry() {
        SchemaRegistry.remove();
    }

    protected static String pathOf(Class<?> clazz) {
        return clazz.getName()
                .replace('.', '/')
                .concat(".class");
    }

    protected static void indexDirectory(Indexer indexer, String baseDir) {
        InputStream directoryStream = tcclGetResourceAsStream(baseDir);
        BufferedReader reader = new BufferedReader(new InputStreamReader(directoryStream));
        reader.lines()
                .filter(resName -> resName.endsWith(".class"))
                .map(resName -> Paths.get(baseDir, resName))
                .forEach(path -> index(indexer, path.toString()));
    }

    private static InputStream tcclGetResourceAsStream(String path) {
        return Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);
    }

    public static Index indexOf(Class<?>... classes) {
        Indexer indexer = new Indexer();

        for (Class<?> klazz : classes) {
            index(indexer, pathOf(klazz));
        }

        return indexer.complete();
    }

    protected static void index(Indexer indexer, String resName) {
        try {
            InputStream stream = tcclGetResourceAsStream(resName);
            indexer.index(stream);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    protected static DotName componentize(String className) {
        boolean innerClass = className.contains("$");
        DotName prefix = null;
        String[] components = className.split("[\\.$]");
        int lastIndex = components.length - 1;

        for (int i = 0; i < components.length; i++) {
            String localName = components[i];

            if (i < lastIndex) {
                prefix = DotName.createComponentized(prefix, localName);
            } else {
                prefix = DotName.createComponentized(prefix, localName, innerClass);
            }
        }

        return prefix;
    }

    public static void printToConsole(String entityName, Schema schema) throws IOException {
        // Remember to set debug level logging.
        LOG.debug(schemaToString(entityName, schema));
        System.out.println(schemaToString(entityName, schema));
    }

    public static void printToConsole(AsyncAPI aai) throws IOException {
        // Remember to set debug level logging.
        LOG.debug(AsyncApiSerializer.serialize(aai, AsyncApiFormat.JSON));
        System.out.println(AsyncApiSerializer.serialize(aai, AsyncApiFormat.JSON));
    }

    public static String schemaToString(String entityName, Schema schema) throws IOException {
        Map<String, Schema> map = new HashMap<>();
        map.put(entityName, schema);
        AsyncAPIImpl aai = new AsyncAPIImpl();
        ComponentsImpl comp = new ComponentsImpl();
        comp.setSchemas(map);
        aai.setComponents(comp);
        return AsyncApiSerializer.serialize(aai, AsyncApiFormat.JSON);
    }

    public static void assertJsonEquals(String entityName, String expectedResource, Schema actual)
            throws JSONException, IOException {
        URL resourceUrl = IndexScannerTestBase.class.getResource(expectedResource);
        JSONAssert.assertEquals(loadResource(resourceUrl), schemaToString(entityName, actual), true);
    }

    public static void assertJsonEquals(String expectedResource, AsyncAPI actual) throws JSONException, IOException {
        URL resourceUrl = IndexScannerTestBase.class.getResource(expectedResource);
        JSONAssert.assertEquals(loadResource(resourceUrl), AsyncApiSerializer.serialize(actual, AsyncApiFormat.JSON), true);
    }

    public static void assertJsonEquals(String expectedResource, Class<?>... classes) throws IOException, JSONException {
        Index index = indexOf(classes);
        AsyncApiAnnotationScanner scanner = new AsyncApiAnnotationScanner(dynamicConfig(new HashMap<String, Object>()), index);
        AsyncAPI result = scanner.scan();
        printToConsole(result);
        assertJsonEquals(expectedResource, result);
    }

    public static String loadResource(URL testResource) throws IOException {
        return IOUtils.toString(testResource, "UTF-8");
    }

    public static AsyncApiConfig emptyConfig() {
        return dynamicConfig(Collections.emptyMap());
    }

    public static AsyncApiConfig dynamicConfig(String key, Object value) {
        Map<String, Object> config = new HashMap<>(1);
        config.put(key, value);
        return dynamicConfig(config);
    }

    @SuppressWarnings("unchecked")
    public static AsyncApiConfig dynamicConfig(Map<String, Object> properties) {
        return new AsyncApiConfigImpl(new Config() {
            @Override
            public <T> T getValue(String propertyName, Class<T> propertyType) {
                return (T) properties.get(propertyName);
            }

            @Override
            public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
                return (Optional<T>) Optional.ofNullable(properties.getOrDefault(propertyName, null));
            }

            @Override
            public Iterable<String> getPropertyNames() {
                return properties.keySet();
            }

            @Override
            public Iterable<ConfigSource> getConfigSources() {
                // Not needed for this test case
                return Collections.emptyList();
            }

            @Override
            public ConfigValue getConfigValue(String propertyName) {
                return new ConfigValue() {
                    @Override
                    public String getName() {
                        return propertyName;
                    }

                    @Override
                    public String getValue() {
                        return (String) properties.get(propertyName);
                    }

                    @Override
                    public String getRawValue() {
                        return getValue();
                    }

                    @Override
                    public String getSourceName() {
                        // Not needed for this test case
                        return null;
                    }

                    @Override
                    public int getSourceOrdinal() {
                        return 0;
                    }
                };
            }

            @Override
            public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
                return Optional.empty();
            }

            @Override
            public <T> T unwrap(Class<T> type) {
                throw new IllegalArgumentException();
            }
        });
    }
}
