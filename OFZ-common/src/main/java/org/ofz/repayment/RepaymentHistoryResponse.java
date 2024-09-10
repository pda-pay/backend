package org.ofz.repayment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentHistoryResponse {

    private List<RepaymentHistoryDTO> repaymentHistories = new ArrayList<>();

    public void addHistory(RepaymentHistoryDTO dto) {
        repaymentHistories.add(dto);
    }

    @Getter
    @NoArgsConstructor
    public static class RepaymentHistoryDTO {
        private int repaymentAmount;
        private LocalDateTime createdAt;
        private String type;

        @Builder
        public RepaymentHistoryDTO(int repaymentAmount, LocalDateTime createdAt, RepaymentType type) {
            this.repaymentAmount = repaymentAmount;
            this.createdAt = createdAt;
            this.type = type.kor;
        }
    }
}
