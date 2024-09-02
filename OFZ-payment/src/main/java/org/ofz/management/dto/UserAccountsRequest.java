package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class UserAccountsRequest {
    private final String name;
    private final String phoneNumber;
}
