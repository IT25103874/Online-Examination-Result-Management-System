package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Subject;

import java.util.List;

public interface SubjectService {
    Subject createSubject(Long courseId, Subject subject);
    List<Subject> getAllSubjects();
    Subject getSubjectById(Long id);
    Subject getSubjectByCode(String subjectCode);
    List<Subject> getSubjectsByCourseId(Long courseId);
    Subject updateSubject(Long id, Subject subjectDetails);
    void deleteSubject(Long id);
}
