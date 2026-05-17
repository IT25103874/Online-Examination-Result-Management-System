package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    long countByCourseId(String courseId);
    Optional<Student> findByUser_UserId(Integer userId);
    Optional<Student> findByRollNumber(String rollNumber);

    // ADD THIS — find students by courseId contains IT or BM
    // used for exam notification
    List<Student> findByCourseIdContainingIgnoreCase(String courseId);
}