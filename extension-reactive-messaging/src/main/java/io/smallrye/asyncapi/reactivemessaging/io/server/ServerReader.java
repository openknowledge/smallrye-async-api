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
 */
package io.smallrye.asyncapi.reactivemessaging.io.server;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import io.smallrye.asyncapi.core.api.models.server.ServerImpl;
import io.smallrye.asyncapi.reactivemessaging.ReactiveMessagingContext;
import io.smallrye.asyncapi.reactivemessaging.ReactiveMessagingProperty;
import io.smallrye.asyncapi.spec.models.AsyncAPI;
import io.smallrye.asyncapi.spec.models.server.Server;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

public class ServerReader {

    private List<ReactiveMessagingProperty> incomingConnector;

    private List<ReactiveMessagingProperty> outgoingConnector;

    private final ReactiveMessagingContext rmContext;

    private AsyncAPI aai;

    private List<ReactiveMessagingProperty> connectors;

    public ServerReader(final ReactiveMessagingContext rmContext, final AsyncAPI aai) {
        this.rmContext = rmContext;
        this.aai = aai;

        extractConnectors();

        this.connectors = new ArrayList<>();
        connectors.addAll(this.incomingConnector);
        connectors.addAll(this.outgoingConnector);
    }

    private void extractConnectors() {
        this.incomingConnector = this.rmContext.getIncomingChannels().stream()
                .filter(p -> "connector".equals(p.getAttribute()))
                .collect(Collectors.toList());

        this.outgoingConnector = this.rmContext.getOutgoingChannels().stream()
                .filter(p -> "connector".equals(p.getAttribute()))
                .collect(Collectors.toList());
    }

    public void readServers() {
        List<ReactiveMessagingProperty> supported = this.connectors.stream()
                .filter(ReactiveMessagingProperty::isSupportedConnector)
                .collect(Collectors.toList());

        List<Server> servers = supported.stream()
            .map(this::readServer)
            .collect(Collectors.toList());

        if (servers == null || servers.isEmpty()) {
            return;
        }

        this.aai.setServers(getUniqueServer(servers));
    }
    // remove server if url is already used
    private List<Server> getUniqueServer(final List<Server> servers){
        return servers.stream()
            .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(Server::getUrl))), ArrayList::new));
    }

    private Server readServer(final ReactiveMessagingProperty property) {
        if (property.isKafkaConnector()) {
            return readKafkaServer(property);
        }

        return null;
    }

    private Server readKafkaServer(final ReactiveMessagingProperty property) {
        Server server = new ServerImpl();
        server.setProtocol(ServerConstants.KAFKA);
        server.setName(property.getName());

        String url = getURL(property);
        if (url == null) {
            return null;
        }

        server.setUrl(url);

        return server;
    }

    private String getURL(final ReactiveMessagingProperty property) {
        ArrayList<ReactiveMessagingProperty> channels = new ArrayList<>();
        channels.addAll(this.rmContext.getIncomingChannels());
        channels.addAll(this.rmContext.getOutgoingChannels());

        return channels.stream()
                .filter(property::isSameTypeAndChannel)
                .filter(ServerReader::isBootstrapURL)
                .map(ReactiveMessagingProperty::getValue)
                .findFirst()
                .orElse(null);
    }

    private static boolean isBootstrapURL(ReactiveMessagingProperty reactiveMessagingProperty) {
        return reactiveMessagingProperty.getAttribute().contains(ServerConstants.KAFKA_URL);
    }
}
