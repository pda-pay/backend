package org.ofz.repayment.service.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateChecker {

    public static boolean isWeekend() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}
