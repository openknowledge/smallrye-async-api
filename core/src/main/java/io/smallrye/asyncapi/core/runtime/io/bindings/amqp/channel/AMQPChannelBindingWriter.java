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
package io.smallrye.asyncapi.core.runtime.io.bindings.amqp.channel;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.asyncapi.core.runtime.io.JsonUtil;
import io.smallrye.asyncapi.core.runtime.io.bindings.ChannelBindingsConstants;
import io.smallrye.asyncapi.spec.models.binding.amqp.AMQPChannelBinding;

public class AMQPChannelBindingWriter {

    public AMQPChannelBindingWriter() {
    }

    public static void writeAMQPChannelBinding(ObjectNode parent, AMQPChannelBinding model) {
        if (model == null) {
            return;
        }
        ObjectNode node = JsonUtil.objectNode();
        parent.set(ChannelBindingsConstants.PROP_AMQP_BINDING, node);

        JsonUtil.stringProperty(node, AMQPChannelBindingConstants.PROP_IS, model.getIs());
        ExchangeWriter.writeExchange(node, model.getExchange());
        QueueWriter.writeQueue(node, model.getQueue());
        JsonUtil.stringProperty(node, AMQPChannelBindingConstants.PROP_BINDING_VERSION, model.getBindingVersion());
    }
}
