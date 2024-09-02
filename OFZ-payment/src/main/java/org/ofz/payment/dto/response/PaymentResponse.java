package org.ofz.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {

    private String franchiseName;
    private int paymentAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private int leftCreditLimit;
    private String message;

    public PaymentResponse() {}

    public PaymentResponse(String franchiseName, int paymentAmount, LocalDateTime date, int leftCreditLimit, String message) {
        this.franchiseName = franchiseName;
        this.paymentAmount = paymentAmount;
        this.date = date;
        this.leftCreditLimit = leftCreditLimit;
        this.message = message;
    }
}
