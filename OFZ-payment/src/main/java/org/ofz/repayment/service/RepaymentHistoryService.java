package org.ofz.repayment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.repayment.AmountAndDateAndTypeProjection;
import org.ofz.repayment.RepaymentHistoryRepository;
import org.ofz.repayment.RepaymentType;
import org.ofz.repayment.dto.response.RepaymentHistoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepaymentHistoryService {

    private final RepaymentHistoryRepository repaymentHistoryRepository;

    public RepaymentHistoryResponse getRepaymentHistory(Long userId) {

        List<AmountAndDateAndTypeProjection> histories = repaymentHistoryRepository.findRepaymentHistoriesByUserIdOrderByIdAsc(userId);

        if (histories.isEmpty()) {
            return null;
        }

        RepaymentHistoryResponse response = new RepaymentHistoryResponse();

        for (AmountAndDateAndTypeProjection repaymentHistory : histories) {

            response.addHistory(
                    RepaymentHistoryResponse.RepaymentHistoryDTO.builder()
                            .repaymentAmount(repaymentHistory.getRepaymentAmount())
                            .createdAt(repaymentHistory.getCreatedAt())
                            .type(RepaymentType.getKorNameByType(repaymentHistory.getType()))
                            .build()
            );
        }

        return response;
    }
}
