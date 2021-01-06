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
