package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.SendNotificationRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Notification;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notify")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final NotificationService notificationService;

    // send to ALL students (IT + BM)
    @PostMapping("/all")
    public ResponseEntity<String> sendToAll(
            @RequestBody SendNotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendToAll(request));
    }

    // send to specific course — IT or BM
    @PostMapping("/course/{courseId}")
    public ResponseEntity<String> sendToCourse(
            @PathVariable String courseId,
            @RequestBody SendNotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendToCourse(courseId, request));
    }

    // send to specific user — exam reminder / schedule
    @PostMapping("/user/{userId}")
    public ResponseEntity<String> sendToUser(
            @PathVariable Integer userId,
            @RequestBody SendNotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendToUser(userId, request));
    }

    // admin views all sent notifications
    @GetMapping("/all-sent")
    public ResponseEntity<List<Notification>> getAllSentNotifications() {
        return ResponseEntity.ok(notificationService.getAllSentNotifications());
    }

    // admin deletes a notification — removes for everyone
    @DeleteMapping("/delete/{notificationId}")
    public ResponseEntity<String> deleteNotification(
            @PathVariable Integer notificationId) {
        return ResponseEntity.ok(notificationService.deleteNotification(notificationId));
    }
}