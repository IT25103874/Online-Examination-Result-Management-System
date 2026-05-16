package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Course;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Subject;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.CourseRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, SubjectRepository subjectRepository) {
        this.courseRepository = courseRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    @Transactional
    public Course createCourse(Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new IllegalStateException("Course code already exists: " + course.getCourseCode());
        }
        return courseRepository.save(course);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    @Override
    @Transactional
    public Subject addSubjectToCourse(Long courseId, Subject subject) {
        Course course = getCourseById(courseId);

        if (subjectRepository.existsBySubjectCode(subject.getSubjectCode())) {
            throw new IllegalStateException("Subject code already exists: " + subject.getSubjectCode());
        }

        subject.setCourse(course);
        return subjectRepository.save(subject);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }
}