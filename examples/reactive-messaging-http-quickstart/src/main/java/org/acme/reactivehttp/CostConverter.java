package org.acme.reactivehttp;

import io.smallrye.asyncapi.spec.annotations.channel.ChannelItem;
import io.smallrye.asyncapi.spec.annotations.message.Message;
import io.smallrye.asyncapi.spec.annotations.operation.Operation;
import io.smallrye.asyncapi.spec.annotations.schema.Schema;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

/**
 * A bean consuming costs in multiple currencies and producing prices in EUR from them
 */
@ApplicationScoped
public class CostConverter {

    private static final Map<String, Double> conversionRatios = new HashMap<>();

    static {
        conversionRatios.put("CHF", 0.93);
        conversionRatios.put("USD", 0.84);
        conversionRatios.put("PLN", 0.22);
        conversionRatios.put("EUR", 1.0);
    }

    @ChannelItem(
        channel = "costs",
        subscribe = @Operation(
            operationId = "costs-converter",
            message = @Message(
                name = "CostsMessage",
                contentType = "text/plain",
                summary = "A converted currency",
                payload = @Schema(
                    type = SchemaType.NUMBER,
                    name = "cost",
                    minimum = "0"
                ),
                example = {"42.24", "17.6", "87.12"}
            )
        )
    )
    @Incoming("incoming-costs")
    @Outgoing("outgoing-costs")
    double convert(Cost cost) {
        Double conversionRatio = conversionRatios.get(cost.getCurrency().toUpperCase());
        if (conversionRatio == null) {
            return 0.;
        }
        return conversionRatio * cost.getValue();
    }
}
