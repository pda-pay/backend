package org.ofz.management.dto.common;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private String accountNumber;
    private String companyCode;
    private String companyName;
    private String category;
}
