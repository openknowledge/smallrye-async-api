package io.smallrye.asyncapi.apps.streetlights.dim;

import java.time.LocalDateTime;

import io.smallrye.asyncapi.spec.annotations.schema.Schema;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaProperty;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;

@Schema(name = "dimLightPayload", type = SchemaType.OBJECT, properties = {
        @SchemaProperty(name = "percentage", type = SchemaType.INTEGER, description = "Percentage to which the light should be dimmed to.", minimum = "0", maximum = "100"),
        @SchemaProperty(ref = "#/components/schemas/sentAt")
})
public class Dim {

    private int percentage;

    @Schema(type = SchemaType.STRING, format = "date-time", description = "Date and time when the message was sent.")
    private LocalDateTime sentAt;

    public Dim() {
    }

    public Dim(final int percentage, final LocalDateTime sentAt) {
        this.percentage = percentage;
        this.sentAt = sentAt;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(final int percentage) {
        this.percentage = percentage;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(final LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "Dim{" + "percentage=" + percentage + ", sentAt=" + sentAt.toString() + '}';
    }
}
