package org.ofz.management.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.management.dto.api.response.CheckUserJoinedPaymentServiceResponse;
import org.ofz.management.dto.api.request.*;
import org.ofz.management.dto.api.response.*;
import org.ofz.management.service.ManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payment")
public class ManagementController {
    private final ManagementService managementService;

    @GetMapping("/users/{id}/stocks")
    public ResponseEntity<UserStockResponse> getUserStocks(@PathVariable("id") String userId) {
        UserStockResponse userStockResponse = managementService.getUserStocks(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userStockResponse);
    }

    @PutMapping("/users/mortgaed-stocks")
    public ResponseEntity<SavedResponse> saveUserMortgagedStocks(@RequestBody SaveMortgagedStockRequest saveMortgagedStockRequest) {
        SavedResponse saveMortgagedStockResponse = managementService.saveMortgagedStockInformation(saveMortgagedStockRequest);

        return ResponseEntity.status(HttpStatus.OK).body(saveMortgagedStockResponse);
    }

    // 사용자의 주식 담보 주식, 우선순위 정보 반환 API
    @GetMapping("/users/{id}/stock-priorities")
    public ResponseEntity<UserMortgagedStockStockPriorityResponse> getUserStockPriorities(@PathVariable("id") String userId) {
        UserMortgagedStockStockPriorityResponse userMortgagedStockStockPriorityResponse = managementService.getUserMortgagedStockStockPriority(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userMortgagedStockStockPriorityResponse);
    }

    @PutMapping("/users/stock-priorities")
    public ResponseEntity<SavedResponse> saveUserStockPriorities(@RequestBody SaveStockPriorityRequest saveStockPriorityRequest) {
        SavedResponse saveStockPriorityResponse = managementService.saveStockPriorityInformation(saveStockPriorityRequest);

        return ResponseEntity.status(HttpStatus.OK).body(saveStockPriorityResponse);
    }

    @PutMapping("/users/{id}/limit-information")
    public ResponseEntity<UserLimitResponse> getUserLimitInformation(@PathVariable("id") String userId) {
        UserLimitResponse userLimitInformation = managementService.getUserLimitInformation(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userLimitInformation);
    }

    @PutMapping("/users/limit-information")
    public ResponseEntity<SavedResponse> saveUserLimitInformation(@RequestBody SaveLimitInformationRequest saveLimitInformationRequest) {
        SavedResponse saveLimitInformationResponse = managementService.saveLimitInformation(saveLimitInformationRequest);

        return ResponseEntity.status(HttpStatus.OK).body(saveLimitInformationResponse);
    }

    @GetMapping("/users/{id}/accounts")
    public ResponseEntity<UserAccountInformationResponse> getUserAccountInformation(@PathVariable("id") String userId) {
        UserAccountInformationResponse userAccountInformationResponse = managementService.getUserAccountInformation(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userAccountInformationResponse);
    }

    @PutMapping("/users/accounts")
    public ResponseEntity<SavedResponse> saveUserRepaymentAccount(@RequestBody SaveRepaymentAccountRequest saveRepaymentAccountRequest) {
        SavedResponse saveRepaymentAccountResponse = managementService.saveRepaymentAccount(saveRepaymentAccountRequest);

        return ResponseEntity.status(HttpStatus.OK).body(saveRepaymentAccountResponse);
    }

    @PutMapping("/users/repayment-date")
    public ResponseEntity<SavedResponse> saveUserRepaymentDate(@RequestBody SaveRepaymentDateRequest saveRepaymentDateRequest) {
        SavedResponse saveRepaymentDateResponse = managementService.saveRepaymentDate(saveRepaymentDateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(saveRepaymentDateResponse);
    }

    @GetMapping("/users/{id}/information")
    public ResponseEntity<PaymentInformationResponse> getUserPaymentInformation(@PathVariable("id") String userId) {
        PaymentInformationResponse paymentInformationResponse = managementService.getPaymentInformation(userId);

        return ResponseEntity.status(HttpStatus.OK).body(paymentInformationResponse);
    }

    @GetMapping("/users/{id}/join")
    public ResponseEntity<CheckUserJoinedPaymentServiceResponse> checkUserJoinedPaymentService(@PathVariable("id") String userId) {
        CheckUserJoinedPaymentServiceResponse checkUserJoinedPaymentService = managementService.checkUserJoinedPaymentService(userId);
        return ResponseEntity.status(HttpStatus.OK).body(checkUserJoinedPaymentService);
    }

    @PostMapping("/users/join")
    public ResponseEntity<SavedResponse> joinPaymentService(@RequestBody JoinPaymentServiceRequest joinPaymentServiceRequest) {
        SavedResponse savePaymentServiceResponse = managementService.joinPaymentService(joinPaymentServiceRequest);

        return ResponseEntity.status(HttpStatus.OK).body(savePaymentServiceResponse);
    }
}
