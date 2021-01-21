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
package io.smallrye.asyncapi.core.api.models.binding.http;

import io.smallrye.asyncapi.core.api.models.ExtensibleImpl;
import io.smallrye.asyncapi.core.api.models.ModelImpl;
import io.smallrye.asyncapi.spec.models.binding.MessageBinding;
import io.smallrye.asyncapi.spec.models.binding.http.HTTPMessageBinding;
import io.smallrye.asyncapi.spec.models.schema.Schema;

/**
 * An implementation of the {@link HTTPMessageBinding} AsyncAPI model interface.
 */
public class HTTPMessageBindingImpl extends ExtensibleImpl<MessageBinding> implements HTTPMessageBinding, ModelImpl {

    private Schema headers;

    private String bindingVersion;

    /**
     * @see HTTPMessageBinding#getHeaders()
     */
    @Override
    public Schema getHeaders() {
        return headers;
    }

    /**
     * @see HTTPMessageBinding#setHeaders(Schema headers)
     */
    @Override
    public void setHeaders(final Schema headers) {
        this.headers = headers;
    }

    /**
     * @see HTTPMessageBinding#getBindingVersion()
     */
    @Override
    public String getBindingVersion() {
        return bindingVersion;
    }

    /**
     * @see HTTPMessageBinding#setBindingVersion(String bindingVersion)
     */
    @Override
    public void setBindingVersion(final String bindingVersion) {
        this.bindingVersion = bindingVersion;
    }
}
