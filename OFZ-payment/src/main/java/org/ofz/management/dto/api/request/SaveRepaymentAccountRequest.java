package org.ofz.management.dto.api.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ofz.management.dto.common.AccountDto;

@Getter
@RequiredArgsConstructor
public class SaveRepaymentAccountRequest {
    private final String loginId;
    private final AccountDto repaymentAccount;
}
