package io.smallrye.asyncapi.core.runtime.io.tag;

import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.asyncapi.core.runtime.io.JsonUtil;
import io.smallrye.asyncapi.core.runtime.io.definition.DefinitionConstant;
import io.smallrye.asyncapi.core.runtime.io.extension.ExtensionWriter;
import io.smallrye.asyncapi.core.runtime.io.externaldocs.ExternalDocsWriter;
import io.smallrye.asyncapi.spec.models.tag.Tag;

/**
 * Writing the Tag to json
 */
public class TagWriter {

    private TagWriter() {
    }

    /**
     * Writes the {@link Tag} model array to the JSON tree.
     *
     * @param node the json node
     * @param tags list of Tag models
     */
    public static void writeTags(ObjectNode node, List<Tag> tags) {
        if (tags == null) {
            return;
        }
        ArrayNode array = node.putArray(DefinitionConstant.PROP_TAGS);
        for (Tag tag : tags) {
            ObjectNode tagNode = array.addObject();
            JsonUtil.stringProperty(tagNode, TagConstant.PROP_NAME, tag.getName());
            JsonUtil.stringProperty(tagNode, TagConstant.PROP_DESCRIPTION, tag.getDescription());
            ExternalDocsWriter.writeExternalDocumentation(tagNode, tag.getExternalDocumentation());
            ExtensionWriter.writeExtensions(tagNode, tag);
        }
    }
}
