package org.ofz.payment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.dto.request.FranchiseLoginRequest;
import org.ofz.payment.dto.response.FranchiseLoginResponse;
import org.ofz.payment.service.FranchiseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/franchise")
public class FranchiseController {

    private final FranchiseService franchiseService;

    @PostMapping("/login")
    public ResponseEntity<FranchiseLoginResponse> franchiseLogin(@RequestBody FranchiseLoginRequest franchiseLoginRequest) {

        FranchiseLoginResponse response = franchiseService.login(franchiseLoginRequest);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
