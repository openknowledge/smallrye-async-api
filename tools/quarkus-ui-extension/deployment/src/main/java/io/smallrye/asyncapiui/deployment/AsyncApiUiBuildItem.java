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

package io.smallrye.asyncapiui.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

public final class AsyncApiUiBuildItem extends SimpleBuildItem {

    private final String asyncApiUiFinalDestination;

    private final String asyncApiUiPath;

    public AsyncApiUiBuildItem(String swaggerUiFinalDestination, String swaggerUiPath) {
        this.asyncApiUiFinalDestination = swaggerUiFinalDestination;
        this.asyncApiUiPath = swaggerUiPath;
    }

    public String getAsyncApiUiFinalDestination() {
        return asyncApiUiFinalDestination;
    }

    public String getAsyncApiUiPath() {
        return asyncApiUiPath;
    }
}
