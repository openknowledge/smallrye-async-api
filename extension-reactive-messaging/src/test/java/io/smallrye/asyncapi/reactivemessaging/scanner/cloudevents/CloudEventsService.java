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
package io.smallrye.asyncapi.reactivemessaging.scanner.cloudevents;

import io.cloudevents.CloudEvent;
import io.cloudevents.v03.AttributesImpl;
import io.cloudevents.v03.CloudEventBuilder;
import io.cloudevents.v03.CloudEventImpl;
import io.smallrye.asyncapi.spec.annotations.AsyncAPI;
import io.smallrye.asyncapi.spec.annotations.channel.ChannelItem;
import io.smallrye.asyncapi.spec.annotations.info.Info;
import io.smallrye.asyncapi.spec.annotations.message.Message;
import io.smallrye.asyncapi.spec.annotations.operation.Operation;
import io.smallrye.asyncapi.spec.annotations.schema.Schema;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

@AsyncAPI(
    asyncapi = "2.0.0",
    info = @Info(
        title = "CloudEventsService",
        version = "1.0.0"
    ),
    defaultContentType = "application/json"
)
public class CloudEventsService {

  @ChannelItem(
      channel = "prices",
      publish = @Operation(
          operationId = "generated-price",
          message = @Message(
              contentType = "text/plain",
              summary = "A random price"
          )
      )
  )
  @Outgoing("generated-price")
  public CloudEventImpl<RandomNumber> generate() {
    return CloudEventBuilder.<RandomNumber>builder()
        .withType("org.acme.generate.v1")
        .withId(UUID.randomUUID().toString())
        .withTime(ZonedDateTime.now())
        .withSource(URI.create("acme.org"))
        .withData(new RandomNumber())
        .build();
  }

  @Incoming("cloudevents-string-in")
  public void receiveStringCloudEvent(CloudEvent<AttributesImpl, String> event) {
  }

  @Incoming("cloudevents-double-in")
  public void receiveDoubleCloudEvent(CloudEvent<AttributesImpl, Double> event) {
  }

  @Incoming("cloudevents-float-in")
  public void receiveFloatCloudEvent(CloudEvent<AttributesImpl, Float> event) {
  }

  @Incoming("cloudevents-integer-in")
  public void receiveIntegerCloudEvent(CloudEvent<AttributesImpl, Integer> event) {
  }

  @ChannelItem(
      channel = "cloudevents-short-in",
      publish = @Operation(
          operationId = "cloudevents-short-in",
          message = @Message(
              contentType = "text/plain",
              summary = "10000"
          )
      )
  )
  @Incoming("cloudevents-short-in")
  public void receiveShortCloudEvent(CloudEvent<AttributesImpl, Short> event) {
  }

  @Outgoing("cloudevents-out")
  public CloudEventImpl<Greeting> sendGreetingCloudEvent(){
    return CloudEventBuilder.<Greeting>builder()
        .withData(new Greeting("Hello World"))
        .build();
  }

  @ChannelItem(
      channel = "greeting-out",
      publish = @Operation(
          operationId = "generated-out",
          message = @Message(
              contentType = "application/json",
              summary = "a Hello World greeting"
          )
      )
  )
  @Outgoing("greeting-out")
  public Greeting sendGreeting(){
    return new Greeting("Hello World");
  }

  @Outgoing("config-out")
  public Greeting sendConfigGreeting(){
    return new Greeting("Hello World");
  }

  @Schema
  public class Greeting{

    @Schema(required = true, example = "Hello World")
    private String greet;

    public Greeting(final String greet) {
      this.greet = greet;
    }

    public String getGreet() {
      return greet;
    }

    public void setGreet(final String greet) {
      this.greet = greet;
    }
  }

  @Schema
  public class RandomNumber{

    @Schema(required = true, example = "2")
    private int number;

    public RandomNumber() {
      this.number = new Random().nextInt();
    }

    public int getNumber() {
      return number;
    }

    public void setNumber(final int number) {
      this.number = number;
    }
  }
}
