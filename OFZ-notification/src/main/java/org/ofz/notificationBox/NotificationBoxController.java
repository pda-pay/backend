package org.ofz.notificationBox;

import lombok.RequiredArgsConstructor;
import org.ofz.notificationBox.dto.NotificationDeleteReq;
import org.ofz.notificationBox.dto.NotificationDto;
import org.ofz.notificationBox.dto.NotificationRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationBoxController {
    private final NotificationBoxService notificationBoxService;

    @GetMapping("/notification/{userId}")
    public ResponseEntity<NotificationRes> getNotifications(@PathVariable(value = "userId") Long userId) {
        List<NotificationDto> notifications = notificationBoxService.getNotifications(userId);
        NotificationRes response = new NotificationRes(notifications);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/notification")
    public ResponseEntity<Void> deleteNotifications(@RequestBody NotificationDeleteReq notificationDeleteReq){
        notificationBoxService.deleteNotificationsByIds(notificationDeleteReq.getIds());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
