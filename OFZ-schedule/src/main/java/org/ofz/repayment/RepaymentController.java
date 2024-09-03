package org.ofz.repayment;

import org.ofz.repayment.dto.RepaymentRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repayments")
public class RepaymentController {

    private final RepaymentService repaymentService;

    @Autowired
    public RepaymentController(RepaymentService repaymentService) {
        this.repaymentService = repaymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<RepaymentRes> processRepayments() {
        RepaymentRes response = repaymentService.processRepayments();
        return ResponseEntity.ok(response);
    }
}