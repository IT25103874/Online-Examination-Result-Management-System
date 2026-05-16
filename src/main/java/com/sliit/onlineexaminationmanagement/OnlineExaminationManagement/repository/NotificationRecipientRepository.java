package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRecipientRepository
        extends JpaRepository<NotificationRecipient, Integer> {

    // student views all their notifications — newest first
    List<NotificationRecipient>
    findByUser_UserIdOrderByNotification_CreatedAtDesc(
            Integer userId
    );

    // student views only unread
    List<NotificationRecipient>
    findByUser_UserIdAndIsReadOrderByNotification_CreatedAtDesc(
            Integer userId,
            Boolean isRead
    );

    // needed for admin delete — find all recipients
    List<NotificationRecipient>
    findByNotification_NotificationId(
            Integer notificationId
    );

    // FIXED DELETE QUERY
    @Modifying
    @Transactional
    @Query("DELETE FROM NotificationRecipient r WHERE r.notification.notificationId = :notificationId")
    void deleteByNotificationId(
            @Param("notificationId")
            Integer notificationId
    );

    // count unread
    long countByUser_UserIdAndIsRead(
            Integer userId,
            Boolean isRead
    );
}