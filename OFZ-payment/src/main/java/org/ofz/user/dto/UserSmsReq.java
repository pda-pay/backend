package org.ofz.user.dto;

import lombok.Getter;

@Getter
public class UserSmsReq {
    private String loginId;
    private String phoneNumber;
}
