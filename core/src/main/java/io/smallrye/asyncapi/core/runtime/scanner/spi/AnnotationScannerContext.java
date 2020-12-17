/*
 * Copyright 2019 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.smallrye.asyncapi.core.runtime.scanner.spi;

import java.util.Collections;
import java.util.List;

import org.jboss.jandex.IndexView;

import io.smallrye.asyncapi.core.api.AsyncApiConfig;
import io.smallrye.asyncapi.core.runtime.scanner.AnnotationScannerExtension;
import io.smallrye.asyncapi.core.runtime.scanner.FilteredIndexView;
import io.smallrye.asyncapi.core.runtime.scanner.dataobject.AugmentedIndexView;

/**
 * Context for scanners.
 */
public class AnnotationScannerContext {

    private final FilteredIndexView index;

    private final AugmentedIndexView augmentedIndex;

    private final List<AnnotationScannerExtension> extensions;

    private final AsyncApiConfig config;

    private final ClassLoader classLoader;

    public AnnotationScannerContext(FilteredIndexView index, ClassLoader classLoader,
            List<AnnotationScannerExtension> extensions,
            AsyncApiConfig config) {
        this.index = index;
        this.augmentedIndex = AugmentedIndexView.augment(index);
        this.classLoader = classLoader;
        this.extensions = extensions;
        this.config = config;
    }

    public AnnotationScannerContext(IndexView index, ClassLoader classLoader, AsyncApiConfig config) {
        this(new FilteredIndexView(index, config), classLoader, Collections.emptyList(), config);
    }

    public FilteredIndexView getIndex() {
        return index;
    }

    public AugmentedIndexView getAugmentedIndex() {
        return augmentedIndex;
    }

    public List<AnnotationScannerExtension> getExtensions() {
        return extensions;
    }

    public AsyncApiConfig getConfig() {
        return config;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public String toString() {
        return "AnnotationScannerContext{" + "index=" + index + ", augmentedIndex=" + augmentedIndex + ", extensions="
                + extensions
                + ", config=" + config + ", classLoader=" + classLoader + '}';
    }
}
