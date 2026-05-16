package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationID;

    private Long attemptID;
    private double marksobtained;
    private String feedback;
    private Long evaluatedBy;
    private LocalDate evaluatedOn;

    public Evaluation() {
    }

    public Evaluation(Long evaluationID, Long attemptID, double marksobtained, String feedback, Long evaluatedBy, LocalDate evaluatedOn) {
        this.evaluationID = evaluationID;
        this.attemptID = attemptID;
        this.marksobtained = marksobtained;
        this.feedback = feedback;
        this.evaluatedBy = evaluatedBy;
        this.evaluatedOn = evaluatedOn;
    }

    public Long getEvaluationID() {
        return evaluationID;
    }

    public void setEvaluationID(Long evaluationID) {
        this.evaluationID = evaluationID;
    }

    public Long getAttemptID() {
        return attemptID;
    }

    public void setAttemptID(Long attemptID) {
        this.attemptID = attemptID;
    }

    public double getMarksobtained() {
        return marksobtained;
    }

    public void setMarksobtained(double marksobtained) {
        this.marksobtained = marksobtained;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Long getEvaluatedBy() {
        return evaluatedBy;
    }

    public void setEvaluatedBy(Long evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }

    public LocalDate getEvaluatedOn() {
        return evaluatedOn;
    }

    public void setEvaluatedOn(LocalDate evaluatedOn) {
        this.evaluatedOn = evaluatedOn;
    }
}