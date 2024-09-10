package org.ofz.repayment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.repayment.dto.response.RepaymentHistoryResponse;
import org.ofz.repayment.service.RepaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RepaymentHistoryController {

    private final RepaymentService repaymentService;

    @GetMapping("/payment/repayment-history")
    public ResponseEntity<RepaymentHistoryResponse> getRepaymentHistory(@RequestHeader("X-USER-ID") String userId) {

        Long id = Long.parseLong(userId);

        RepaymentHistoryResponse response = repaymentService.getRepaymentHistory(id);

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
