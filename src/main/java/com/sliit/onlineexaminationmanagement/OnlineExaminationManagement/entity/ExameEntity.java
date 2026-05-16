package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_attempt")
public class ExameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    private Long studentId;
    private Long examId;
    private LocalDateTime startTime;
    private int durationMinutes;
    private boolean submitted;
    private boolean cheated;

    @Column(columnDefinition = "TEXT") // Allows storing heavy payload strings
    private String submittedAnswersJson;

    public ExameEntity() {}

    // Getters and Setters
    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public boolean isSubmitted() { return submitted; }
    public void setSubmitted(boolean submitted) { this.submitted = submitted; }
    public boolean isCheated() { return cheated; }
    public void setCheated(boolean cheated) { this.cheated = cheated; }
    public String getSubmittedAnswersJson() { return submittedAnswersJson; }
    public void setSubmittedAnswersJson(String submittedAnswersJson) { this.submittedAnswersJson = submittedAnswersJson; }
}