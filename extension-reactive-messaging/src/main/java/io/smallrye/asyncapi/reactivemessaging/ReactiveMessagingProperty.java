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
package io.smallrye.asyncapi.reactivemessaging;

import static io.smallrye.asyncapi.reactivemessaging.ReactiveMessagingConstants.IN;
import static io.smallrye.asyncapi.reactivemessaging.ReactiveMessagingConstants.INCOMING_PREFIX;
import static io.smallrye.asyncapi.reactivemessaging.ReactiveMessagingConstants.OUT;

import io.smallrye.asyncapi.reactivemessaging.io.server.ServerConstants;

public class ReactiveMessagingProperty {

    private String type;

    private String channel;

    private String attribute;

    private String value;

    public ReactiveMessagingProperty(final String type, final String channel, final String attribute, final String value) {
        this.type = type.equals(INCOMING_PREFIX) ? IN : OUT;
        this.channel = channel;
        this.attribute = attribute;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(final String channel) {
        this.channel = channel;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public boolean isSameTypeAndChannel(final ReactiveMessagingProperty other) {
        return this.type.equals(other.getType()) && this.channel.equals(other.getChannel());
    }

    public boolean isSupportedConnector() {
        return this.attribute.equals("connector") && isKafkaConnector();
    }

    public boolean isKafkaConnector() {
        return this.value.contains(ServerConstants.KAFKA);
    }

    public String getName() {
        return this.type + "_" + this.channel + "_" + this.value;
    }

    @Override
    public String toString() {
        return "ReactiveMessagingProperty{" + "type='" + type + '\'' + ", channel='" + channel + '\'' + ", attribute='"
                + attribute + '\''
                + ", value='" + value + '\'' + '}';
    }

}
