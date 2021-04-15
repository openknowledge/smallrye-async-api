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
package org.acme;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import io.cloudevents.v03.CloudEventImpl;
import io.cloudevents.v03.CloudEventBuilder;

import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.asyncapi.spec.annotations.channel.ChannelItem;
import io.smallrye.asyncapi.spec.annotations.message.Message;
import io.smallrye.asyncapi.spec.annotations.operation.Operation;

/**
 * A bean producing random prices every 5 seconds.
 * The prices are written to a MQTT topic (prices). The MQTT configuration is specified in the application configuration.
 */
@ApplicationScoped
public class PriceGenerator {

    private Random random = new Random();

    @ChannelItem(
        channel = "prices",
        publish = @Operation(
            message = @Message(
                contentType = "text/plain",
                summary = "A random price"
            )
        )
    )
    @Outgoing("generated-price")
    public CloudEventImpl<String> generate() {
        return CloudEventBuilder.<String>builder()
                .withType("org.acme.generate.v1")
                .withId(UUID.randomUUID().toString())
                .withTime(ZonedDateTime.now())
                .withSource(URI.create("acme.org"))
                .withData(String.valueOf(random.nextInt(100)))
                .build();
    }
}
