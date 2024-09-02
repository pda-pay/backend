package org.ofz.repayment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PaymentHistoriesResponse {

    List<PaymentHistoryDTO> paymentHistories;

    public PaymentHistoriesResponse() {}

    public PaymentHistoriesResponse(List<PaymentHistoryDTO> paymentHistories) {
        this.paymentHistories = paymentHistories;
    }

    @Getter
    @Setter
    public static class PaymentHistoryDTO {
        private Long id;
        private int paymentAmount;
        private LocalDateTime createdAt;
        private String franchiseName;

        public PaymentHistoryDTO() {}

        public PaymentHistoryDTO(Long id, int paymentAmount, LocalDateTime createdAt, String franchiseName) {
            this.id = id;
            this.paymentAmount = paymentAmount;
            this.createdAt = createdAt;
            this.franchiseName = franchiseName;
        }
    }
}
