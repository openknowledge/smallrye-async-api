package org.acme.mqtt;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.smallrye.asyncapi.spec.annotations.server.Server;
import org.reactivestreams.Publisher;

import io.smallrye.reactive.messaging.annotations.Channel;

import io.smallrye.asyncapi.spec.annotations.AsyncAPI;
import io.smallrye.asyncapi.spec.annotations.info.Info;
import io.smallrye.asyncapi.spec.annotations.info.License;
/**
 * A simple resource retrieving the "in-memory" "my-data-stream" and sending the items to a server sent event.
 */
@AsyncAPI(
    asyncapi = "2.0.0",
    defaultContentType = "text/plain",
    info = @Info(
        title = "AMQP Quickstart API",
        version = "1.0-SNAPSHOT",
        description = "This project illustrates how you can interact with MQTT using MicroProfile Reactive Messaging.\n" + "\n",
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0")
    ),
    servers = {
        @Server(
            name = "develop",
            protocol = "mqtt",
            url = "localhost:1883"
        )
    }
)
@Path("/prices")
public class PriceResource {

    @Inject
    @Channel("my-data-stream")
    Publisher<Double> prices;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<Double> stream() {
        return prices;
    }
}
