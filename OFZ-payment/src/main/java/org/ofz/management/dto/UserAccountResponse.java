package org.ofz.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountResponse {
    private String accountNumber;
    private int deposit;
    private String companyCode;
    private String category;
}
