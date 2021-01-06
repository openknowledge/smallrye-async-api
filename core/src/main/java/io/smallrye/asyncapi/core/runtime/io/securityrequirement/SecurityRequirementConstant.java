package io.smallrye.asyncapi.core.runtime.io.securityrequirement;

import org.jboss.jandex.DotName;

import io.smallrye.asyncapi.spec.annotations.security.SecurityRequirement;

/**
 * Lists the required security schemes to execute this operation.
 * The name used for each property MUST correspond to a security scheme declared in the Security Schemes under the Components
 * Object.
 *
 * @see "https://www.asyncapi.com/docs/specifications/2.0.0/#securityRequirementObject"
 */
public class SecurityRequirementConstant {

    static final DotName DOTNAME_SECURITY_REQUIREMENT = DotName.createSimple(SecurityRequirement.class.getName());

    public static final String PROP_NAME = "name";
    public static final String PROP_VALUES = "values";

    private SecurityRequirementConstant() {
    }
}
