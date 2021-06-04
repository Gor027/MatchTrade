package com.gstep.coinbaseclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gstep.coinbaseclient.dao.Channel;
import com.gstep.coinbaseclient.dao.Subscribe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CoinbaseClientApplication {

    @Value("${coinbase-url}")
    private String urlString;

    @Value("${ticker.instruments}")
    private String[] instruments;

    @Value("${ticker.channelName}")
    private String channelName;

    @PostConstruct
    private void initClient() {
        Client client = new Client(urlString, new ObjectMapper());
        client.setMessageHandler(MessageController::messageHandler);

        client.connect();
        client.subscribe(new Subscribe(new String[]{}, new Channel[]{new Channel(channelName, instruments)}));
    }

    public static void main(String[] args) {
        SpringApplication.run(CoinbaseClientApplication.class, args);
    }
}
