package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Integer> {

    // student views all their notifications — newest first
    List<NotificationRecipient> findByUser_UserIdOrderByNotification_CreatedAtDesc(Integer userId);

    // student views only unread
    List<NotificationRecipient> findByUser_UserIdAndIsReadOrderByNotification_CreatedAtDesc(
            Integer userId, Boolean isRead);

    // needed for admin delete — find all recipients of a notification
    List<NotificationRecipient> findByNotification_NotificationId(Integer notificationId);

    // count unread for a student
    long countByUser_UserIdAndIsRead(Integer userId, Boolean isRead);
}