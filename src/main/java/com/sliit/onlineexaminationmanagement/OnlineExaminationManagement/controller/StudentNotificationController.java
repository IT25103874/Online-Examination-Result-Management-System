package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.NotificationResponse;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/notifications")
@RequiredArgsConstructor
public class StudentNotificationController {

    private final NotificationService notificationService;

    // student views all their notifications
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(notificationService.getMyNotifications(userId));
    }

    // student views only unread notifications
    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    // count unread notifications
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Long> countUnread(@PathVariable Integer userId) {
        return ResponseEntity.ok(notificationService.countUnread(userId));
    }

    // mark notification as read
    @PutMapping("/read/{recipientId}")
    public ResponseEntity<String> markAsRead(@PathVariable Integer recipientId) {
        return ResponseEntity.ok(notificationService.markAsRead(recipientId));
    }

    // mark notification as unread
    @PutMapping("/unread/{recipientId}")
    public ResponseEntity<String> markAsUnread(@PathVariable Integer recipientId) {
        return ResponseEntity.ok(notificationService.markAsUnread(recipientId));
    }

    // full notification history
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<NotificationResponse>> getHistory(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(notificationService.getHistory(userId));
    }

    // student deletes from their view only — others not affected
    @DeleteMapping("/delete/{recipientId}")
    public ResponseEntity<String> deleteMyNotification(
            @PathVariable Integer recipientId) {
        return ResponseEntity.ok(notificationService.deleteMyNotification(recipientId));
    }
}