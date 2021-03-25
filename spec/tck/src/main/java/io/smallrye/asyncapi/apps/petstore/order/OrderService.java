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
package io.smallrye.asyncapi.apps.petstore.order;

import io.smallrye.asyncapi.spec.annotations.channel.ChannelItem;
import io.smallrye.asyncapi.spec.annotations.message.Message;
import io.smallrye.asyncapi.spec.annotations.operation.Operation;
import io.smallrye.asyncapi.spec.annotations.schema.Schema;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;

public class OrderService {

  @ChannelItem(
      channel = "order-send-topic",
      publish = @Operation(
          message = @Message(
              name = "BadOrderMessage",
              payload = @Schema(
                  type = SchemaType.OBJECT,
                  implementation = Order.class,
                  anyOf = { Order.class, BadOrder.class}
              )
          )
      )
  )
  public Order send(){
    return new Order();
  }

  @ChannelItem(
      channel = "order-receive-topic",
      subscribe = @Operation(
          message = @Message(
              name = "OrderMessage",
              payload = @Schema(
                  type = SchemaType.OBJECT,
                  implementation = Order.class,
                  not =  BadOrder.class
              )
          )
      )
  )
  public void receive(Order order){
  }
}
