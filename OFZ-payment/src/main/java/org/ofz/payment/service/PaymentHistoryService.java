package org.ofz.payment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.repository.PaymentHistoryRepository;
import org.ofz.repayment.dto.response.PaymentHistoriesResponse;
import org.ofz.repayment.exception.user.UserNotFoundException;
import org.ofz.user.User;
import org.ofz.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserRepository userRepository;

    public List<PaymentHistoriesResponse.PaymentHistoryDTO> getPaymentHistory(int month, Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저 정보가 조회되지 않습니다."));

        return paymentHistoryRepository.findPaymentHistoryByUserIdAndMonth(user.getId(), month);
    }
}
