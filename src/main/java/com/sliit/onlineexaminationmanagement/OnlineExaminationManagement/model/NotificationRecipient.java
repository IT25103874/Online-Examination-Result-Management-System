package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_recipients")
@Data @NoArgsConstructor @AllArgsConstructor
public class NotificationRecipient {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipient_id")
    private Integer recipientId;

    // link to notification
    @ManyToOne
    @JoinColumn(name = "notification_id")
    private Notification notification;

    // link to user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}