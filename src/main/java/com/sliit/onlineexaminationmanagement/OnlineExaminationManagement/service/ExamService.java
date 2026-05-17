package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import org.springframework.stereotype.Service;

@Service
public class ExamService {

    public String startExam(int studentId, int examId, int duration) {
        // This is a placeholder logic for now
        return "Exam started successfully for student " + studentId;
    }

    public void flagCheating(int id) {
        // This is a placeholder logic for now
        System.out.println("Student " + id + " has been flagged for cheating.");
    }

    public String submitAnswers(int id, String answersJson) {
        // This is a placeholder logic for now
        return "Answers submitted successfully for exam attempt " + id;
    }
}