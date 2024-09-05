package org.ofz.payment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.exception.history.MissingParameterException;
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

    public List<PaymentHistoriesResponse.PaymentHistoryDTO> getPaymentHistory(int year, int month, Long userId) {

        if (year == 0 || month == 0) {
            throw new MissingParameterException("조회하고자 하는 연도/월을 설정해주세요.");
        }

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저 정보가 조회되지 않습니다."));

        return paymentHistoryRepository.findPaymentHistoryByUserIdAndYearAndMonth(user.getId(), year, month);
    }
}
