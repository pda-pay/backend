package org.ofz.repayment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class MonthlyDebtResponse {

    private int repaymentDate;
    private int previousMonthDebt;
    private int currentMonthDebt;

    public MonthlyDebtResponse(int repaymentDate, int previousMonthDebt, int currentMonthDebt) {
        this.repaymentDate = repaymentDate;
        this.previousMonthDebt = previousMonthDebt;
        this.currentMonthDebt = currentMonthDebt;
    }
}
