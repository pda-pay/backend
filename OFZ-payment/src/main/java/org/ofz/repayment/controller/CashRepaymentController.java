package org.ofz.repayment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.repayment.dto.request.CashPrepaymentRequest;
import org.ofz.repayment.dto.response.*;
import org.ofz.repayment.service.RepaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CashRepaymentController {

    private final RepaymentService repaymentService;

    @GetMapping("/payment/cash-info")
    public ResponseEntity<PaymentInfoForCashResponse> getPaymentInfo(@RequestHeader("X-USER-ID") String userId) {

        PaymentInfoForCashResponse paymentInfo = repaymentService.getPaymentInfo(Long.parseLong(userId));

        return new ResponseEntity<>(paymentInfo, HttpStatus.OK);
    }

    @GetMapping("/payment/accounts")
    public ResponseEntity<RepaymentAccountResponse> getPaymentAccount(@RequestHeader("X-USER-ID") String userId) {

        RepaymentAccountResponse account = repaymentService.getPaymentAccount(Long.parseLong(userId));

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/payment/amount")
    public ResponseEntity<MonthlyDebtResponse> getMonthlyDebt(@RequestHeader("X-USER-ID") String userId) {

        MonthlyDebtResponse monthlyDebtResponse = repaymentService.getMonthlyDebt(Long.parseLong(userId));

        return new ResponseEntity<>(monthlyDebtResponse, HttpStatus.OK);
    }

    @PostMapping("/payment/cash")
    public ResponseEntity<CashRepaymentResponse> prepayWithCash(@RequestHeader("X-USER-ID") String userId, @RequestBody CashPrepaymentRequest cashPrepaymentRequest) {

        cashPrepaymentRequest.setUserId(Long.parseLong(userId));

        CashRepaymentResponse cashRepaymentResponse = repaymentService.prepayWithCash(cashPrepaymentRequest);

        return new ResponseEntity<>(cashRepaymentResponse, HttpStatus.OK);
    }
}
