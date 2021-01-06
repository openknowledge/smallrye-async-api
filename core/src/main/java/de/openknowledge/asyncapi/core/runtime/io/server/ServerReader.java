package de.openknowledge.asyncapi.core.runtime.io.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.openknowledge.asyncapi.core.api.models.server.ServerImpl;
import de.openknowledge.asyncapi.core.runtime.io.IoLogging;
import de.openknowledge.asyncapi.core.runtime.io.JsonUtil;
import de.openknowledge.asyncapi.core.runtime.io.bindings.ServerBindingsConstants;
import de.openknowledge.asyncapi.core.runtime.io.bindings.ServerBindingsReader;
import de.openknowledge.asyncapi.core.runtime.io.extension.ExtensionReader;
import de.openknowledge.asyncapi.core.runtime.io.securityrequirement.SecurityRequirementReader;
import de.openknowledge.asyncapi.core.runtime.io.servervariable.ServerVariableReader;
import de.openknowledge.asyncapi.core.runtime.util.JandexUtil;
import io.smallrye.asyncapi.spec.models.server.Server;

/**
 * Reading the Server annotation and json node
 *
 * @see "https://www.asyncapi.com/docs/specifications/2.0.0#serverObject"
 */
public class ServerReader {

    private ServerReader() {
    }

    /**
     * Reads any Server annotations.The annotation value is an array of Server annotations.
     *
     * @param annotationValue an Array of {@literal @}Server annotations
     * @return a List of Server models
     */
    public static Optional<List<Server>> readServers(final AnnotationValue annotationValue) {
        if (annotationValue == null) {
            return Optional.empty();
        }
        IoLogging.logger.annotationsArray("@Server");
        AnnotationInstance[] nestedArray = annotationValue.asNestedArray();
        List<Server> servers = new ArrayList<>();
        for (AnnotationInstance serverAnno : nestedArray) {
            servers.add(readServer(serverAnno));
        }
        return Optional.of(servers);
    }

    /**
     * Reads a list of {@link Server} AsyncAPI nodes.
     *
     * @param node the json array
     * @return a List of Server models
     */
    public static Optional<List<Server>> readServers(final JsonNode node) {
        if (node != null && node.isArray()) {
            IoLogging.logger.jsonArray("Server");
            ArrayNode nodes = (ArrayNode) node;
            List<Server> rval = new ArrayList<>(nodes.size());
            for (JsonNode serverNode : nodes) {
                rval.add(readServer(serverNode));
            }
            return Optional.of(rval);
        }
        return Optional.empty();
    }

    /**
     * Reads a single Server annotation.
     *
     * @param annotationValue the {@literal @}Server annotation
     * @return a Server model
     */
    public static Server readServer(final AnnotationValue annotationValue) {
        if (annotationValue != null) {
            return readServer(annotationValue.asNested());
        }
        return null;
    }

    /**
     * Reads a single Server annotation.
     *
     * @param annotationInstance the {@literal @}Server annotations instance
     * @return Server model
     */
    public static Server readServer(final AnnotationInstance annotationInstance) {
        if (annotationInstance != null) {
            IoLogging.logger.singleAnnotation("@Server");
            Server server = new ServerImpl();
            server.setUrl(JandexUtil.stringValue(annotationInstance, ServerConstant.PROP_URL));
            server.setProtocol(JandexUtil.stringValue(annotationInstance, ServerConstant.PROP_PROTOCOL));
            server.setProtocolVersion(JandexUtil.stringValue(annotationInstance, ServerConstant.PROP_PROTOCOL_VERSION));
            server.setDescription(JandexUtil.stringValue(annotationInstance, ServerConstant.PROP_DESCRIPTION));
            server.setVariables(
                    ServerVariableReader.readServerVariables(annotationInstance.value(ServerConstant.PROP_VARIABLES)));
            server.setSecurityRequirements(SecurityRequirementReader
                    .readSecurityRequirements(annotationInstance.value(ServerConstant.PROP_SECURITY_REQUIREMENTS))
                    .orElse(null));
            server.setServerBindings(
                    ServerBindingsReader.readServerBindings(null, annotationInstance.value(ServerConstant.PROP_BINDINGS)));
            return server;
        }
        return null;
    }

    /**
     * Reads a list of {@link Server} AsyncAPI nodes.
     *
     * @param node the json array
     * @return a List of Server models
     */
    public static Server readServer(final JsonNode node) {
        if (node != null && node.isObject()) {
            IoLogging.logger.singleJsonNode("Server");
            Server server = new ServerImpl();
            server.setUrl(JsonUtil.stringProperty(node, ServerConstant.PROP_URL));
            server.setProtocol(JsonUtil.stringProperty(node, ServerConstant.PROP_PROTOCOL));
            server.setProtocolVersion(JsonUtil.stringProperty(node, ServerConstant.PROP_PROTOCOL_VERSION));
            server.setDescription(JsonUtil.stringProperty(node, ServerConstant.PROP_DESCRIPTION));
            server.setVariables(ServerVariableReader.readServerVariables(node.get(ServerConstant.PROP_VARIABLES)));
            server.setSecurityRequirements(SecurityRequirementReader
                    .readSecurityRequirements(node.get(ServerConstant.PROP_SECURITY_REQUIREMENTS)).orElse(null));
            server.setServerBindings(ServerBindingsReader.readServerBindings(node.get(ServerBindingsConstants.PROP_BINDINGS)));
            ExtensionReader.readExtensions(node, server);
            return server;
        }
        return null;
    }
}