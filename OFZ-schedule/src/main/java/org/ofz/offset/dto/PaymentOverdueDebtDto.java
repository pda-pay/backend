package org.ofz.offset.dto;

import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class PaymentOverdueDebtDto {
    private int previousMonthDebt;
    private LocalDate overdueDay;
}
