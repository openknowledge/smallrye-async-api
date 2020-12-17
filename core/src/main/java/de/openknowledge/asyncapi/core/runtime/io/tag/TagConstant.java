package de.openknowledge.asyncapi.core.runtime.io.tag;

import org.jboss.jandex.DotName;

import io.smallrye.asyncapi.spec.annotations.tag.Tag;
import io.smallrye.asyncapi.spec.annotations.tag.Tags;

/**
 * Constants related to Server
 */
public class TagConstant {
    static final DotName DOTNAME_TAG = DotName.createSimple(Tag.class.getName());

    public static final DotName DOTNAME_TAGS = DotName.createSimple(Tags.class.getName());

    public static final String PROP_NAME = "name";

    public static final String PROP_DESCRIPTION = "description";

    private TagConstant() {
    }
}
