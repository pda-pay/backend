package org.ofz.payment.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.service.OwnerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

}
