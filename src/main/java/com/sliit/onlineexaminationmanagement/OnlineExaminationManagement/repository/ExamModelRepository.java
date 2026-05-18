package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.LegacyExam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamModelRepository extends JpaRepository<LegacyExam, Integer> {
}
