package io.quarkus.asyncapi.deployment;

import io.quarkus.builder.item.SimpleBuildItem;
import io.smallrye.asyncapi.core.runtime.scanner.FilteredIndexView;

/**
 * The filtered Jandex index of the AsyncApi
 */
public final class AsyncApiFilteredIndexViewBuildItem extends SimpleBuildItem {

    private final FilteredIndexView index;

    public AsyncApiFilteredIndexViewBuildItem(FilteredIndexView index) {
        this.index = index;
    }

    public FilteredIndexView getIndex() {
        return index;
    }
}
