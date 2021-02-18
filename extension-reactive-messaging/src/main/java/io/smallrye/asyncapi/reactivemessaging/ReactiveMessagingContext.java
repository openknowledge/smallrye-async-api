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
package io.smallrye.asyncapi.reactivemessaging;

import java.util.ArrayList;
import java.util.List;

public class ReactiveMessagingContext {

  private final List<ReactiveMessagingProperty> incomingChannels;

  private final List<ReactiveMessagingProperty> outgoingChannels;

  private List<ReactiveMessagingProperty> allChannels;

  public ReactiveMessagingContext(final List<ReactiveMessagingProperty> in, final List<ReactiveMessagingProperty> out) {
    this.incomingChannels = in;
    this.outgoingChannels = out;

    this.allChannels = new ArrayList<>();
    this.allChannels.addAll(in);
    this.allChannels.addAll(out);
  }

  public List<ReactiveMessagingProperty> getIncomingChannels() {
    return incomingChannels;
  }

  public List<ReactiveMessagingProperty> getOutgoingChannels() {
    return outgoingChannels;
  }

  public List<ReactiveMessagingProperty> getAllChannels() {
    return allChannels;
  }
}
