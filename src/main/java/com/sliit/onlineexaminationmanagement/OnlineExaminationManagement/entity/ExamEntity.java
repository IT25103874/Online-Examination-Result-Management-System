package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_attempt")
public class ExamEntity {

    // Unique identifier for the exam attempt.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer attemptId; // Using Integer wrapper for JPA compatibility

    private int studentId;
    private int examId;
    private LocalDateTime startTime;
    private int durationMinutes;
    private boolean submitted;
    private boolean cheated;

    @Column(columnDefinition = "TEXT")
    private String submittedAnswersJson;

    public ExamEntity() {}

    // Getters and Setters
    public Integer getAttemptId() { return attemptId; }
    public void setAttemptId(Integer attemptId) { this.attemptId = attemptId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }
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