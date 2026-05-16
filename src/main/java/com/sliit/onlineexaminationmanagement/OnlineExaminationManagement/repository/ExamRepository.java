package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime endTime, LocalDateTime startTime);

    // New Addition: Search by title ignoring case
    List<Exam> findByTitleContainingIgnoreCase(String title);
}
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Integer> {

    // find all exams for a specific subject
    List<Exam> findBySubject_SubjectId(Integer subjectId);
}
