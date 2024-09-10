package org.ofz.repayment;

import java.time.LocalDateTime;

public interface AmountAndDateAndTypeProjection {

    int getRepaymentAmount();
    LocalDateTime getCreatedAt();
    String getType();
}
