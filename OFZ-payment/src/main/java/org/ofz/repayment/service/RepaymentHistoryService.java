package org.ofz.repayment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.exception.history.MissingParameterException;
import org.ofz.repayment.RepaymentHistory;
import org.ofz.repayment.RepaymentHistoryRepository;
import org.ofz.repayment.RepaymentHistoryResponse;
import org.ofz.repayment.exception.user.UserNotFoundException;
import org.ofz.user.User;
import org.ofz.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepaymentHistoryService {

    private final RepaymentHistoryRepository repaymentHistoryRepository;
    private final UserRepository userRepository;

    public RepaymentHistoryResponse getRepaymentHistory(int year, int month, Long userId) {

        if (year == 0 || month == 0) {
            throw new MissingParameterException("조회하고자 하는 연도/월을 설정해주세요.");
        }

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저 정보가 조회되지 않습니다."));
        LocalDateTime after = LocalDateTime.of(year, month, 1, 0,0,0);
        LocalDateTime before = LocalDateTime.of(year, month+1, 1, 0,0,0);

        List<RepaymentHistory> histories = repaymentHistoryRepository.findRepaymentHistoriesByCreatedAtBetweenAndUserId(after, before, user.getId());

        if (histories.isEmpty()) {
            return new RepaymentHistoryResponse();
        }

        RepaymentHistoryResponse response = new RepaymentHistoryResponse();

        for (RepaymentHistory history : histories) {

            RepaymentHistoryResponse.RepaymentHistoryDTO dto = RepaymentHistoryResponse.RepaymentHistoryDTO.builder()
                    .repaymentAmount(history.getRepaymentAmount())
                    .createdAt(history.getCreatedAt())
                    .type(history.getType())
                    .build();

            response.addHistory(dto);
        }

        return response;
    }
}
