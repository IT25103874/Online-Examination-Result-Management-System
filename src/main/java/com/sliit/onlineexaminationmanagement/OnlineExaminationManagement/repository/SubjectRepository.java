package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.LegacySubject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<LegacySubject, Integer> {

    
    java.util.List<LegacySubject> findByCourseCourseId(Integer courseCourseId);
}
