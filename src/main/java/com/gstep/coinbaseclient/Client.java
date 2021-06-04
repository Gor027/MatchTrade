package com.gstep.coinbaseclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gstep.coinbaseclient.dao.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private final ObjectMapper objectMapper;
    private final String Url;
    private Session session;
    private MessageHandler messageHandler;

    public Client(final String Url, final ObjectMapper objectMapper) {
        this.Url = Url;
        this.objectMapper = objectMapper;
    }

    public void connect() {
        logger.info("Connecting to websocket server by URI: {}", Url);
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(Url));
        } catch (Exception e) {
            logger.error("Could not connect to remote server", e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        logger.info("Opening websocket");
        this.session = session;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("Closing websocket {}", closeReason);
        this.session = null;
    }

    @OnError
    public void onError(Throwable throwable) {
        logger.error("Websocket error", throwable);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void sendMessage(String message) {
        logger.info("Sending: " + message);
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(Subscribe message) {
        String jsonMessage = toJson(message);
        logger.info("Subscribing with message {}", jsonMessage);
        sendMessage(jsonMessage);
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Unable to serialize", e);
            throw new RuntimeException("Unable to serialize");
        }
    }
}
