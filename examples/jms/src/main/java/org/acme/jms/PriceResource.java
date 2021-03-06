package org.acme.jms;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.smallrye.asyncapi.spec.annotations.AsyncAPI;
import io.smallrye.asyncapi.spec.annotations.info.Info;
import io.smallrye.asyncapi.spec.annotations.info.License;
import io.smallrye.asyncapi.spec.annotations.server.Server;

/**
 * A simple resource showing the last price.
 */
@AsyncAPI(
    asyncapi = "2.0.0",
    defaultContentType = "text/plain",
    info = @Info(
        title = "JMS Quickstart API",
        version = "1.0-SNAPSHOT",
        description = "In this guide, we are going to generate (random) prices in one component."
            + " These prices are written in a JMS queue (prices)."
            + " A second component reads from the JMS prices queue and apply some magic conversion to the price."
            + " The result is sent to an in-memory stream consumed by a JAX-RS resource."
            + " The data is sent to a browser using server-sent events.",
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    servers = {
        @Server(
            name = "develop",
            url = "tcp://localhost:61616",
            protocol = "jms"
        )
    }
)
@Path("/prices")
public class PriceResource {

    @Inject
    PriceConsumer consumer;

    @GET
    @Path("last")
    @Produces(MediaType.TEXT_PLAIN)
    public String last() {
        return consumer.getLastPrice();
    }
}
