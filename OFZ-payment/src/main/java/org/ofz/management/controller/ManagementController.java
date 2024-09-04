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

    @GetMapping("/users/stocks")
    public ResponseEntity<UserStockResponse> getUserStocks(@RequestHeader("X-LOGIN-ID") String userId) {
        UserStockResponse userStockResponse = managementService.getUserStocks(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userStockResponse);
    }

    @PutMapping("/users/mortgaed-stocks")
    public ResponseEntity<SavedResponse> saveUserMortgagedStocks(@RequestHeader("X-LOGIN-ID") String userId, @RequestBody SaveMortgagedStockRequest saveMortgagedStockRequest) {
        saveMortgagedStockRequest.setLoginId(userId);
        SavedResponse saveMortgagedStockResponse = managementService.saveMortgagedStockInformation(saveMortgagedStockRequest);

        return ResponseEntity.status(HttpStatus.OK).body(saveMortgagedStockResponse);
    }

    @GetMapping("/users/stock-priorities")
    public ResponseEntity<UserMortgagedStockStockPriorityResponse> getUserStockPriorities(@RequestHeader("X-LOGIN-ID") String userId) {
        UserMortgagedStockStockPriorityResponse userMortgagedStockStockPriorityResponse = managementService.getUserMortgagedStockStockPriority(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userMortgagedStockStockPriorityResponse);
    }

    @PutMapping("/users/stock-priorities")
    public ResponseEntity<SavedResponse> saveUserStockPriorities(@RequestHeader("X-LOGIN-ID") String userId, @RequestBody SaveStockPriorityRequest saveStockPriorityRequest) {
        saveStockPriorityRequest.setLoginId(userId);
        SavedResponse saveStockPriorityResponse = managementService.saveStockPriorityInformation(saveStockPriorityRequest);

        return ResponseEntity.status(HttpStatus.OK).body(saveStockPriorityResponse);
    }

    @GetMapping("/users/limit-information")
    public ResponseEntity<UserLimitResponse> getUserLimitInformation(@RequestHeader("X-LOGIN-ID") String userId) {
        UserLimitResponse userLimitInformation = managementService.getUserLimitInformation(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userLimitInformation);
    }

    @PutMapping("/users/limit-information")
    public ResponseEntity<SavedResponse> saveUserLimitInformation(@RequestHeader("X-LOGIN-ID") String userId, @RequestBody SaveLimitInformationRequest saveLimitInformationRequest) {
        saveLimitInformationRequest.setLoginId(userId);
        SavedResponse saveLimitInformationResponse = managementService.saveLimitInformation(saveLimitInformationRequest);

        return ResponseEntity.status(HttpStatus.OK).body(saveLimitInformationResponse);
    }

    @GetMapping("/users/accounts")
    public ResponseEntity<UserAccountInformationResponse> getUserAccountInformation(@RequestHeader("X-LOGIN-ID") String userId) {
        UserAccountInformationResponse userAccountInformationResponse = managementService.getUserAccountInformation(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userAccountInformationResponse);
    }

    @PutMapping("/users/accounts")
    public ResponseEntity<SavedResponse> saveUserRepaymentAccount(@RequestHeader("X-LOGIN-ID") String userId, @RequestBody SaveRepaymentAccountRequest saveRepaymentAccountRequest) {
        saveRepaymentAccountRequest.setLoginId(userId);
        SavedResponse saveRepaymentAccountResponse = managementService.saveRepaymentAccount(saveRepaymentAccountRequest);

        return ResponseEntity.status(HttpStatus.OK).body(saveRepaymentAccountResponse);
    }

    @PutMapping("/users/repayment-date")
    public ResponseEntity<SavedResponse> saveUserRepaymentDate(@RequestHeader("X-LOGIN-ID") String userId, @RequestBody SaveRepaymentDateRequest saveRepaymentDateRequest) {
        saveRepaymentDateRequest.setLoginId(userId);
        SavedResponse saveRepaymentDateResponse = managementService.saveRepaymentDate(saveRepaymentDateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(saveRepaymentDateResponse);
    }

    @GetMapping("/users/information")
    public ResponseEntity<PaymentInformationResponse> getUserPaymentInformation(@RequestHeader("X-LOGIN-ID") String userId) {
        PaymentInformationResponse paymentInformationResponse = managementService.getPaymentInformation(userId);

        return ResponseEntity.status(HttpStatus.OK).body(paymentInformationResponse);
    }

    @GetMapping("/users/join")
    public ResponseEntity<CheckUserJoinedPaymentServiceResponse> checkUserJoinedPaymentService(@RequestHeader("X-LOGIN-ID") String userId) {
        CheckUserJoinedPaymentServiceResponse checkUserJoinedPaymentService = managementService.checkUserJoinedPaymentService(userId);
        return ResponseEntity.status(HttpStatus.OK).body(checkUserJoinedPaymentService);
    }

    @PostMapping("/users/join")
    public ResponseEntity<SavedResponse> joinPaymentService(@RequestHeader("X-LOGIN-ID") String userId, @RequestBody JoinPaymentServiceRequest joinPaymentServiceRequest) {
        joinPaymentServiceRequest.setLoginId(userId);
        SavedResponse savePaymentServiceResponse = managementService.joinPaymentService(joinPaymentServiceRequest);

        return ResponseEntity.status(HttpStatus.OK).body(savePaymentServiceResponse);
    }
}
