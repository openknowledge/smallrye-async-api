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
 *
 *
 */
package de.openknowledge.asyncapi.core.api.models.binding.nats;

import de.openknowledge.asyncapi.core.api.models.ExtensibleImpl;
import de.openknowledge.asyncapi.core.api.models.ModelImpl;
import io.smallrye.asyncapi.spec.models.binding.ChannelBinding;
import io.smallrye.asyncapi.spec.models.binding.nats.NATSChannelBinding;

/**
 * An implementation of the {@link NATSChannelBinding} AsyncAPI model interface.
 */
public class NATSChannelBindingImpl extends ExtensibleImpl<ChannelBinding> implements NATSChannelBinding, ModelImpl {
}
