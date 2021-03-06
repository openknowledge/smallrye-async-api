/**
 * Copyright 2019 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.smallrye.asyncapi.tck;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

import io.restassured.response.ValidatableResponse;

public class ModelReaderTest extends AppTestBase {
    @Deployment(name = "modelReader")
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "modelReader.war")
                .addPackages(true, "io.smallrye.asyncapi.apps.modelReader")
                .addAsManifestResource("modelReader.properties", "microprofile-config.properties");
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testVersion(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("asyncapi", startsWith("2.0."));
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testInfo(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("info.title", equalTo("Model Reader API"));
        vr.body("info.version", equalTo("3.0.7"));
        vr.body("info.description", equalTo("An API definition created by a model reader."));
    }
}
