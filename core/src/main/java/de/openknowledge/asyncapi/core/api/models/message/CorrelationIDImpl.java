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
 *
 *
 */
package de.openknowledge.asyncapi.core.api.models.message;

import de.openknowledge.asyncapi.core.api.models.ExtensibleImpl;
import de.openknowledge.asyncapi.core.api.models.ModelImpl;
import io.smallrye.asyncapi.spec.models.message.CorrelationID;

/**
 * An implementation of the {@link CorrelationID} AsyncAPI model interface.
 */
public class CorrelationIDImpl extends ExtensibleImpl<CorrelationID> implements CorrelationID, ModelImpl {

    private String description;

    private String location;

    /**
     * @see CorrelationID#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @see CorrelationID#setDescription(String description)
     */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @see CorrelationID#getLocation()
     */
    public String getLocation() {
        return location;
    }

    /**
     * @see CorrelationID#setLocation(String location)
     */
    public void setLocation(final String location) {
        this.location = location;
    }
}
