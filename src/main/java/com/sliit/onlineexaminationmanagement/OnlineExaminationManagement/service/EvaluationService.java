package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Evaluation;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    public Evaluation saveEvaluation(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    public Optional<Evaluation> getEvaluationById(Long id) {
        return evaluationRepository.findById(id);
    }

    public Evaluation updateEvaluation(Long id, Evaluation updatedEvaluation) {
        return evaluationRepository.findById(id).map(evaluation -> {
            evaluation.setAttemptID(updatedEvaluation.getAttemptID());
            evaluation.setMarksobtained(updatedEvaluation.getMarksobtained());
            evaluation.setFeedback(updatedEvaluation.getFeedback());
            evaluation.setEvaluatedBy(updatedEvaluation.getEvaluatedBy());
            evaluation.setEvaluatedOn(updatedEvaluation.getEvaluatedOn());
            return evaluationRepository.save(evaluation);
        }).orElseThrow(() -> new RuntimeException("Evaluation not found with id " + id));
    }

    public void deleteEvaluation(Long id) {
        evaluationRepository.deleteById(id);
    }
}