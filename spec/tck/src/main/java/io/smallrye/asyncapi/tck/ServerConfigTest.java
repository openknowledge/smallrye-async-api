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

package io.smallrye.asyncapi.tck;

import io.restassured.response.ValidatableResponse;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

public class ServerConfigTest extends AppTestBase {

  @Deployment(name = "streetlights")
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "streetlights.war")
        .addPackages(true, "io.smallrye.asyncapi.apps.streetlights")
        .addAsManifestResource("servers.properties", "microprofile-config.properties");
  }

  @RunAsClient
  @Test(dataProvider = "formatProvider")
  public void testServers(String type) {
    ValidatableResponse vr = callEndpoint(type);

    vr.body("servers.dev.url", equalTo("dev.example.com"));
    vr.body("servers.dev.protocol", equalTo("kafka"));
  }
}
