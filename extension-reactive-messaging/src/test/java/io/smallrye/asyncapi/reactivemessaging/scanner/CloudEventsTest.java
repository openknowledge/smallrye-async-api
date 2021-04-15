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

import io.smallrye.asyncapi.core.runtime.scanner.AsyncApiAnnotationScanner;
import io.smallrye.asyncapi.reactivemessaging.scanner.cloudevents.CloudEventsService;
import io.smallrye.asyncapi.spec.models.AsyncAPI;
import org.jboss.jandex.Index;
import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;

public class CloudEventsTest extends ReactiveMessagingDataObjectScannerTestBase {

  @Test
  public void testCloudEventsScanning() throws IOException, JSONException {
    Index i = indexOf(CloudEventsService.class, CloudEventsService.Greeting.class, CloudEventsService.RandomNumber.class);

    System.setProperty("mp.messaging.outgoing.config-out.cloud-events-source", "app");
    System.setProperty("mp.messaging.outgoing.config-out.cloud-events-type", "dev.mweis.helloworld.v1");
    System.setProperty("mp.messaging.outgoing.config-out.cloud-events-subject", "hello-world");
    System.setProperty("mp.messaging.outgoing.config-out.cloud-events-data-content-type", "application/json");
    System.setProperty("mp.messaging.outgoing.config-out-override.cloud-events-source", "app");
    System.setProperty("mp.messaging.outgoing.generated-price.topic", "prices");

    AsyncApiAnnotationScanner scanner = new AsyncApiAnnotationScanner(emptyConfig(), i);

    AsyncAPI result = scanner.scan();

    printToConsole(result);
    assertJsonEquals("cloudevents.json", result);
  }
}
