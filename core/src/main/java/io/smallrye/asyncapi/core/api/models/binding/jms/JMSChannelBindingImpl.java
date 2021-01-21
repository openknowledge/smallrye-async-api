/*
 * Copyright 2019 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.smallrye.asyncapi.core.api.models.binding.jms;

import io.smallrye.asyncapi.core.api.models.ExtensibleImpl;
import io.smallrye.asyncapi.core.api.models.ModelImpl;
import io.smallrye.asyncapi.spec.models.binding.ChannelBinding;
import io.smallrye.asyncapi.spec.models.binding.jms.JMSChannelBinding;

/**
 * An implementation of the {@link JMSChannelBinding} AsyncAPI model interface.
 */
public class JMSChannelBindingImpl extends ExtensibleImpl<ChannelBinding> implements JMSChannelBinding, ModelImpl {
}
