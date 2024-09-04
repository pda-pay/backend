package org.ofz.repayment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.repayment.dto.request.PawnPrepaymentRequest;
import org.ofz.repayment.dto.request.RepaymentUserRequest;
import org.ofz.repayment.dto.response.PawnRepaymentResponse;
import org.ofz.repayment.dto.response.PaymentInfoForPawnResponse;
import org.ofz.repayment.service.PawnRepaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PawnRepaymentController {

    private final PawnRepaymentService pawnRepaymentService;

    // 총 결제 금액
    // 담보로 잡은 주식들
    @GetMapping("/payment/pawn-info")
    public ResponseEntity<PaymentInfoForPawnResponse> getPaymentInfo(@RequestBody RepaymentUserRequest repaymentUserRequest) {

        PaymentInfoForPawnResponse response = pawnRepaymentService.getPaymentInfo(repaymentUserRequest);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 전일 종가 * 퍼센트

    // 담보 결제 시도
    @PostMapping("/payment/pawn")
    public ResponseEntity<PawnRepaymentResponse> prepayWithPawn(@RequestBody PawnPrepaymentRequest pawnPrepaymentRequest) {

        PawnRepaymentResponse response = pawnRepaymentService.prepayWithPawn(pawnPrepaymentRequest);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
