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
package io.smallrye.asyncapi.reactivemessaging.io;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "SRAAP", length = 5)
public interface ReactiveMessagingLogging {

    ReactiveMessagingLogging logger = Logger.getMessageLogger(ReactiveMessagingLogging.class,
            ReactiveMessagingLogging.class.getPackage().getName());

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 10000, value = "Processing a ReactiveMessaging resource class: %s")
    void processingClass(String className);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 10001, value = "Processing ReactiveMessaging method: %s")
    void processingMethod(String method);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 10002, value = "Processing a single %s annotation.")
    void singleAnnotation(String annotation);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 99999, value = "%s")
    void log(String msg);
}
