package org.ofz.notificationBox.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class NotificationDeleteReq {
    private List<Long> ids;
}
