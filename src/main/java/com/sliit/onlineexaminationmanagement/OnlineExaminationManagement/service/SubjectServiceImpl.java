package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Course;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Subject;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service

public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    // Assuming you have a CourseRepository to validate course existence
    // private final CourseRepository courseRepository;

    @Autowired
    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Subject createSubject(Long courseId, Subject subject) {
        if (subjectRepository.existsBySubjectCode(subject.getSubjectCode())) {
            throw new RuntimeException("Subject code already exists: " + subject.getSubjectCode());
        }

        // Mocking Course association since Course entity isn't fully provided.
        // Ideally: Course course = courseRepository.findById(courseId).orElseThrow(...)
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
    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Subject getSubjectByCode(String subjectCode) {
        return subjectRepository.findBySubjectCode(subjectCode)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with code: " + subjectCode));
    }

    @Transactional(readOnly = true)
    public List<Subject> getSubjectsByCourseId() {
        return getSubjectsByCourseId(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> getSubjectsByCourseId(Long courseId) {
        return subjectRepository.findByCourseId(courseId);
    }

    @Override
    public Subject updateSubject(Long id, Subject subjectDetails) {
        Subject existingSubject = getSubjectById(id);

        // Prevent changing to an already existing code assigned to another subject
        if (!existingSubject.getSubjectCode().equals(subjectDetails.getSubjectCode()) &&
                subjectRepository.existsBySubjectCode(subjectDetails.getSubjectCode())) {
            throw new RuntimeException("Subject code already exists: " + subjectDetails.getSubjectCode());
        }

        existingSubject.setSubjectCode(subjectDetails.getSubjectCode());
        existingSubject.setName(subjectDetails.getName());

        return subjectRepository.save(existingSubject);
    }

    @Override
    public void deleteSubject(Long id) {
        Subject subject = getSubjectById(id);
        subjectRepository.delete(subject);
    }
}