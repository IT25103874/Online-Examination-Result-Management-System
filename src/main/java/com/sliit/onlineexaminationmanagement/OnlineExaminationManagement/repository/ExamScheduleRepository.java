package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Integer> {

    // find all schedules for a specific exam
    List<ExamSchedule> findByExam_ExamId(Integer examId);
}