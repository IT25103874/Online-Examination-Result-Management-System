package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamModelRepository extends JpaRepository<Exam, Integer> {
}
