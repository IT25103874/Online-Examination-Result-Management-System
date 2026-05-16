package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_notification")
@Data @NoArgsConstructor @AllArgsConstructor
public class ExamNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // links to exam_schedule
    @ManyToOne
    @JoinColumn(name = "exam_schedule_id")
    private ExamSchedule examSchedule;

    // links to notifications
    @ManyToOne
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "course_id", length = 30)
    private String courseId;
}