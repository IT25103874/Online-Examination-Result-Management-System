package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Result;
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

    public Optional<Result> getResultById(int id) {
        return resultRepository.findById(id);
    }

    public Result updateResult(int id, Result updatedResult) {
        return resultRepository.findById(id).map(result -> {
            // These methods MUST match the Result class exactly
            result.setStudentID(updatedResult.getStudentID());
            result.setExamID(updatedResult.getExamID());
            result.setTotalMarks(updatedResult.getTotalMarks());
            result.setGrade(updatedResult.getGrade());
            return resultRepository.save(result);
        }).orElseThrow(() -> new RuntimeException("Result not found with id " + id));
    }

    public void deleteResult(int id) {
        resultRepository.deleteById(id);
    }
}