package org.ofz.repayment.exception.webclient;

import lombok.Getter;

@Getter
public class WebClientIllegalArgumentException extends RuntimeException {

    private final String errorMessage;

    public WebClientIllegalArgumentException(String message, String errorMessage) {
        super(message);
        this.errorMessage = errorMessage;
    }
}
