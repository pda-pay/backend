package org.ofz.repayment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.repayment.RepaymentHistoryResponse;
import org.ofz.repayment.service.RepaymentHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RepaymentHistoryController {

    private final RepaymentHistoryService repaymentHistoryService;

    @GetMapping("/payment/repayment-history")
    public ResponseEntity<RepaymentHistoryResponse> getRepaymentHistory(
            @RequestHeader("X-USER-ID") String userId,
            @RequestParam(value = "year", required = false, defaultValue = "0") int year,
            @RequestParam(value = "month", required = false, defaultValue = "0") int month
    ) {
        Long id = Long.parseLong(userId);

        RepaymentHistoryResponse response = repaymentHistoryService.getRepaymentHistory(year, month, id);

        if (response.getRepaymentHistories().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
