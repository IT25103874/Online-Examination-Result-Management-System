package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Result;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    public Result saveResult(Result result) {
        return resultRepository.save(result);
    }

    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }

    public Optional<Result> getResultById(Long id) {
        return resultRepository.findById(id);
    }

    public Result updateResult(Long id, Result updatedResult) {
        return resultRepository.findById(id).map(result -> {
            // These methods MUST match the Result class exactly
            result.setStudentID(updatedResult.getStudentID());
            result.setExamID(updatedResult.getExamID());
            result.setTotalMarks(updatedResult.getTotalMarks());
            result.setGrade(updatedResult.getGrade());
            return resultRepository.save(result);
        }).orElseThrow(() -> new RuntimeException("Result not found with id " + id));
    }

    public void deleteResult(Long id) {
        resultRepository.deleteById(id);
    }
}