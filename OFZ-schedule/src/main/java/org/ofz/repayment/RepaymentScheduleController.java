package org.ofz.repayment;

import org.ofz.repayment.dto.RepaymentRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedule")
public class RepaymentScheduleController {

    private final RepaymentScheduleService repaymentScheduleService;

    @Autowired
    public RepaymentScheduleController(RepaymentScheduleService repaymentScheduleService) {
        this.repaymentScheduleService = repaymentScheduleService;
    }

    
    @PostMapping("/process")
    public ResponseEntity<RepaymentRes> processRepayments() {
        RepaymentRes response = repaymentScheduleService.processRepayments();
        return ResponseEntity.ok(response);
    }
}