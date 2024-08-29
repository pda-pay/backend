package org.ofz.payment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.service.PaymentHistoryService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;
}
