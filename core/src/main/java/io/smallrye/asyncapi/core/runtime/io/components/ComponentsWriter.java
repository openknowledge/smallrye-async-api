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
package io.smallrye.asyncapi.core.runtime.io.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.asyncapi.core.runtime.io.definition.DefinitionConstant;
import io.smallrye.asyncapi.core.runtime.io.extension.ExtensionWriter;
import io.smallrye.asyncapi.core.runtime.io.message.MessageTraitWriter;
import io.smallrye.asyncapi.core.runtime.io.message.MessageWriter;
import io.smallrye.asyncapi.core.runtime.io.operation.OperationTraitWriter;
import io.smallrye.asyncapi.core.runtime.io.parameter.ParameterWriter;
import io.smallrye.asyncapi.core.runtime.io.schema.SchemaWriter;
import io.smallrye.asyncapi.core.runtime.io.securityscheme.SecuritySchemesWriter;
import io.smallrye.asyncapi.spec.models.Components;
import io.smallrye.asyncapi.spec.models.message.MessageTrait;
import io.smallrye.asyncapi.spec.models.operation.OperationTrait;

public class ComponentsWriter {
    public ComponentsWriter() {
    }

    /**
     * Writes the given model.
     *
     * @param parent the json node
     * @param model the AsyncAPI model
     */
    public static void writeComponents(ObjectNode parent, Components model) {
        if (model == null) {
            return;
        }
        if (parent == null) {
            return;
        }

        ObjectNode node = parent.putObject(DefinitionConstant.PROP_COMPONENTS);

        MessageWriter.writeMessages(node, model.getMessages());
        SecuritySchemesWriter.writeSecuritySchemes(node, model.getSecuritySchemes());
        ParameterWriter.writeParameters(node, model.getParameters());
        SchemaWriter.writeSchemas(node, model.getSchemas());
        OperationTraitWriter.writeOperationTraits(node, getOperationTraits(model));
        MessageTraitWriter.writeComponentsMessageTraits(node, getMessageTraits(model));
        ExtensionWriter.writeExtensions(node, model);
    }

    private static List<OperationTrait> getOperationTraits(Components model) {
        if (model.getOperationTraits() == null) {
            return new ArrayList<>();
        }

        List<OperationTrait> list = new ArrayList<>();
        for (Map.Entry<String, OperationTrait> entry : model.getOperationTraits().entrySet()) {
            list.add(entry.getValue());
        }

        return list;
    }

    private static List<MessageTrait> getMessageTraits(Components model) {
        if (model.getMessageTraits() == null) {
            return new ArrayList<>();
        }

        List<MessageTrait> list = new ArrayList<>();
        for (Map.Entry<String, MessageTrait> entry : model.getMessageTraits().entrySet()) {
            list.add(entry.getValue());
        }

        return list;
    }
}
