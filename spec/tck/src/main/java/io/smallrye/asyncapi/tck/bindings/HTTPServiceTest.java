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

public class HTTPServiceTest extends AppTestBase {

  @Deployment(name = "http")
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "http.war")
        .addPackages(true, "io.smallrye.asyncapi.bindings.http");
  }

  @RunAsClient
  @Test(dataProvider = "formatProvider")
  public void testVersion(String type) {
    ValidatableResponse vr = callEndpoint(type);
    vr.body("asyncapi", equalTo("2.0.0"));
  }

  @RunAsClient
  @Test(dataProvider = "formatProvider")
  public void testOperationBinding(String type) {
    ValidatableResponse vr = callEndpoint(type);

    String httpBinding = "channels.http-test1.subscribe.bindings.httpBinding.";
    vr.body(httpBinding + "type", equalTo("request"));
    vr.body(httpBinding + "method", equalTo("GET"));
    vr.body(httpBinding + "query.type", equalTo("object"));
    vr.body(httpBinding + "query.properties.my-app-header.description", equalTo("The Id of the company"));
    vr.body(httpBinding + "query.properties.my-app-header.minimum", equalTo(1));
    vr.body(httpBinding + "bindingVersion", equalTo("0.1.0"));
  }

  @RunAsClient
  @Test(dataProvider = "formatProvider")
  public void testMessageBinding(String type) {
    ValidatableResponse vr = callEndpoint(type);

    String httpBinding = "channels.http-test2.publish.message.bindings.httpBinding.";
    vr.body(httpBinding + "headers.type", equalTo("object"));
    vr.body(httpBinding + "bindingVersion", equalTo("0.1.0"));
  }
}
