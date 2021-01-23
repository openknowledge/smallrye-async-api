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
package io.smallrye.asyncapi.core.runtime.io.bindings.amqp.channel;

public class AMQPChannelBindingConstants {

    // ChannelBindings
    public static final String PROP_BINDINGS = "bindings";

    public static final String PROP_IS = "is";

    public static final String PROP_EXCHANGE = "exchange";

    public static final String PROP_QUEUE = "queue";

    public static final String PROP_BINDING_VERSION = "bindingVersion";

    public static final String DEFAULT_IS = "routingKey";

    // Exchange
    public static final String PROP_NAME = "name";

    public static final String PROP_EXCHANGE_TYPE = "exchangeType";

    public static final String PROP_DURABLE = "durable";

    public static final String PROP_AUTO_DELETE = "autoDelete";

    public static final String PROP_VIRTUAL_HOST = "virtualHost";

    // Queue
    public static final String PROP_EXCLUSIVE = "exclusive";

    public AMQPChannelBindingConstants() {
    }
}
