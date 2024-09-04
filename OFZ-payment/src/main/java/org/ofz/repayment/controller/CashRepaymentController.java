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
    @GetMapping("/payment/cash-info")
    public ResponseEntity<PaymentInfoForCashResponse> getPaymentInfo(@RequestHeader("X-USER-ID") String userId) {

        PaymentInfoForCashResponse paymentInfo = repaymentService.getPaymentInfo(Long.parseLong(userId));

        return new ResponseEntity<>(paymentInfo, HttpStatus.OK);
    }

    // 결제 내역 조회
    @GetMapping("/payment/history")
    public ResponseEntity<PaymentHistoriesResponse> getPaymentHistories(@RequestParam("month") int month, @RequestHeader("X-USER-ID") String userId) {

        List<PaymentHistoriesResponse.PaymentHistoryDTO> paymentHistories = repaymentService.getPaymentHistory(month, Long.parseLong(userId));

        if (paymentHistories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return new ResponseEntity<>(new PaymentHistoriesResponse(paymentHistories), HttpStatus.OK);
    }

    // 출금 계좌 조회
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

    // prepayments-cash
    @PostMapping("/payment/cash")
    public ResponseEntity<CashRepaymentResponse> prepayWithCash(@RequestHeader("X-USER-ID") String userId, @RequestBody CashPrepaymentRequest cashPrepaymentRequest) {

        cashPrepaymentRequest.setUserId(Long.parseLong(userId));

        CashRepaymentResponse cashRepaymentResponse = repaymentService.prepayWithCash(cashPrepaymentRequest);

        return new ResponseEntity<>(cashRepaymentResponse, HttpStatus.OK);
    }
}
