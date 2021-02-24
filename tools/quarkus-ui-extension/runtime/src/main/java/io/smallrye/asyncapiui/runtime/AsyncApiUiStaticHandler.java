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
package io.smallrye.asyncapiui.runtime;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Handling static AsyncApi UI content
 */
public class AsyncApiUiStaticHandler implements Handler<RoutingContext> {

    private String asyncApiUiFinalDestination;

    private String asyncapiUiPath;

    public AsyncApiUiStaticHandler() {
    }

    public AsyncApiUiStaticHandler(String swaggerUiFinalDestination, String swaggerUiPath) {
        this.asyncApiUiFinalDestination = swaggerUiFinalDestination;
        this.asyncapiUiPath = swaggerUiPath;
    }

    public String getAsyncApiUiFinalDestination() {
        return asyncApiUiFinalDestination;
    }

    public void setAsyncApiUiFinalDestination(String asyncApiUiFinalDestination) {
        this.asyncApiUiFinalDestination = asyncApiUiFinalDestination;
    }

    public String getAsyncapiUiPath() {
        return asyncapiUiPath;
    }

    public void setAsyncapiUiPath(String asyncapiUiPath) {
        this.asyncapiUiPath = asyncapiUiPath;
    }

    @Override
    public void handle(RoutingContext event) {
        StaticHandler staticHandler = StaticHandler.create()
                .setAllowRootFileSystemAccess(true)
                .setWebRoot(asyncApiUiFinalDestination)
                .setDefaultContentEncoding("UTF-8");

        if (event.normalisedPath().length() == asyncapiUiPath.length()) {
            event.response().setStatusCode(302);
            event.response().headers().set(HttpHeaders.LOCATION, asyncapiUiPath + "/");
            event.response().end();

            return;
        } else if (event.normalisedPath().length() == asyncapiUiPath.length() + 1) {
            event.reroute(asyncapiUiPath + "/index.html");
            return;
        }

        staticHandler.handle(event);
    }
}
