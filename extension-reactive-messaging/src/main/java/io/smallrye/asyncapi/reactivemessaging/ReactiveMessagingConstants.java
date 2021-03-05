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

import org.jboss.jandex.DotName;

public class ReactiveMessagingConstants {

    static final DotName OUTGOING = DotName.createSimple("org.eclipse.microprofile.reactive.messaging.Outgoing");

    static final DotName INCOMING = DotName.createSimple("org.eclipse.microprofile.reactive.messaging.Incoming");

    static final String CONNECTOR_PREFIX = "mp.messaging.connector.";

    static final String INCOMING_PREFIX = "mp.messaging.incoming.";

    static final String OUTGOING_PREFIX = "mp.messaging.outgoing.";

    static final String IN = "in";

    static final String OUT = "out";

}
