package org.ofz.management.dto.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ofz.management.dto.common.AccountDto;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserAccountInformationResponse {
    private List<AccountDto> accounts;

    public void addAccount(AccountDto accountDto) {
        accounts.add(accountDto);
    }
}
