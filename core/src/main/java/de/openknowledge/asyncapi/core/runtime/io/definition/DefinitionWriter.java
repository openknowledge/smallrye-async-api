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
package de.openknowledge.asyncapi.core.runtime.io.definition;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.openknowledge.asyncapi.core.runtime.io.JsonUtil;
import de.openknowledge.asyncapi.core.runtime.io.channels.ChannelsWriter;
import de.openknowledge.asyncapi.core.runtime.io.components.ComponentsWriter;
import de.openknowledge.asyncapi.core.runtime.io.extension.ExtensionWriter;
import de.openknowledge.asyncapi.core.runtime.io.externaldocs.ExternalDocsWriter;
import de.openknowledge.asyncapi.core.runtime.io.info.InfoWriter;
import de.openknowledge.asyncapi.core.runtime.io.server.ServerWriter;
import de.openknowledge.asyncapi.core.runtime.io.tag.TagWriter;
import io.smallrye.asyncapi.spec.models.AsyncAPI;

/**
 * Writing the AsyncAPI to json
 *
 * @see <a href= "https://www.asyncapi.com/docs/specifications/2.0.0/#A2SObject">AsyncAPI Specification AsyncAPI Object</a>
 */
public class DefinitionWriter {

    private DefinitionWriter() {
    }

    /**
     * Writes the given model.
     *
     * @param node the json node
     * @param model the AsyncAPI model
     */
    public static void writeAsyncAPI(ObjectNode node, AsyncAPI model) {
        JsonUtil.stringProperty(node, DefinitionConstant.PROP_ASYNCAPI, model.getAsyncapi());
        JsonUtil.stringProperty(node, DefinitionConstant.PROP_CONTENT_TYPE, model.getDefaultContentType());
        InfoWriter.writeInfo(node, model.getInfo());
        ServerWriter.writeServers(node, model.getServers());
        ChannelsWriter.writeChannel(node, model.getChannels());
        ExternalDocsWriter.writeExternalDocumentation(node, model.getExternalDocs());
        JsonUtil.stringProperty(node, DefinitionConstant.PROP_IDENTIFIER, model.getIdentifier());
        ComponentsWriter.writeComponents(node, model.getComponents());
        TagWriter.writeTags(node, model.getTags());
        ExtensionWriter.writeExtensions(node, model);
    }
}
