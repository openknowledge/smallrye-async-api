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

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;

public class App {

    @Outgoing("cloudevents-out")
    public Multi<String> toCloudEvents() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .map(x -> "Hello World");
    }

    @Incoming("cloudevents-in")
    public CompletionStage<Void> process(Message<String> msg) {
        IncomingCloudEventMetadata<Integer> cloudEventMetadata = msg.getMetadata(IncomingCloudEventMetadata.class)
                .orElseThrow(() -> new IllegalArgumentException("Expected a Cloud Event"));

        System.out.println(
                String.format("Received Cloud Events (spec-version: %s): source:  '%s', type: '%s', subject: '%s' , data: '%s'",
                        cloudEventMetadata.getSpecVersion(),
                        cloudEventMetadata.getSource(),
                        cloudEventMetadata.getType(),
                        cloudEventMetadata.getSubject().orElse("no subject"),
                        cloudEventMetadata.getData()));

        return msg.ack();
    }

    @Incoming("cloudevents-in")
    public CompletionStage<Void> process2(Message<String> msg) {
        System.out.println(msg.getPayload());

        return msg.ack();
    }
}
