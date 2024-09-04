package org.ofz.payment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.entity.PaymentHistory;
import org.ofz.payment.repository.PaymentHistoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

}
