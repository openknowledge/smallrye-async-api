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
package io.smallrye.asyncapi.reactivemessaging.io.channel;

import io.smallrye.asyncapi.reactivemessaging.ReactiveMessagingContext;
import io.smallrye.asyncapi.reactivemessaging.ReactiveMessagingProperty;
import org.jboss.jandex.AnnotationInstance;

import io.smallrye.asyncapi.core.api.models.channel.ChannelItemImpl;
import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.asyncapi.core.runtime.util.JandexUtil;
import io.smallrye.asyncapi.reactivemessaging.io.ReactiveMessagingLogging;
import io.smallrye.asyncapi.reactivemessaging.io.operation.OperationReader;
import io.smallrye.asyncapi.spec.models.channel.ChannelItem;

import java.util.List;

public class ChannelReader {

    public static ChannelItem readOutgoing(final AnnotationScannerContext context, final ReactiveMessagingContext rmContext, final AnnotationInstance instance) {
        if (instance == null) {
            return null;
        }

        ReactiveMessagingLogging.logger.singleAnnotation("@Outgoing");

        ChannelItem channelItem = new ChannelItemImpl();
        String channel = readTopic(rmContext, instance);
        channelItem.setChannel(channel);
        channelItem.setPublish(OperationReader.readPublish(context, instance));

        return channelItem;
    }

    public static ChannelItem readIncoming(final AnnotationScannerContext context, final ReactiveMessagingContext rmContext, final AnnotationInstance instance) {
        if (instance == null) {
            return null;
        }

        ReactiveMessagingLogging.logger.singleAnnotation("@Incoming");

        ChannelItem channelItem = new ChannelItemImpl();
        String channel = readTopic(rmContext, instance);
        channelItem.setChannel(channel);
        channelItem.setSubscribe(OperationReader.readSubscribe(context, instance));

        return channelItem;
    }

    private static String readTopic(final ReactiveMessagingContext rmContext, final AnnotationInstance instance){
        String channel = JandexUtil.stringValue(instance, "value");

        String type = instance.name().withoutPackagePrefix();

        switch(type){
        case "Outgoing":
            return getTopic(rmContext.getOutgoingChannels(), channel);
        case "Incoming":
            return getTopic(rmContext.getIncomingChannels(), channel);
        }

        return channel;
    }

    private static String getTopic(final List<ReactiveMessagingProperty> source, final String channel) {
        return source
            .stream()
            .filter(property -> channel.equals(property.getChannel()) && "topic".equals(property.getAttribute()))
            .map(property -> property.getValue())
            .findFirst()
            .orElse(channel);
    }
}
