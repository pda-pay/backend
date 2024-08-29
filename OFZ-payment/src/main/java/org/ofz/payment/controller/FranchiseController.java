package org.ofz.payment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.service.FranchiseService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;
}
