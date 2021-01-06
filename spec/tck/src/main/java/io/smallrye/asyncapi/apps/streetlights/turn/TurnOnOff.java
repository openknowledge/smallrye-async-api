package io.smallrye.asyncapi.apps.streetlights.turn;

import java.time.LocalDateTime;

import io.smallrye.asyncapi.spec.annotations.schema.Schema;

@Schema(name = "turnOnOffPayload")
public class TurnOnOff {

    private Command command;

    private LocalDateTime sentAt;

    public TurnOnOff() {
    }

    public TurnOnOff(final Command command, final LocalDateTime sentAt) {
        this.command = command;
        this.sentAt = sentAt;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(final Command command) {
        this.command = command;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(final LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "TurnOnOff{" + "command=" + command + ", sentAt=" + sentAt.toString() + '}';
    }
}
