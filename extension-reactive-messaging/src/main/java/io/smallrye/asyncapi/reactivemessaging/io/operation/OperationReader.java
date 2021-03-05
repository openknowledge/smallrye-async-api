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
package io.smallrye.asyncapi.reactivemessaging.io.operation;

import org.jboss.jandex.AnnotationInstance;

import io.smallrye.asyncapi.core.api.models.operation.OperationImpl;
import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.asyncapi.core.runtime.util.JandexUtil;
import io.smallrye.asyncapi.reactivemessaging.io.message.MessageReader;
import io.smallrye.asyncapi.spec.models.operation.Operation;

public class OperationReader {

    public static Operation readPublish(final AnnotationScannerContext context, final AnnotationInstance instance) {
        if (instance == null) {
            return null;
        }

        Operation operation = new OperationImpl();
        operation.setOperationId(JandexUtil.stringValue(instance, "value"));
        operation.setMessage(MessageReader.readOutgoingMessage(context, instance));

        return operation;
    }

    public static Operation readSubscribe(final AnnotationScannerContext context, final AnnotationInstance instance) {
        if (instance == null) {
            return null;
        }

        Operation operation = new OperationImpl();
        operation.setOperationId(JandexUtil.stringValue(instance, "value"));
        operation.setMessage(MessageReader.readIncomingMessage(context, instance));

        return operation;
    }
}
