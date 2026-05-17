package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sliit.onlineexaminationmanagement.entity.ExamEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<ExamEntity, Integer> {
    // The second generic type is changed to Integer to match the entity ID type
}