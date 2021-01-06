package io.smallrye.asyncapi.apps.streetlights.measure;

import java.time.LocalDateTime;

import io.smallrye.asyncapi.spec.annotations.schema.Schema;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaProperty;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;

@Schema(name = "lightMeasuredPayload", type = SchemaType.OBJECT, properties = {
        @SchemaProperty(name = "lumens", type = SchemaType.INTEGER, description = "Light intensity measured in lumens.", minimum = "0"),
        @SchemaProperty(ref = "#/components/schemas/sentAt")
})
public class LightMeasured {

    private int lumens;

    private LocalDateTime sentAt;

    public LightMeasured() {
    }

    public LightMeasured(final int lumens, final LocalDateTime sentAt) {
        this.lumens = lumens;
        this.sentAt = sentAt;
    }

    public int getLumens() {
        return lumens;
    }

    public void setLumens(final int lumens) {
        this.lumens = lumens;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(final LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "LightMeasured{" + "lumens=" + lumens + ", sentAt=" + sentAt + '}';
    }
}
