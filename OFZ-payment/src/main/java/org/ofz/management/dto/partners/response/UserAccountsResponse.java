package org.ofz.management.dto.partners.response;

import lombok.Getter;
import lombok.Setter;
import org.ofz.management.dto.common.AccountDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserAccountsResponse {
    private List<UserAccountDto> accounts;

    public List<UserAccountDto> getBankAccounts() {
        List<UserAccountDto> userAccountDtos = new ArrayList<>();
        for (UserAccountDto userAccountDto : userAccountDtos) {
            if (userAccountDto.getCategory().equals("01")) {
                userAccountDtos.add(userAccountDto);
            }
        }
        return userAccountDtos;
    }
    @Setter
    @Getter
    public static class UserAccountDto {
        private String accountNumber;
        private int deposit;
        private String companyCode;
        private String category;
    }
}
