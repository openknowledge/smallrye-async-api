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
package io.smallrye.asyncapi.reactivemessaging.io.message;

import io.smallrye.asyncapi.core.runtime.io.bindings.MessageBindingsReader;
import io.smallrye.asyncapi.core.runtime.io.channels.ChannelsConstants;
import io.smallrye.asyncapi.core.runtime.io.correlationId.CorrelationIdReader;
import io.smallrye.asyncapi.core.runtime.io.externaldocs.ExternalDocsReader;
import io.smallrye.asyncapi.core.runtime.io.message.MessageConstant;
import io.smallrye.asyncapi.core.runtime.io.message.MessageTraitReader;
import io.smallrye.asyncapi.core.runtime.io.operation.OperationConstant;
import io.smallrye.asyncapi.core.runtime.io.schema.SchemaFactory;
import io.smallrye.asyncapi.core.runtime.io.tag.TagReader;
import io.smallrye.asyncapi.core.runtime.util.JandexUtil;
import io.smallrye.asyncapi.reactivemessaging.ReactiveMessagingContext;
import io.smallrye.asyncapi.reactivemessaging.io.schema.CloudEventConfigReader;
import io.smallrye.asyncapi.reactivemessaging.util.CloudEventUtil;
import io.smallrye.asyncapi.spec.models.schema.Schema;
import org.jboss.jandex.AnnotationInstance;

import io.smallrye.asyncapi.core.api.models.message.MessageImpl;
import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.asyncapi.reactivemessaging.io.schema.SchemaReader;
import io.smallrye.asyncapi.spec.models.message.Message;
import org.jboss.jandex.AnnotationValue;

public class MessageReader {

    public static Message readOutgoingMessage(final AnnotationScannerContext context, final ReactiveMessagingContext rmContext, final AnnotationInstance instance) {
        if (instance == null) {
            return null;
        }

        Message message = new MessageImpl();
        message.setName(getName(instance));

        populateMessage(context, instance, message);

        if (CloudEventUtil.isCloudEvent(instance)){
            Schema payload = CloudEventUtil.readCloudEventReturnType(instance, context);
            message.setPayload(payload);

            return message;
        }

        Schema payload = SchemaReader.readReturnType(context, instance);
        payload = CloudEventConfigReader.readCloudEventFromConfig(instance, context, payload);

        message.setPayload(payload);

        return message;
    }

    public static Message readIncomingMessage(final AnnotationScannerContext context, final AnnotationInstance instance) {
        if (instance == null) {
            return null;
        }

        Message message = new MessageImpl();
        message.setPayload(SchemaReader.readParameterType(context, instance));
        message.setName(getName(instance));

        populateMessage(context, instance, message);

        return message;
    }

    private static String getName(final AnnotationInstance instance) {
        return String.format("%sPayload", instance.target().asMethod().name());
    }

    private static void populateMessage(final AnnotationScannerContext context, final AnnotationInstance instance, final Message message){
        if (instance == null){
            return;
        }

        AnnotationInstance channelItem = getChannelItemAnnotation(instance);
        if (channelItem == null){
            return;
        }

        message.setHeaders(SchemaFactory.readSchema(context, channelItem.value(MessageConstant.PROP_HEADERS)));
        message.setCorrelationID(CorrelationIdReader.readCorrelationID(channelItem.value(MessageConstant.PROP_CORRELATION_ID)));
        message.setSchemaFormat(JandexUtil.stringValue(channelItem, MessageConstant.PROP_SCHEMA_FORMAT));
        message.setContentType(JandexUtil.stringValue(channelItem, MessageConstant.PROP_CONTENT_TYPE));
        message.setTitle(JandexUtil.stringValue(channelItem, MessageConstant.PROP_TITLE));
        message.setSummary(JandexUtil.stringValue(channelItem, MessageConstant.PROP_SUMMARY));
        message.setDescription(JandexUtil.stringValue(channelItem, MessageConstant.PROP_DESCRIPTION));
        message.setTraits(
            MessageTraitReader.readMessageTraits(context, channelItem.value(MessageConstant.PROP_TRAITS)).orElse(null));
        message.setTags(TagReader.readTags(context, channelItem.value(MessageConstant.PROP_TAGS)).orElse(null));
        message.setBindings(MessageBindingsReader.readMessageBindings(context, channelItem.value(MessageConstant.PROP_BINDINGS)));
        message.setExample(JandexUtil.stringListValue(channelItem, MessageConstant.PROP_EXAMPLE).orElse(null));
        message.setExternalDocumentation(
            ExternalDocsReader.readExternalDocs(context, channelItem.value(MessageConstant.PROP_EXTERNAL_DOCS)));
        message.setRef(JandexUtil.refValue(channelItem, JandexUtil.RefType.MESSAGE));
    }

    private static AnnotationInstance getChannelItemAnnotation(final AnnotationInstance instance) {
        AnnotationInstance annotation = instance.target()
            .asMethod()
            .annotation(ChannelsConstants.DOTNAME_CHANNEL_ITEM);

        if (annotation == null){
            return null;
        }

        AnnotationValue operation = annotation.value(ChannelsConstants.PROP_PUBLISH);
        if (operation == null){
            operation = annotation.value(ChannelsConstants.PROP_SUBSCRIBE);
        }

        AnnotationValue message = operation.asNested()
            .value(OperationConstant.PROP_MESSAGE);

        return message.asNested();
    }
}
