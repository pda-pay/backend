package org.ofz.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserAccountsResponse {
    private List<UserAccountResponse> accounts;
}
