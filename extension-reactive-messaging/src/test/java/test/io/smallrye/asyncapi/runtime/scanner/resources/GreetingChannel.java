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
package test.io.smallrye.asyncapi.runtime.scanner.resources;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.asyncapi.spec.annotations.AsyncAPI;
import io.smallrye.asyncapi.spec.annotations.info.Info;

@AsyncAPI(asyncapi = "2.0.0", defaultContentType = "application/json", info = @Info(version = "1.0.0", title = "GreetingChannel"))
public class GreetingChannel {

    private boolean caps;

    @Outgoing("hello")
    public String hello() {
        return caps ? "HELLO WORLD" : "Hello World";
    }

    @Incoming("caps")
    public void toCaps(final boolean caps) {
        this.caps = caps;
    }

    @Incoming("lower-in")
    @Outgoing("lower-out")
    public String lower(final Greeting greeting){
        return greeting.greet();
    }
}
