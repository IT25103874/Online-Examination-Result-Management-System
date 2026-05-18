package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;


import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Course;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Subject;

import java.util.List;

public interface CourseService {
    Course createCourse(Course course);
    List<Course> getAllCourses();
    Course getCourseById(int id);
    Subject addSubjectToCourse(int courseId, Subject subject);
    void deleteCourse(int id);
}
