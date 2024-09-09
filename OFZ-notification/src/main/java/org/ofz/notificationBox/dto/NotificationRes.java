package org.ofz.notificationBox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NotificationRes {
    private List<NotificationDto> messages;
}
