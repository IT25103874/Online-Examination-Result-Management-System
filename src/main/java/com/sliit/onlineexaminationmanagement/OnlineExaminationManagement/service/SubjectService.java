package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Subject;

import java.util.List;

public interface SubjectService {
    Subject createSubject(int courseId, Subject subject);
    List<Subject> getAllSubjects();
    Subject getSubjectById(int id);
    Subject getSubjectByCode(String subjectCode);
    List<Subject> getSubjectsByCourseId(int courseId);
    Subject updateSubject(int id, Subject subjectDetails);
    void deleteSubject(int id);
}
