package org.acme.reactivehttp;

import io.smallrye.asyncapi.spec.annotations.AsyncAPI;
import io.smallrye.asyncapi.spec.annotations.info.Info;
import io.smallrye.asyncapi.spec.annotations.info.License;
import io.smallrye.asyncapi.spec.annotations.server.Server;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@AsyncAPI(
    asyncapi = "2.0.0",
    defaultContentType = "text/plain",
    info = @Info(
        title = "HTTP Quickstart API",
        version = "1.0-SNAPSHOT",
        description = "In this guide we will implement a service, namely CostConverter that consumes HTTP messages with costs in multiple currencies and converts each cost to its value in Euro."
            + "To let a user easily try out the service, we will implement an HTTP resource summing up the costs (CostCollector), and a simple web page to add new costs and watch the sum.",
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    servers = {
        @Server(
            name = "develop",
            url = "http://localhost:8080",
            protocol = "http"
        )
    }
)
@Path("/cost-collector")
@ApplicationScoped
public class CostCollector {

    private double sum = 0;

    @POST
    public synchronized void consumeCost(String valueAsString) {
        sum += Double.parseDouble(valueAsString);
    }

    @GET
    public synchronized double getSum() {
        return sum;
    }

}
