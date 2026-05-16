package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "exam_schedule")
@Data @NoArgsConstructor @AllArgsConstructor
public class ExamSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;

    @Column(name = "start_time", length = 45)
    private String startTime;

    @Column(name = "end_time", length = 45)
    private String endTime;

    @Column(name = "venue/mode", length = 45)
    private String venueMode;

    @ManyToOne
    @JoinColumn(name = "exam_exam_id")
    private Exam exam;
}