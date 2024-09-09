package org.ofz.repayment;

public enum RepaymentType {
    CASH("현금"),
    PAWN("담보"),
    PRE_CASH("현금선상환"),
    PRE_PAWN("담보선상환"),
    OFFSET("반대매매");

    public String kor;

    RepaymentType(String kor) {
        this.kor = kor;
    }
}
