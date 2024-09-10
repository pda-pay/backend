package org.ofz.rabbitMQ;

public enum NotificationPage {

    MAIN("메인"),
    PAYMENT("결제"),
    ASSET("자산"),
    ALL_MENU("전체");

    public String kor;

    NotificationPage(String kor) {
        this.kor = kor;
    }
}
