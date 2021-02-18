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

import java.util.HashMap;
import java.util.Map;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;
import org.junit.Before;
import org.junit.BeforeClass;

import io.smallrye.asyncapi.core.api.util.ClassLoaderUtil;
import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;

public class ReactiveMessagingDataObjectScannerTestBase extends IndexScannerTestBase {

    protected AnnotationScannerContext context;

    protected static Index index;

    @BeforeClass
    public static void createIndex() {
        Indexer indexer = new Indexer();

        // Stand-in stuff
        index(indexer, "io/smallrye/asyncapi/core/runtime/scanner/CollectionStandin.class");
        index(indexer, "io/smallrye/asyncapi/core/runtime/scanner/IterableStandin.class");
        index(indexer, "io/smallrye/asyncapi/core/runtime/scanner/MapStandin.class");

        index = indexer.complete();
    }

    @Before
    public void createContext() {
        context = new AnnotationScannerContext(index, ClassLoaderUtil.getDefaultClassLoader(), emptyConfig());
    }

    public FieldInfo getFieldFromKlazz(String containerName, String fieldName) {
        ClassInfo container = index.getClassByName(DotName.createSimple(containerName));
        return container.field(fieldName);
    }
}
