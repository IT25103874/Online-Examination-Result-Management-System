package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {

    List<Exam> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime endTime, LocalDateTime startTime);

    // New Addition: Search by title ignoring case
    List<Exam> findByTitleContainingIgnoreCase(String title);
}
