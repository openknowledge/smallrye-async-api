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
import test.io.smallrye.asyncapi.tck.BaseTckTest;
import test.io.smallrye.asyncapi.tck.TckTest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

@TckTest
public class InfoTckTest extends BaseTckTest<InfoTckTest.InfoTest> {

  public static class InfoTest extends AppTestBase {

    @Deployment(name = "info")
    public static WebArchive createDeployment() {
      return ShrinkWrap.create(WebArchive.class, "info.war")
          .addPackages(true, "io.smallrye.asyncapi.tck.info")
          .addAsManifestResource("info.properties", "microprofile-config.properties");
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testVersion(String type) {
      ValidatableResponse vr = callEndpoint(type);
      vr.body("asyncapi", startsWith("2.0."));

      vr.body("info.title", equalTo("Demo Service"));
      vr.body("info.version", equalTo("1.0.0-SNAPSHOT"));
      vr.body("info.description", equalTo("A Demo Service"));
      vr.body("info.termsOfService", equalTo("http://example.com/terms/"));

      vr.body("info.contact.name", equalTo("API Support"));
      vr.body("info.contact.url", equalTo("http://www.example.com/support"));
      vr.body("info.contact.email", equalTo("support@example.com"));

      vr.body("info.license.name", equalTo("Apache 2.0"));
      vr.body("info.license.url", equalTo("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }
  }
}
