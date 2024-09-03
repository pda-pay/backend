package org.ofz.management.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.management.dto.*;
import org.ofz.management.service.ManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Exception 처리
@RequiredArgsConstructor
@RestController
@RequestMapping("/payment")
public class ManagementController {
    private final ManagementService managementService;

    // TODO: 전일 종가 캐싱 후 종가 반환
    @GetMapping("/users/{id}/stocks")
    public ResponseEntity<UserStockResponses> getUserStocks(@PathVariable("id") String userId) {
        UserStockResponses userStockResponses = managementService.getUserStocks(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userStockResponses);
    }

    @GetMapping("/users/{id}/accounts")
    public ResponseEntity<UserAccountsResponse> getUserAccounts(@PathVariable("id") String userId) {
        UserAccountsResponse userAccountResponses = managementService.getUserAccounts(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userAccountResponses);
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentsJoinResponse> joinPaymentsService(@RequestBody PaymentsJoinRequest paymentsJoinRequest) {
        PaymentsJoinResponse paymentsJoinResponse = managementService.joinPaymentsService(paymentsJoinRequest);

        return ResponseEntity.status(HttpStatus.OK).body(paymentsJoinResponse);
    }

    // TODO: 전일 종가 캐싱 후 담보총액 계산
    @PostMapping("/payments/limits-mortgage")
    public ResponseEntity<UserCreditLimitResponse> getUserCreditLimit(@RequestBody UserCreditLimitRequest userCreditLimitRequest) {
        UserCreditLimitResponse userCreditLimitResponse = managementService.getUserCreditLimit(userCreditLimitRequest);

        return ResponseEntity.status(HttpStatus.OK).body(userCreditLimitResponse);
    }

    // TODO: 한도 유효성 검사
    @PutMapping(value="/payments/limits")
    public ResponseEntity<UpdateCreditLimitResponse> updateUserCreditLimit(@RequestBody UpdateCreditLimitRequest updateCreditRequest) {
        UpdateCreditLimitResponse updateUserCreditLimit = managementService.updateUserCreditLimit(updateCreditRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updateUserCreditLimit);
    }

    // TODO: 변경 할 담보, 우선순위 유효성 검사
    @PutMapping(value="/payments/mortgage")
    public ResponseEntity<UpdateMortgagedStockResponse> updateUserMortgagedStock(@RequestBody UpdateMortgagedStockResquest updateMortgagedStockResquest) {
        UpdateMortgagedStockResponse updateMortgagedStockResponse = managementService.updateUserMortgagedStock(updateMortgagedStockResquest);
        return ResponseEntity.status(HttpStatus.OK).body(updateMortgagedStockResponse);
    }

    // TODO: 변경 할 출금계좌 유효성 검사
    @PutMapping(value="/payments/accounts")
    public ResponseEntity<UpdateRepaymentAccountResponse> updateUserRepaymentAccount(@RequestBody UpdateRepaymentAccountRequest updateRepaymentAccountRequest) {
        UpdateRepaymentAccountResponse updateRepaymentAccountResponse = managementService.updateUserRepaymentAccount(updateRepaymentAccountRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updateRepaymentAccountResponse);
    }
}
