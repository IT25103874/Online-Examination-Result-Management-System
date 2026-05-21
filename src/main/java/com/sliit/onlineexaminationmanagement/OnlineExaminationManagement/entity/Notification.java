package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data @NoArgsConstructor @AllArgsConstructor
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "message", length = 1000)
    private String message;

    // GENERAL, COURSE, PERSONAL
    @Column(name = "type", length = 50)
    private String type;

    // ALL, IT, BM, or specific userId as string
    @Column(name = "target", length = 100)
    private String target;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // which admin sent it
    @Column(name = "sent_by")
    private Integer sentBy;
}