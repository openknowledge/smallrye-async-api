package de.openknowledge.asyncapi.core.runtime.io.contact;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import com.fasterxml.jackson.databind.JsonNode;

import de.openknowledge.asyncapi.core.api.models.info.ContactImpl;
import de.openknowledge.asyncapi.core.runtime.io.IoLogging;
import de.openknowledge.asyncapi.core.runtime.io.JsonUtil;
import de.openknowledge.asyncapi.core.runtime.io.extension.ExtensionReader;
import de.openknowledge.asyncapi.core.runtime.util.JandexUtil;
import io.smallrye.asyncapi.spec.models.info.Contact;

/**
 * This reads the Contact from annotations or json
 *
 * @see "https://www.asyncapi.com/docs/specifications/2.0.0/#contactObject"
 */
public class ContactReader {

    private ContactReader() {
    }

    /**
     * Reads an Contact annotation.
     *
     * @param annotationValue the {@literal @}Contact annotation
     * @return Contact model
     */
    public static Contact readContact(final AnnotationValue annotationValue) {
        if (annotationValue == null) {
            return null;
        }
        IoLogging.logger.singleAnnotation("@Contact");
        AnnotationInstance nested = annotationValue.asNested();
        Contact contact = new ContactImpl();
        contact.setName(JandexUtil.stringValue(nested, ContactConstant.PROP_NAME));
        contact.setUrl(JandexUtil.stringValue(nested, ContactConstant.PROP_URL));
        contact.setEmail(JandexUtil.stringValue(nested, ContactConstant.PROP_EMAIL));
        return contact;
    }

    /**
     * Reads an {@link Contact} AsyncAPI node.
     *
     * @param node the json node
     * @return Contact model
     */
    public static Contact readContact(final JsonNode node) {
        if (node == null) {
            return null;
        }
        IoLogging.logger.singleJsonNode("Contact");
        Contact contact = new ContactImpl();
        contact.setName(JsonUtil.stringProperty(node, ContactConstant.PROP_NAME));
        contact.setUrl(JsonUtil.stringProperty(node, ContactConstant.PROP_URL));
        contact.setEmail(JsonUtil.stringProperty(node, ContactConstant.PROP_EMAIL));
        ExtensionReader.readExtensions(node, contact);
        return contact;
    }

}
