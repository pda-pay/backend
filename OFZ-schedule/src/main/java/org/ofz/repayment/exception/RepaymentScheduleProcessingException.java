package org.ofz.repayment.exception;

public class RepaymentScheduleProcessingException extends RuntimeException {
    public RepaymentScheduleProcessingException(String message) {
        super(message);
    }

    public RepaymentScheduleProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
