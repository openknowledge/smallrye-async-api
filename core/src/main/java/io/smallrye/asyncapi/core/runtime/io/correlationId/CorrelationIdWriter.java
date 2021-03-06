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
package io.smallrye.asyncapi.core.runtime.io.correlationId;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.asyncapi.core.runtime.io.JsonUtil;
import io.smallrye.asyncapi.core.runtime.io.message.MessageConstant;
import io.smallrye.asyncapi.spec.models.message.CorrelationID;

public class CorrelationIdWriter {

    public CorrelationIdWriter() {
    }

    /**
     * Writes the {@link CorrelationID} model to the JSON tree.
     *
     * @param parent the parent json node
     * @param model the CorrelationID model
     */
    public static void writeCorrelationID(ObjectNode parent, CorrelationID model) {
        if (model == null) {
            return;
        }
        ObjectNode node = parent.putObject(MessageConstant.PROP_CORRELATION_ID);

        JsonUtil.stringProperty(node, CorrelationIdConstant.PROP_DESCRIPTION, model.getDescription());
        JsonUtil.stringProperty(node, CorrelationIdConstant.PROP_LOCATION, model.getLocation());
    }
}
