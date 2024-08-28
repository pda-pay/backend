package org.ofz.payment.exception.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ConvertMessageToJsonException extends JsonProcessingException {

    public ConvertMessageToJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertMessageToJsonException(String message) {
        super(message);
    }
}
