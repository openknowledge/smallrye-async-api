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
package de.openknowledge.asyncapi.core.runtime.io.bindings.mqtt.message;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.openknowledge.asyncapi.core.runtime.io.JsonUtil;
import de.openknowledge.asyncapi.core.runtime.io.bindings.MessageBindingsConstants;
import de.openknowledge.asyncapi.core.runtime.io.bindings.kafka.message.KafkaMessageBindingConstant;
import io.smallrye.asyncapi.spec.models.binding.mqtt.MQTTMessageBinding;

public class MQTTMessageBindingWriter {

    public MQTTMessageBindingWriter() {
    }

    public static void writeMQTTMessageBinding(ObjectNode parent, MQTTMessageBinding model) {
        if (model == null) {
            return;
        }

        ObjectNode node = JsonUtil.objectNode();
        parent.set(MessageBindingsConstants.PROP_MQTT_BINDING, node);

        JsonUtil.stringProperty(node, KafkaMessageBindingConstant.PROP_BINDING_VERSION, model.getBindingVersion());
    }
}