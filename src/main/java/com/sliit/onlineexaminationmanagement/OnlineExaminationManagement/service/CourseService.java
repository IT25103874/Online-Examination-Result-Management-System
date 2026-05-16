package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;


import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Course;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Subject;

import java.util.List;

public interface CourseService {
    Course createCourse(Course course);
    List<Course> getAllCourses();
    Course getCourseById(Long id);
    Subject addSubjectToCourse(Long courseId, Subject subject);
    void deleteCourse(Long id);
}
