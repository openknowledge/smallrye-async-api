package de.openknowledge.asyncapi.core.runtime.io.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.openknowledge.asyncapi.core.api.models.tag.TagImpl;
import de.openknowledge.asyncapi.core.runtime.io.IoLogging;
import de.openknowledge.asyncapi.core.runtime.io.JsonUtil;
import de.openknowledge.asyncapi.core.runtime.io.extension.ExtensionReader;
import de.openknowledge.asyncapi.core.runtime.io.externaldocs.ExternalDocsConstant;
import de.openknowledge.asyncapi.core.runtime.io.externaldocs.ExternalDocsReader;
import de.openknowledge.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;
import de.openknowledge.asyncapi.core.runtime.util.JandexUtil;
import de.openknowledge.asyncapi.core.runtime.util.TypeUtil;
import io.smallrye.asyncapi.spec.models.tag.Tag;

/**
 * Reading the Tag from annotation or json
 */
public class TagReader {

    private TagReader() {
    }

    /**
     * Reads any Tag annotations.The annotation value is an array of Tag annotations.
     *
     * @param context scanning context
     * @param annotationValue an array of {@literal @}Tag annotations
     * @return List of Tag models
     */
    public static Optional<List<Tag>> readTags(final AnnotationScannerContext context, final AnnotationValue annotationValue) {
        if (annotationValue != null) {
            IoLogging.logger.annotationsArray("@Tag");
            AnnotationInstance[] nestedArray = annotationValue.asNestedArray();
            List<Tag> tags = new ArrayList<>();
            for (AnnotationInstance tagAnno : nestedArray) {
                if (!JandexUtil.isRef(tagAnno)) {
                    tags.add(readTag(context, tagAnno));
                }
            }
            return Optional.of(tags);
        }
        return Optional.empty();
    }

    /**
     * Reads a list of {@link Tag} AsyncAPI nodes.
     *
     * @param node the json array node
     * @return List of Tag models
     */
    public static Optional<List<Tag>> readTags(final JsonNode node) {
        if (node != null && node.isArray()) {
            IoLogging.logger.jsonArray("Tag");
            ArrayNode nodes = (ArrayNode) node;
            List<Tag> rval = new ArrayList<>(nodes.size());
            for (JsonNode tagNode : nodes) {
                rval.add(readTag(tagNode));
            }
            return Optional.of(rval);
        }
        return Optional.empty();
    }

    /**
     * Reads a single Tag annotation.
     *
     * @param context scanning context
     * @param annotationInstance {@literal @}Tag annotation, must not be null
     * @return Tag model
     */
    public static Tag readTag(final AnnotationScannerContext context, final AnnotationInstance annotationInstance) {
        Objects.requireNonNull(annotationInstance, "Tag annotation must not be null");
        IoLogging.logger.singleAnnotation("@Tag");
        Tag tag = new TagImpl();
        tag.setName(JandexUtil.stringValue(annotationInstance, TagConstant.PROP_NAME));
        tag.setDescription(JandexUtil.stringValue(annotationInstance, TagConstant.PROP_DESCRIPTION));
        tag.setExternalDocumentation(ExternalDocsReader.readExternalDocs(context,
                annotationInstance.value(ExternalDocsConstant.PROP_EXTERNAL_DOCS)));
        tag.setExtensions(ExtensionReader.readExtensions(context, annotationInstance));
        return tag;
    }

    /**
     * Reads a {@link Tag} AsyncAPI node.
     *
     * @param node the json node
     * @return Tag model
     */
    private static Tag readTag(final JsonNode node) {
        IoLogging.logger.singleJsonNode("Tag");
        Tag tag = new TagImpl();
        tag.setName(JsonUtil.stringProperty(node, TagConstant.PROP_NAME));
        tag.setDescription(JsonUtil.stringProperty(node, TagConstant.PROP_DESCRIPTION));
        tag.setExternalDocumentation(ExternalDocsReader.readExternalDocs(node.get(ExternalDocsConstant.PROP_EXTERNAL_DOCS)));
        ExtensionReader.readExtensions(node, tag);
        return tag;
    }

    // Helpers for scanner classes
    public static boolean hasTagAnnotation(final AnnotationTarget target) {
        return TypeUtil.hasAnnotation(target, TagConstant.DOTNAME_TAG)
                || TypeUtil.hasAnnotation(target, TagConstant.DOTNAME_TAGS);
    }

    public static List<AnnotationInstance> getTagAnnotations(final AnnotationTarget target) {
        return JandexUtil.getRepeatableAnnotation(target, TagConstant.DOTNAME_TAG, TagConstant.DOTNAME_TAGS);
    }

}
