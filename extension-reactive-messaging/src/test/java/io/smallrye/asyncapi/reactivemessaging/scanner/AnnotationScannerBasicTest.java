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
package io.smallrye.asyncapi.reactivemessaging.scanner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.jandex.Index;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.smallrye.asyncapi.core.runtime.scanner.AsyncApiAnnotationScanner;
import io.smallrye.asyncapi.spec.models.AsyncAPI;
import test.io.smallrye.asyncapi.runtime.scanner.resources.GreetingChannel;

public class AnnotationScannerBasicTest extends ReactiveMessagingDataObjectScannerTestBase {

    private static final String OUTGOING_HELLO_CONNECTOR = "mp.messaging.outgoing.hello.connector";
    private static final String OUTGOING_HELLO_TOPIC = "mp.messaging.outgoing.hello.topic";
    private static final String OUTGOING_HELLO_BOOTSTRAP_SERVER = "mp.messaging.outgoing.hello.bootstrap.servers";

    private static final String INCOMING_CAPS_CONNECTOR = "mp.messaging.incoming.caps.connector";
    private static final String INCOMING_CAPS_TOPIC = "mp.messaging.incoming.caps.topic";
    private static final String INCOMING_CAPS_BOOTSTRAP_SERVER = "mp.messaging.incoming.caps.bootstrap.servers";

    private static final String INCOMING_LOWER_IN_CONNECTOR = "mp.messaging.incoming.lower-in.connector";
    private static final String INCOMING_LOWER_IN_TOPIC = "mp.messaging.incoming.lower-in.topic";
    private static final String INCOMING_LOWER_IN_BOOTSTRAP_SERVER = "mp.messaging.incoming.lower-in.bootstrap.servers";

    private static final String OUTGOING_LOWER_OUT_CONNECTOR = "mp.messaging.outgoing.lower-out.connector";
    private static final String OUTGOING_LOWER_OUT_TOPIC = "mp.messaging.outgoing.lower-out.topic";
    private static final String OUTGOING_LOWER_OUT_BOOTSTRAP_SERVER = "mp.messaging.outgoing.lower-out.bootstrap.servers";

    @Before
    public void setUp(){
        System.setProperty(OUTGOING_HELLO_CONNECTOR, "liberty-kafka");
        System.setProperty(OUTGOING_HELLO_TOPIC, "hello-topic");
        System.setProperty(OUTGOING_HELLO_BOOTSTRAP_SERVER, "localhost:9092");

        System.setProperty(INCOMING_CAPS_CONNECTOR, "smallrye-kafka");
        System.setProperty(INCOMING_CAPS_TOPIC, "cap-topic");
        System.setProperty(INCOMING_CAPS_BOOTSTRAP_SERVER, "localhost:9092");

        System.setProperty(INCOMING_LOWER_IN_CONNECTOR, "smallrye-kafka");
        System.setProperty(INCOMING_LOWER_IN_TOPIC, "lower-in-topic");
        System.setProperty(INCOMING_LOWER_IN_BOOTSTRAP_SERVER, "localhost:9093");

        System.setProperty(OUTGOING_LOWER_OUT_CONNECTOR, "smallrye-kafka");
        System.setProperty(OUTGOING_LOWER_OUT_TOPIC, "lower-out-topic");
        System.setProperty(OUTGOING_LOWER_OUT_BOOTSTRAP_SERVER, "localhost:9093");
    }

    @Test
    public void testGreetingChannelScanning() throws IOException, JSONException {
        Index i = indexOf(GreetingChannel.class);

        AsyncApiAnnotationScanner scanner = new AsyncApiAnnotationScanner(emptyConfig(), i);

        AsyncAPI result = scanner.scan();

        printToConsole(result);
        assertJsonEquals("asyncapi.json", result);
    }

    @After
    public void tearDown(){
        System.clearProperty(OUTGOING_HELLO_CONNECTOR);
        System.clearProperty(OUTGOING_HELLO_TOPIC);
        System.clearProperty(OUTGOING_HELLO_BOOTSTRAP_SERVER);

        System.clearProperty(INCOMING_CAPS_CONNECTOR);
        System.clearProperty(INCOMING_CAPS_TOPIC);
        System.clearProperty(INCOMING_CAPS_BOOTSTRAP_SERVER);

        System.clearProperty(INCOMING_LOWER_IN_CONNECTOR);
        System.clearProperty(INCOMING_LOWER_IN_TOPIC);
        System.clearProperty(INCOMING_LOWER_IN_BOOTSTRAP_SERVER);

        System.clearProperty(OUTGOING_LOWER_OUT_CONNECTOR);
        System.clearProperty(OUTGOING_LOWER_OUT_TOPIC);
        System.clearProperty(OUTGOING_LOWER_OUT_BOOTSTRAP_SERVER);
    }
}
