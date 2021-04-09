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

@TckTest
public class SchemaTckTest extends BaseTckTest<SchemaTckTest.SchemaTest> {

  public static class SchemaTest extends AppTestBase {

    @Deployment(name = "schema")
    public static WebArchive createDeployment() {
      return ShrinkWrap.create(WebArchive.class, "streetlights.war")
          .addPackages(true, "io.smallrye.asyncapi.tck.schema")
          .addAsManifestResource("schema.properties", "microprofile-config.properties");
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testVersion(String type) {
      ValidatableResponse vr = callEndpoint(type);
      vr.body("asyncapi", equalTo("2.0.0"));
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testSchema(String type) {
      ValidatableResponse vr = callEndpoint(type);

      vr.body("components.schemas.EpochMillis.format", equalTo("int64"));
      vr.body("components.schemas.EpochMillis.description", equalTo("Milliseconds since January 1, 1970, 00:00:00 GMT"));
      vr.body("components.schemas.EpochMillis.type", equalTo("number"));

      vr.body("components.schemas.Hello.properties.date.$ref", equalTo("#/components/schemas/EpochMillis"));
      vr.body("components.schemas.Hello.properties.customers.description", equalTo("A list of customer"));
      vr.body("components.schemas.Hello.properties.customers.type", equalTo("array"));
      vr.body("components.schemas.Hello.properties.customers.items.$ref", equalTo("#/components/schemas/Customer"));

      vr.body("components.schemas.Customer.type", equalTo("object"));
      vr.body("components.schemas.Customer.properties.id.format", equalTo("int64"));
      vr.body("components.schemas.Customer.properties.id.type", equalTo("number"));
      vr.body("components.schemas.Customer.properties.id.example", equalTo("342-513-214"));
    }
  }
}
