package org.ofz.payment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.dto.request.PaymentAuthRequest;
import org.ofz.payment.dto.response.PaymentResponse;
import org.ofz.payment.dto.request.PaymentRequest;
import org.ofz.payment.dto.response.PaymentTokenResponse;
import org.ofz.payment.exception.payment.PaymentIOException;
import org.ofz.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment/request")
    public ResponseEntity<PaymentResponse> payment(@RequestBody PaymentRequest paymentRequest) {

        try {
            PaymentResponse paymentResponse = paymentService.payment(paymentRequest);

            return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
        } catch (IOException e) {

            throw new PaymentIOException("결제 API 오류", e);
        }
    }

    @PostMapping("/payment/auth")
    public ResponseEntity<PaymentTokenResponse> paymentAuth(@RequestHeader("X-USER-ID") String userId,  @RequestBody PaymentAuthRequest paymentAuthRequest) {
        paymentAuthRequest.setUserId(Long.parseLong(userId));

        PaymentTokenResponse paymentTokenResponse = paymentService.createPaymentToken(paymentAuthRequest);

        return new ResponseEntity<>(paymentTokenResponse, HttpStatus.CREATED);
    }
}
