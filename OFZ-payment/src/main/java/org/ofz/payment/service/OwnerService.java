package org.ofz.payment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.repository.OwnerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
}
