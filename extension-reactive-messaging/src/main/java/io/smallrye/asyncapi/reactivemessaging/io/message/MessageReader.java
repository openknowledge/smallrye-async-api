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

import org.jboss.jandex.AnnotationInstance;

import io.smallrye.asyncapi.core.api.models.message.MessageImpl;
import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.asyncapi.reactivemessaging.io.schema.SchemaReader;
import io.smallrye.asyncapi.spec.models.message.Message;

public class MessageReader {

    public static Message readOutgoingMessage(final AnnotationScannerContext context, final AnnotationInstance instance) {
        if (instance == null) {
            return null;
        }

        Message message = new MessageImpl();
        message.setPayload(SchemaReader.readReturnType(context, instance));

        return message;
    }

    public static Message readIncomingMessage(final AnnotationScannerContext context, final AnnotationInstance instance) {
        if (instance == null) {
            return null;
        }

        Message message = new MessageImpl();
        message.setPayload(SchemaReader.readParameterType(context, instance));

        return message;
    }
}
