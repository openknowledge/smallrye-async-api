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
package io.smallrye.asyncapi.core.runtime.io.message;

import org.jboss.jandex.DotName;

import io.smallrye.asyncapi.spec.annotations.message.Message;
import io.smallrye.asyncapi.spec.annotations.message.MessageTrait;

public class MessageConstant {

    static final DotName DOTNAME_MESSAGE = DotName.createSimple(Message.class.getName());

    static final DotName DOTNAME_MESSAGE_TRAIT = DotName.createSimple(MessageTrait.class.getName());

    public static final String PROP_HEADERS = "headers";

    public static final String PROP_PAYLOAD = "payload";

    public static final String PROP_CORRELATION_ID = "correlationID";

    public static final String PROP_SCHEMA_FORMAT = "schemaFormat";

    public static final String PROP_CONTENT_TYPE = "contentType";

    public static final String PROP_NAME = "name";

    public static final String PROP_TITLE = "title";

    public static final String PROP_SUMMARY = "summary";

    public static final String PROP_DESCRIPTION = "description";

    public static final String PROP_TAGS = "tags";

    public static final String PROP_EXTERNAL_DOCS = "externalDocs";

    public static final String PROP_BINDINGS = "bindings";

    public static final String PROP_EXAMPLE = "example";

    public static final String PROP_TRAITS = "traits";

    public static final String PROP_REF = "ref";

    public MessageConstant() {
    }
}
