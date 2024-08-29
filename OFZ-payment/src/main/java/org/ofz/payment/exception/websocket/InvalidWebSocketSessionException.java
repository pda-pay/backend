package org.ofz.payment.exception.websocket;

public class InvalidWebSocketSessionException extends IllegalStateException {

    public InvalidWebSocketSessionException(String message) {
        super(message);
    }
}
