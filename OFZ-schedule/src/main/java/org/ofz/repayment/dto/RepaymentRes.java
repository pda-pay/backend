package org.ofz.repayment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RepaymentRes {
    private String message;
    private int processedCount;
}
