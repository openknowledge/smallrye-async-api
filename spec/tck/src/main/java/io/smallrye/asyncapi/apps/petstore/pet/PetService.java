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
package io.smallrye.asyncapi.apps.petstore.pet;

import io.smallrye.asyncapi.spec.annotations.channel.ChannelItem;
import io.smallrye.asyncapi.spec.annotations.message.Message;
import io.smallrye.asyncapi.spec.annotations.operation.Operation;
import io.smallrye.asyncapi.spec.annotations.schema.Schema;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;

public class PetService {

  @ChannelItem(
      channel = "pet-send-topic",
      publish = @Operation(
          message = @Message(
              name = "SpecificPetMessage",
              payload = @Schema(
                  type = SchemaType.OBJECT,
                  implementation = Pet.class,
                  oneOf = { Cat.class, Dog.class, Lizard.class }
              )
          )
      )
  )
  public Pet send(){
    return new Pet();
  }

  @ChannelItem(
      channel = "pet-receive-topic",
      subscribe = @Operation(
          message = @Message(
              name = "PetMessage",
              payload = @Schema(
                  type = SchemaType.OBJECT,
                  implementation = Pet.class,
                  allOf = { Cat.class, Dog.class, Lizard.class }
              )
          )
      )
  )
  public void receive(Pet pet){
  }

  @ChannelItem(
      channel = "pet-receive2-topic",
      subscribe = @Operation(
          message = @Message(
              name = "DiscriminatorMessage",
              payload = @Schema(
                      oneOf = { Cat.class, Dog.class, Lizard.class },
                      discriminator = "pet_type"
                  )
          )
      )
  )
  public void receive2(Pet pet){
  }
}
