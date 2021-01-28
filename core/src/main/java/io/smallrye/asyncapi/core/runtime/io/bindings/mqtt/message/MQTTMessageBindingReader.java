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
package io.smallrye.asyncapi.core.runtime.io.bindings.mqtt.message;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import com.fasterxml.jackson.databind.JsonNode;

import io.smallrye.asyncapi.core.api.models.binding.mqtt.MQTTMessageBindingImpl;
import io.smallrye.asyncapi.core.runtime.io.IoLogging;
import io.smallrye.asyncapi.core.runtime.io.JsonUtil;
import io.smallrye.asyncapi.core.runtime.util.JandexUtil;
import io.smallrye.asyncapi.spec.models.binding.mqtt.MQTTMessageBinding;

public class MQTTMessageBindingReader {

    public MQTTMessageBindingReader() {
    }

    public static MQTTMessageBinding readMessageBinding(final AnnotationValue annotationValue) {
        if (annotationValue == null) {
            return null;
        }

        IoLogging.logger.singleAnnotation("@MQTTMessageBinding");

        AnnotationInstance annotationInstance = annotationValue.asNested();

        MQTTMessageBinding binding = new MQTTMessageBindingImpl();
        binding.setBindingVersion(JandexUtil.stringValue(annotationInstance, MQTTMessageBindingConstant.PROP_BINDING_VERSION));

        return binding;
    }

    public static MQTTMessageBinding readMessageBinding(final JsonNode node) {
        if (node == null) {
            return null;
        }

        IoLogging.logger.singleJsonNode("MQTTMessageBinding");

        MQTTMessageBinding binding = new MQTTMessageBindingImpl();
        binding.setBindingVersion(JsonUtil.stringProperty(node, MQTTMessageBindingConstant.PROP_BINDING_VERSION));

        return binding;
    }
}
