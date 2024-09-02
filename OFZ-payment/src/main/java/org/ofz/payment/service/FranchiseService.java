package org.ofz.payment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.repository.FranchiseRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;

}
