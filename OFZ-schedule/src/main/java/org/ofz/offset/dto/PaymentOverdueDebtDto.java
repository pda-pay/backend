package org.ofz.offset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PaymentOverdueDebtDto {
    private int previousMonthDebt;
    private LocalDate overdueDay;
}
