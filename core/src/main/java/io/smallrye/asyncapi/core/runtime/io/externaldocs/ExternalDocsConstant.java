package io.smallrye.asyncapi.core.runtime.io.externaldocs;

import io.smallrye.asyncapi.spec.models.ExternalDocumentation;

/**
 * An implementation of the {@link ExternalDocumentation} AsyncAPI model interface.
 *
 * @see "https://www.asyncapi.com/docs/specifications/2.0.0#externalDocumentationObject"
 */
public class ExternalDocsConstant {

    public static final String PROP_EXTERNAL_DOCS = "externalDocs";

    public static final String PROP_DESCRIPTION = "description";

    public static final String PROP_URL = "url";

    private ExternalDocsConstant() {
    }
}
