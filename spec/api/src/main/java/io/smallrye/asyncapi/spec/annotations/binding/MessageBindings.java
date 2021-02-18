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
package io.smallrye.asyncapi.spec.annotations.binding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.smallrye.asyncapi.spec.annotations.binding.amqp.AMQPMessageBinding;
import io.smallrye.asyncapi.spec.annotations.binding.http.HTTPMessageBinding;
import io.smallrye.asyncapi.spec.annotations.binding.kafka.KafkaMessageBinding;
import io.smallrye.asyncapi.spec.annotations.binding.mqtt.MQTTMessageBinding;
import io.smallrye.asyncapi.spec.annotations.schema.Schema;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MessageBindings {

    /**
     * A array where the items describe protocol-specific definitions for the message.
     *
     * @return bindings of the message
     */
    MessageBinding[] binding() default {};

    /**
     * amqp-specific definitions for the channel.
     *
     * @return amqp bindings of the channel
     */
    AMQPMessageBinding amqp() default @AMQPMessageBinding(messageType = "", contentEncoding = "");

    /**
     * http-specific definitions for the channel.
     *
     * @return http bindings of the channel
     */
    HTTPMessageBinding http() default @HTTPMessageBinding(headers = @Schema());

    /**
     * kafka-specific definitions for the channel.
     *
     * @return kafka bindings of the channel
     */
    KafkaMessageBinding kafka() default @KafkaMessageBinding(key = @Schema);

    /**
     * mqtt-specific definitions for the channel.
     *
     * @return mqtt bindings of the channel
     */
    MQTTMessageBinding mqtt() default @MQTTMessageBinding;
}
