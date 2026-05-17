package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Exam;

import java.util.List;

public interface ExamService {
    Exam createAndScheduleExam(Exam exam);
    List<Exam> getAllExams();
    Exam getExamById(Long id);
    void cancelExam(Long id);

    // New Additions
    Exam updateExam(Long id, Exam updatedExam);
    void deleteExam(Long id);
    List<Exam> searchExamsByTitle(String title);
}