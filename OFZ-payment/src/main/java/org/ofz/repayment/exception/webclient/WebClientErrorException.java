package org.ofz.repayment.exception.webclient;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class WebClientErrorException extends RuntimeException {

    private final HttpStatusCode code;

    public WebClientErrorException(String message, HttpStatusCode code) {
        super(message);
        this.code = code;
    }
}
