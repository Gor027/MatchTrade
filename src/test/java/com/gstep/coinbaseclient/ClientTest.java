package com.gstep.coinbaseclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gstep.coinbaseclient.dao.Channel;
import com.gstep.coinbaseclient.dao.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ClientTest {
    private Client client;

    protected static final String subscriptions = "{\"type\":\"subscriptions\"," +
            "\"channels\":[" +
            "{\"name\":\"ticker\"," +
            "\"product_ids\":[" +
            "\"BTC-USD\"," +
            "\"BTC-EUR\"," +
            "\"ETH-USD\"," +
            "\"ETH-EUR\"" +
            "]}" +
            "]}";
    protected static final String unknownResponse = "{" +
            "\"type\": \"unknown\"" +
            "}";

    @BeforeEach
    public void setUp() {
        client = new Client("URL", new ObjectMapper());
        client.setMessageHandler(MessageController::messageHandler);
    }

    @Test
    public void shouldSuccessOnSubscribe() throws IOException {
        //given
        Subscribe subscribe = new Subscribe(
                new String[]{},
                new Channel[]{new Channel("ticker", new String[]{"PRODUCT-ID-1", "PRODUCT-ID-2"})}
        );
        String subscribeMessage = new ObjectMapper().writeValueAsString(subscribe);

        Session session = mock(Session.class);
        RemoteEndpoint.Basic mockBasic = mock(RemoteEndpoint.Basic.class);

        when(session.getBasicRemote()).thenReturn(mockBasic);
        doNothing().when(mockBasic).sendText(subscribeMessage);
        client.onOpen(session);

        //when
        // As subscribe has void type and does not return anything or throw an exception, then
        // logs can be checked to make sure the behaviour of subscribe functionality is ok.
        client.subscribe(subscribe);
        Throwable thrown = catchThrowable(() -> client.onMessage(subscriptions, session));

        //then
        assertThat(thrown).isNull();
    }

    @Test
    public void shouldThrowRuntimeException() {
        //given
        Session session = mock(Session.class);

        //when
        Throwable thrown = catchThrowable(() -> client.onMessage(unknownResponse, session));

        //then
        assertThat(thrown).isNotNull();
        assertEquals("The response cannot be parsed", thrown.getMessage());
    }
}
