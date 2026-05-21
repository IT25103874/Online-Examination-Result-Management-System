package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Course;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Subject;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.CourseSubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final CourseSubjectRepository subjectRepository;

    @Autowired
    public SubjectServiceImpl(CourseSubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Subject createSubject(int courseId, Subject subject) {
        if (subjectRepository.existsBySubjectCode(subject.getSubjectCode())) {
            throw new RuntimeException("Subject code already exists: " + subject.getSubjectCode());
        }

        
        Course course = new Course();
        course.setId(courseId);

        subject.setCourse(course);
        return subjectRepository.save(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Subject getSubjectById(int id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Subject getSubjectByCode(String subjectCode) {
        return subjectRepository.findBySubjectCode(subjectCode)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with code: " + subjectCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> getSubjectsByCourseId(int courseId) {
        return subjectRepository.findByCourseId(courseId);
    }

    @Override
    public Subject updateSubject(int id, Subject subjectDetails) {
        Subject existingSubject = getSubjectById(id);

        
        if (!existingSubject.getSubjectCode().equals(subjectDetails.getSubjectCode()) &&
                subjectRepository.existsBySubjectCode(subjectDetails.getSubjectCode())) {
            throw new RuntimeException("Subject code already exists: " + subjectDetails.getSubjectCode());
        }

        existingSubject.setSubjectCode(subjectDetails.getSubjectCode());
        existingSubject.setName(subjectDetails.getName());

        return subjectRepository.save(existingSubject);
    }

    @Override
    public void deleteSubject(int id) {
        Subject subject = getSubjectById(id);
        subjectRepository.delete(subject);
    }
}
