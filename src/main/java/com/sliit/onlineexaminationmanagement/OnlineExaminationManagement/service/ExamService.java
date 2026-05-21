package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Exam;

import java.util.List;

public interface ExamService {
    
    Exam createAndScheduleExam(Exam exam);
    List<Exam> getAllExams();
    Exam getExamById(int id);
    void cancelExam(int id);

   
    Exam updateExam(int id, Exam updatedExam);
    void deleteExam(int id);
    List<Exam> searchExamsByTitle(String title);
}
