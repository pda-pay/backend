package org.ofz.repayment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.repayment.dto.request.PawnRepaymentRequest;
import org.ofz.repayment.dto.response.PawnRepaymentResponse;
import org.ofz.repayment.dto.response.PaymentInfoForPawnResponse;
import org.ofz.repayment.service.PawnRepaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PawnRepaymentController {

    private final PawnRepaymentService pawnRepaymentService;

    @GetMapping("/payment/pawn-info")
    public ResponseEntity<PaymentInfoForPawnResponse> getPaymentInfo(@RequestHeader("X-USER-ID") String userId) {

        PaymentInfoForPawnResponse response = pawnRepaymentService.getPaymentInfo(Long.parseLong(userId));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/payment/pawn")
    public ResponseEntity<PawnRepaymentResponse> prepayWithPawn(@RequestHeader("X-USER-ID") String userId, @RequestBody PawnRepaymentRequest pawnRepaymentRequest) {

        PawnRepaymentResponse response = pawnRepaymentService.repayWithPawn(Long.parseLong(userId), pawnRepaymentRequest);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
