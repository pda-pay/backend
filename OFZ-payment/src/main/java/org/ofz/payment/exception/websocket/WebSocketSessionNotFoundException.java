package org.ofz.payment.exception.websocket;

public class WebSocketSessionNotFoundException extends RuntimeException {

    public WebSocketSessionNotFoundException(String message) {
        super(message);
    }
}
