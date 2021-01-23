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
package io.smallrye.asyncapi.core.runtime.io.servervariable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import com.fasterxml.jackson.databind.JsonNode;

import io.smallrye.asyncapi.core.api.models.server.ServerVariableImpl;
import io.smallrye.asyncapi.core.runtime.io.IoLogging;
import io.smallrye.asyncapi.core.runtime.io.JsonUtil;
import io.smallrye.asyncapi.core.runtime.io.extension.ExtensionConstant;
import io.smallrye.asyncapi.core.runtime.io.extension.ExtensionReader;
import io.smallrye.asyncapi.core.runtime.util.JandexUtil;
import io.smallrye.asyncapi.spec.models.server.ServerVariable;

/**
 * Reading the ServerVariable annotation and json node
 *
 * @see "https://www.asyncapi.com/docs/specifications/2.0.0#serverVariableObject"
 */
public class ServerVariableReader {

    private ServerVariableReader() {
    }

    /**
     * Reads an array of ServerVariable annotations, returning a new {@link ServerVariable} model. The
     * annotation value is an array of ServerVariable annotations.
     *
     * @param annotationValue an arrays of {@literal @}ServerVariable annotations
     * @return a Map of Variable name and ServerVariable model
     */
    public static Map<String, ServerVariable> readServerVariables(final AnnotationValue annotationValue) {
        if (annotationValue == null) {
            return null;
        }
        IoLogging.logger.annotationsArray("@ServerVariable");
        AnnotationInstance[] nestedArray = annotationValue.asNestedArray();
        Map<String, ServerVariable> variables = new LinkedHashMap<>();
        for (AnnotationInstance serverVariableAnno : nestedArray) {
            String name = JandexUtil.stringValue(serverVariableAnno, ServerVariableConstant.PROP_NAME);
            if (name != null) {
                variables.put(name, readServerVariable(serverVariableAnno));
            }
        }
        return variables;
    }

    /**
     * Reads the {@link ServerVariable} OpenAPI node.
     *
     * @param node the json node
     * @return a Map of Variable name and ServerVariable model
     */
    public static Map<String, ServerVariable> readServerVariables(final JsonNode node) {
        if (node == null) {
            return null;
        }
        IoLogging.logger.jsonNodeMap("ServerVariable");
        Map<String, ServerVariable> variables = new LinkedHashMap<>();
        for (Iterator<String> iterator = node.fieldNames(); iterator.hasNext();) {
            String fieldName = iterator.next();
            if (!ExtensionConstant.isExtensionField(fieldName)) {
                JsonNode varNode = node.get(fieldName);
                variables.put(fieldName, readServerVariable(varNode));
            }
        }

        return variables;
    }

    /**
     * Reads a single ServerVariable annotation.
     *
     * @param annotationInstance the {@literal @}ServerVariable annotation
     * @return the ServerVariable model
     */
    private static ServerVariable readServerVariable(final AnnotationInstance annotationInstance) {
        if (annotationInstance == null) {
            return null;
        }
        IoLogging.logger.singleAnnotation("@ServerVariable");
        ServerVariable variable = new ServerVariableImpl();
        variable.setDescription(JandexUtil.stringValue(annotationInstance, ServerVariableConstant.PROP_DESCRIPTION));
        variable.setEnumeration(
                JandexUtil.stringListValue(annotationInstance, ServerVariableConstant.PROP_ENUMERATION).orElse(null));
        variable.setDefaultValue(JandexUtil.stringValue(annotationInstance, ServerVariableConstant.PROP_DEFAULT_VALUE));
        return variable;
    }

    /**
     * Reads a list of {@link ServerVariable} OpenAPI nodes.
     *
     * @param node the json node
     * @return the ServerVariable model
     */
    private static ServerVariable readServerVariable(JsonNode node) {
        if (node == null) {
            return null;
        }
        IoLogging.logger.singleJsonNode("ServerVariable");
        ServerVariable variable = new ServerVariableImpl();

        JsonNode enumNode = node.get(ServerVariableConstant.PROP_ENUM);
        if (enumNode != null && enumNode.isArray()) {
            List<String> enums = new ArrayList<>(enumNode.size());
            for (JsonNode n : enumNode) {
                enums.add(n.asText());
            }
            variable.setEnumeration(enums);
        }

        JsonNode exampleNode = node.get(ServerVariableConstant.PROP_EXAMPLES);
        if (exampleNode != null && exampleNode.isArray()) {
            List<String> examples = new ArrayList<>(exampleNode.size());
            for (JsonNode n : exampleNode) {
                examples.add(n.asText());
            }
            variable.setExamples(examples);
        }

        variable.setDefaultValue(JsonUtil.stringProperty(node, ServerVariableConstant.PROP_DEFAULT));
        variable.setDescription(JsonUtil.stringProperty(node, ServerVariableConstant.PROP_DESCRIPTION));
        ExtensionReader.readExtensions(node, variable);
        return variable;
    }

}
