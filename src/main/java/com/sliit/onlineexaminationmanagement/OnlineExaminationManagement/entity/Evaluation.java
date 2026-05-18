package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int evaluationID;

    private int attemptID;
    private double marksobtained;
    private String feedback;
    private int evaluatedBy;
    private LocalDate evaluatedOn;

    public Evaluation() {
    }

    public Evaluation(int evaluationID, int attemptID, double marksobtained, String feedback, int evaluatedBy, LocalDate evaluatedOn) {
        this.evaluationID = evaluationID;
        this.attemptID = attemptID;
        this.marksobtained = marksobtained;
        this.feedback = feedback;
        this.evaluatedBy = evaluatedBy;
        this.evaluatedOn = evaluatedOn;
    }

    public int getEvaluationID() {
        return evaluationID;
    }

    public void setEvaluationID(int evaluationID) {
        this.evaluationID = evaluationID;
    }

    public int getAttemptID() {
        return attemptID;
    }

    public void setAttemptID(int attemptID) {
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

    public int getEvaluatedBy() {
        return evaluatedBy;
    }

    public void setEvaluatedBy(int evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }

    public LocalDate getEvaluatedOn() {
        return evaluatedOn;
    }

    public void setEvaluatedOn(LocalDate evaluatedOn) {
        this.evaluatedOn = evaluatedOn;
    }
}