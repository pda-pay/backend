package org.ofz.payment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.dto.request.FranchiseLoginRequest;
import org.ofz.payment.dto.response.FranchiseLoginResponse;
import org.ofz.payment.entity.Franchise;
import org.ofz.payment.exception.franchise.FranchiseNotFoundException;
import org.ofz.payment.exception.franchise.FranchisePasswordMismatchException;
import org.ofz.payment.repository.FranchiseRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;

    public FranchiseLoginResponse login(FranchiseLoginRequest franchiseLoginRequest) {

        int reqCode = franchiseLoginRequest.getCode();
        String reqPassword = franchiseLoginRequest.getPassword();

        Franchise franchise = franchiseRepository
                .findFranchiseByCode(reqCode)
                .orElseThrow(() -> new FranchiseNotFoundException("가맹점이 조회되지 않습니다."));

        String password = franchise.getPassword();

        System.out.println("reqPassword = " + reqPassword);
        System.out.println("password = " + password);

        if (!password.equals(reqPassword)) {
            throw new FranchisePasswordMismatchException("코드 또는 비밀번호가 잘못되었습니다.");
        }

        int code = franchise.getCode();

        return new FranchiseLoginResponse(code);
    }
}
