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

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot
public class AsyncApiUiConfig {

    /**
     * The path where AsyncApi UI is available.
     *
     * The value `/` is not allowed as it blocks the application from serving anything else.
     */
    @ConfigItem(defaultValue = "/asyncapi-ui")
    String path;

    /**
     * If this should be included every time. By default this is only included when the application is running
     * in dev mode.
     */
    @ConfigItem(defaultValue = "true")
    boolean alwaysInclude;

}
