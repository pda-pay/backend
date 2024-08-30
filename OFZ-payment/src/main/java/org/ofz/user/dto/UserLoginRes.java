package org.ofz.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginRes {
    private final String loginId;
    private final String name;

}
