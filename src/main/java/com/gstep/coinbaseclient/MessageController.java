package com.gstep.coinbaseclient;

import com.gstep.coinbaseclient.dao.TickerMessage;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MessageController {
    private static final Map<String, TickerMessage> products = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @GetMapping("/")
    public TickerMessage[] tickerInstruments() {
        TickerMessage[] result = products.values().toArray(new TickerMessage[0]);
        logger.info("Request for ticker instruments is responded successfully");

        return result;
    }

    public static void messageHandler(String message) {
        JSONObject object = new JSONObject(message);
        String type = object.getString("type"); // Taken into consideration that all responses have type field

        switch (type) {
            case "ticker":
                logger.info("Processing message from ticker channel");
                LocalTime messageTime = LocalDateTime.ofInstant(
                        Instant.parse(object.getString("time")), ZoneId.of(ZoneOffset.UTC.getId())
                ).toLocalTime();
                TickerMessage tickerMessage = TickerMessage.builder()
                        .instrument(object.getString("product_id"))
                        .bid(object.getBigDecimal("best_bid"))
                        .ask(object.getBigDecimal("best_ask"))
                        .last(object.getBigDecimal("price"))
                        .time(messageTime)
                        .build();

                products.put(
                        object.getString("product_id"),
                        tickerMessage
                );
                break;
            case "subscriptions":
                logger.info("Successfully subscribed to the channel");
                break;
            case "error":
                logger.error("Responded with error message: {}", object.getString("reason"));
                break;
            default:
                logger.error("The response type {} is not supported", type);
                throw new RuntimeException("The response cannot be parsed");
        }
    }
}
