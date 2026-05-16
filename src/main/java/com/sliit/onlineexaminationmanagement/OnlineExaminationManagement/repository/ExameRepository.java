package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.ExameEntity;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public interface ExameRepository extends JpaRepository<ExameEntity,Long> {
}