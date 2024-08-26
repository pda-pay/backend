package org.ofz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @GetMapping("/api/payment/test")
    public String test(){

        return "test";
    }

}
