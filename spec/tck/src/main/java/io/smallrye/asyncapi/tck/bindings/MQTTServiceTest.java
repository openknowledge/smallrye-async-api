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

package io.smallrye.asyncapi.tck.bindings;

import static org.hamcrest.Matchers.equalTo;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

import io.restassured.response.ValidatableResponse;
import io.smallrye.asyncapi.tck.AppTestBase;

public class MQTTServiceTest extends AppTestBase {

    @Deployment(name = "mqtt")
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "mqtt.war")
                .addPackages(true, "io.smallrye.asyncapi.bindings.mqtt");
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testVersion(String type) {
        ValidatableResponse vr = callEndpoint(type);

        vr.body("asyncapi", equalTo("2.0.0"));
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testMessageBinding(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("channels.mqtt-test1.subscribe.message.bindings.mqtt.bindingVersion", equalTo("0.1.0"));
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testOperationBinding(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String operationPath = "channels.mqtt-test2.subscribe.bindings.mqtt.";

        vr.body(operationPath + "qos", equalTo(2));
        vr.body(operationPath + "retain", equalTo(true));
        vr.body(operationPath + "bindingVersion", equalTo("0.1.0"));
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testServerBinding(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String serverPath = "servers.production.bindings.mqtt.";

        vr.body(serverPath + "clientId", equalTo("guest"));
        vr.body(serverPath + "cleanSession", equalTo(true));
        vr.body(serverPath + "lastWill.topic", equalTo("/last-will"));
        vr.body(serverPath + "lastWill.qos", equalTo(2));
        vr.body(serverPath + "lastWill.message", equalTo("Guest gone offline."));
        vr.body(serverPath + "lastWill.retain", equalTo(false));

    }
}
