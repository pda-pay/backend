package org.ofz.payment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.entity.Franchise;
import org.ofz.payment.exception.FranchiseNotFoundException;
import org.ofz.payment.repository.FranchiseRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;

    public Franchise getFranchise(int franchiseCode) {

        Franchise franchise = franchiseRepository
                .findFranchiseByCode(franchiseCode)
                .orElseThrow(() -> new FranchiseNotFoundException("가맹점이 조회되지 않습니다."));

        return franchise;
    }
}
