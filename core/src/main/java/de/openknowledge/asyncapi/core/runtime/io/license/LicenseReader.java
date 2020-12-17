package de.openknowledge.asyncapi.core.runtime.io.license;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import com.fasterxml.jackson.databind.JsonNode;

import de.openknowledge.asyncapi.core.api.models.info.LicenseImpl;
import de.openknowledge.asyncapi.core.runtime.io.IoLogging;
import de.openknowledge.asyncapi.core.runtime.io.JsonUtil;
import de.openknowledge.asyncapi.core.runtime.io.extension.ExtensionReader;
import de.openknowledge.asyncapi.core.runtime.util.JandexUtil;
import io.smallrye.asyncapi.spec.models.info.License;

/**
 * This reads the License from annotations or json
 *
 * @see <a href="https://www.asyncapi.com/docs/specifications/2.0.0/#licenseObject">License Object</a>
 */
public class LicenseReader {

    private LicenseReader() {
    }

    /**
     * Reads an License annotation.
     *
     * @param annotationValue the {@literal @}License annotation
     * @return License model
     */
    public static License readLicense(final AnnotationValue annotationValue) {
        if (annotationValue == null) {
            return null;
        }
        IoLogging.logger.singleAnnotation("@License");
        AnnotationInstance nested = annotationValue.asNested();
        License license = new LicenseImpl();
        license.setName(JandexUtil.stringValue(nested, LicenseConstant.PROP_NAME));
        license.setUrl(JandexUtil.stringValue(nested, LicenseConstant.PROP_URL));
        return license;
    }

    /**
     * Reads an {@link License} AsyncAPI node.
     *
     * @param node the json node
     * @return License model
     */
    public static License readLicense(final JsonNode node) {
        if (node == null) {
            return null;
        }
        IoLogging.logger.singleJsonNode("License");
        License license = new LicenseImpl();
        license.setName(JsonUtil.stringProperty(node, LicenseConstant.PROP_NAME));
        license.setUrl(JsonUtil.stringProperty(node, LicenseConstant.PROP_URL));
        ExtensionReader.readExtensions(node, license);
        return license;
    }
}
