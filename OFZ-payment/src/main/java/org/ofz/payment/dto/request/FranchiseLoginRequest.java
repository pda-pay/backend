package org.ofz.payment.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FranchiseLoginRequest {

    private int code;
    private String password;

    public FranchiseLoginRequest() {}

    public FranchiseLoginRequest(int code, String password) {
        this.code = code;
        this.password = password;
    }
}
