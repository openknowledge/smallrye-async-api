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
import static org.hamcrest.Matchers.startsWith;

public class PetStoreTest extends AppTestBase {

  @Deployment(name = "order")
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "petstore.war")
        .addPackages(true, "io.smallrye.asyncapi.apps.petstore");
  }

  @RunAsClient
  @Test(dataProvider = "formatProvider")
  public void testVersion(String type) {
    ValidatableResponse vr = callEndpoint(type);
    System.out.println(getResponse().body()
        .prettyPrint());
    vr.body("asyncapi", startsWith("2.0."));
  }

  @RunAsClient
  @Test(dataProvider = "formatProvider")
  public void testAnyOf(String type) {
    ValidatableResponse vr = callEndpoint(type);

    vr.body("channels.order-send-topic.publish.message.payload.anyOf[0].$ref", equalTo("#/components/schemas/Order"));
    vr.body("channels.order-send-topic.publish.message.payload.anyOf[1].$ref", equalTo("#/components/schemas/BadOrder"));
  }

  @RunAsClient
  @Test(dataProvider = "formatProvider")
  public void testNot(String type) {
    ValidatableResponse vr = callEndpoint(type);

    vr.body("channels.order-receive-topic.subscribe.message.payload.not.$ref", equalTo("#/components/schemas/BadOrder"));
  }

  @RunAsClient
  @Test(dataProvider = "formatProvider")
  public void testOneOf(String type) {
    ValidatableResponse vr = callEndpoint(type);

    vr.body("channels.pet-send-topic.publish.message.payload.oneOf[0].$ref", equalTo("#/components/schemas/Cat"));
    vr.body("channels.pet-send-topic.publish.message.payload.oneOf[1].$ref", equalTo("#/components/schemas/Dog"));
    vr.body("channels.pet-send-topic.publish.message.payload.oneOf[2].$ref", equalTo("#/components/schemas/Lizard"));
  }

  @RunAsClient
  @Test(dataProvider = "formatProvider")
  public void testAllOf(String type) {
    ValidatableResponse vr = callEndpoint(type);

    System.out.println(getResponse().body().prettyPrint());

    vr.body("channels.pet-receive-topic.subscribe.message.payload.allOf[0].$ref", equalTo("#/components/schemas/Cat"));
    vr.body("channels.pet-receive-topic.subscribe.message.payload.allOf[1].$ref", equalTo("#/components/schemas/Dog"));
    vr.body("channels.pet-receive-topic.subscribe.message.payload.allOf[2].$ref", equalTo("#/components/schemas/Lizard"));
  }

  @RunAsClient
  @Test(dataProvider = "formatProvider")
  public void testCatDeprecatedAttribute(String type) {
    ValidatableResponse vr = callEndpoint(type);

    vr.body("components.schemas.Cat.properties.title.deprecated", equalTo(true));
  }

  @RunAsClient
  @Test(dataProvider = "formatProvider")
  public void testDiscriminator(String type) {
    ValidatableResponse vr = callEndpoint(type);

    vr.body("channels.pet-receive2-topic.subscribe.message.payload.oneOf[0].$ref", equalTo("#/components/schemas/Cat"));
    vr.body("channels.pet-receive2-topic.subscribe.message.payload.oneOf[1].$ref", equalTo("#/components/schemas/Dog"));
    vr.body("channels.pet-receive2-topic.subscribe.message.payload.oneOf[2].$ref", equalTo("#/components/schemas/Lizard"));
    vr.body("channels.pet-receive2-topic.subscribe.message.payload.discriminator", equalTo("pet_type"));
  }
}
