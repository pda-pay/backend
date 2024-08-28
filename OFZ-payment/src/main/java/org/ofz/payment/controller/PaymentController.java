package org.ofz.payment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.dto.PaymentResponse;
import org.ofz.payment.dto.PaymentRequest;
import org.ofz.payment.exception.websocket.PaymentIOException;
import org.ofz.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PaymentController {


    private final PaymentService paymentService;

    @PostMapping("/api/payment/request")
    public ResponseEntity<PaymentResponse> payment(@RequestBody PaymentRequest paymentRequest) {

        try {
            PaymentResponse paymentResponse = paymentService.payment(paymentRequest);

            return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
        } catch (IOException e) {

            throw new PaymentIOException("결제 API 오류", e);
        }
    }

    @GetMapping("/api/payment/test")
    public String test(){
        return "test";
    }

}
