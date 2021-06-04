package com.gstep.coinbaseclient;

@FunctionalInterface
public interface MessageHandler {
    void handleMessage(String message);
}
