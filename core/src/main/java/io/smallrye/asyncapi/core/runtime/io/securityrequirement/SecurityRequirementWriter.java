package io.smallrye.asyncapi.core.runtime.io.securityrequirement;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.asyncapi.core.runtime.io.ObjectWriter;
import io.smallrye.asyncapi.core.runtime.io.server.ServerConstant;
import io.smallrye.asyncapi.spec.models.security.SecurityRequirement;

/**
 * Lists the required security schemes to execute this operation.
 * The name used for each property MUST correspond to a security scheme declared in the Security Schemes under the Components
 * Object.
 *
 * @see "https://www.asyncapi.com/docs/specifications/2.0.0/#securityRequirementObject"
 */
public class SecurityRequirementWriter {
    private SecurityRequirementWriter() {
    }

    /**
     * Writes a list of {@link SecurityRequirement} to the JSON tree.
     *
     * @param parent the parent json node
     * @param models list of SecurityRequirement models
     */
    public static void writeSecurityRequirements(ObjectNode parent, List<SecurityRequirement> models) {
        if (models == null) {
            return;
        }
        ArrayNode node = parent.putArray(ServerConstant.PROP_SECURITY_REQUIREMENTS);
        for (SecurityRequirement securityRequirement : models) {
            ObjectNode secNode = node.addObject();
            writeSecurityRequirement(secNode, securityRequirement);
        }
    }

    /**
     * Writes a {@link SecurityRequirement} to the given JS node.
     *
     * @param node
     * @param model
     */
    private static void writeSecurityRequirement(ObjectNode node, SecurityRequirement model) {
        if (model == null) {
            return;
        }
        if (model.getSchemes() != null) {
            for (Map.Entry<String, List<String>> entry : model.getSchemes().entrySet()) {
                ObjectWriter.writeStringArray(node, entry.getValue(), entry.getKey());
            }
        }
    }
}
