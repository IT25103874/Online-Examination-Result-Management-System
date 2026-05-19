package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Exam;

import java.util.List;

public interface ExamService {
    //Validates, creates, and schedules a new examination in the system.
    Exam createAndScheduleExam(Exam exam);
    List<Exam> getAllExams();
    Exam getExamById(int id);
    void cancelExam(int id);

    // New Additions
    Exam updateExam(int id, Exam updatedExam);
    void deleteExam(int id);
    List<Exam> searchExamsByTitle(String title);
}