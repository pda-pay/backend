package org.ofz.payment.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FranchiseLoginResponse {

    private int code;

    public FranchiseLoginResponse() {}

    public FranchiseLoginResponse(int code) {
        this.code = code;
    }
}
