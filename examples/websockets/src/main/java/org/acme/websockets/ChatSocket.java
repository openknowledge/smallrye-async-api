package org.acme.websockets;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import io.smallrye.asyncapi.spec.annotations.channel.ChannelItem;
import io.smallrye.asyncapi.spec.annotations.message.Message;
import io.smallrye.asyncapi.spec.annotations.operation.Operation;
import io.smallrye.asyncapi.spec.annotations.parameter.Parameter;
import io.smallrye.asyncapi.spec.annotations.parameter.Parameters;
import io.smallrye.asyncapi.spec.annotations.schema.Schema;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;
import io.smallrye.asyncapi.spec.annotations.server.Server;
import org.jboss.logging.Logger;

import io.smallrye.asyncapi.spec.annotations.AsyncAPI;
import io.smallrye.asyncapi.spec.annotations.info.Info;
import io.smallrye.asyncapi.spec.annotations.info.License;

@AsyncAPI(
    asyncapi = "2.0.0",
    defaultContentType = "text/plain",
    info = @Info(
        title = "WebSockets Quickstart API",
        version = "1.0-SNAPSHOT",
        description = "In this guide, we create a straightforward chat application using web sockets to receive and send messages to the other connected users.",
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    servers = {
        @Server(
            url = "ws://localhost:8080",
            protocol = "ws",
            name = "develop"
        )
    }
)
@ServerEndpoint("/chat/{username}")
@ApplicationScoped
public class ChatSocket {

    private static final Logger LOG = Logger.getLogger(ChatSocket.class);

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        broadcast("User " + username + " left");
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        LOG.error("onError", throwable);
        broadcast("User " + username + " left on error: " + throwable);
    }

    @ChannelItem(
        channel = "/chat/{username}",
        parameters = @Parameters(
            @Parameter(
                name = "username",
                description = "the user which sends the message",
                schema = @Schema(
                    type = SchemaType.STRING
                )
            )
        ),
        subscribe = @Operation(
            operationId = "onMessage",
            message = @Message(
                name = "ChatMessage",
                payload = @Schema(
                    type = SchemaType.STRING,
                    name = "Message",
                    example = "Hello World"
                )
            )
        )
    )
    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        if (message.equalsIgnoreCase("_ready_")) {
            broadcast("User " + username + " joined");
        } else {
            broadcast(">> " + username + ": " + message);
        }
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

}
