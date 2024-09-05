package org.ofz.payment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.service.PaymentHistoryService;
import org.ofz.repayment.dto.response.PaymentHistoriesResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;

    @GetMapping("/payment/history")
    public ResponseEntity<PaymentHistoriesResponse> getPaymentHistories(
            @RequestParam(value = "year", required = false, defaultValue = "0") int year,
            @RequestParam(value = "month", required = false, defaultValue = "0") int month,
            @RequestHeader("X-USER-ID") String userId)
    {
        List<PaymentHistoriesResponse.PaymentHistoryDTO> paymentHistories = paymentHistoryService.getPaymentHistory(year, month, Long.parseLong(userId));

        if (paymentHistories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return new ResponseEntity<>(new PaymentHistoriesResponse(paymentHistories), HttpStatus.OK);
    }
}
