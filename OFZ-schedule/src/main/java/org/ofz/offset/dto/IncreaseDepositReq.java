package org.ofz.offset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IncreaseDepositReq {
    private String accountNumber;
    private int value;
}
