package org.ofz.repayment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.repayment.dto.request.CashPrepaymentRequest;
import org.ofz.repayment.dto.request.PaymentHistoryRequest;
import org.ofz.repayment.dto.request.RepaymentUserRequest;
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

    // 결제 페이지
    @GetMapping("/payment/info")
    public ResponseEntity<PaymentInfoResponse> getPaymentInfo(@RequestBody RepaymentUserRequest repaymentUserRequest) {

        PaymentInfoResponse paymentInfo = repaymentService.getPaymentInfo(repaymentUserRequest);

        return new ResponseEntity<>(paymentInfo, HttpStatus.OK);
    }

    // 결제 내역 조회
    @GetMapping("/payment/history")
    public ResponseEntity<PaymentHistoriesResponse> getPaymentHistories(@RequestParam("month") int month, @RequestBody PaymentHistoryRequest paymentHistoryRequest) {

        List<PaymentHistoriesResponse.PaymentHistoryDTO> paymentHistories = repaymentService.getPaymentHistory(month, paymentHistoryRequest);

        if (paymentHistories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return new ResponseEntity<>(new PaymentHistoriesResponse(paymentHistories), HttpStatus.OK);
    }

    // 출금 계좌 조회
    @GetMapping("/payment/accounts")
    public ResponseEntity<RepaymentAccountResponse> getPaymentAccount(@RequestBody RepaymentUserRequest repaymentUserRequest) {

        RepaymentAccountResponse account = repaymentService.getPaymentAccount(repaymentUserRequest);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/payment/amount")
    public ResponseEntity<MonthlyDebtResponse> getMonthlyDebt(@RequestBody RepaymentUserRequest repaymentUserRequest) {

        MonthlyDebtResponse monthlyDebtResponse = repaymentService.getMonthlyDebt(repaymentUserRequest);

        return new ResponseEntity<>(monthlyDebtResponse, HttpStatus.OK);
    }

    // prepayments-cash
    @PostMapping("/payment/cash")
    public ResponseEntity<CashRepaymentResponse> prepayWithCash(@RequestBody CashPrepaymentRequest cashPrepaymentRequest) {

        CashRepaymentResponse cashRepaymentResponse = repaymentService.prepayWithCash(cashPrepaymentRequest);

        return new ResponseEntity<>(cashRepaymentResponse, HttpStatus.OK);
    }
}
