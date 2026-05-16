package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.ExameEntity;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.ExameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ExameService {
    private final ExameRepository repository;

    public ExameService(ExameRepository repository) {
        this.repository = repository;
    }

    // 1. Student Exam Enrollment
    @Transactional
    public String startExam(Long studentId, Long examId, int duration) {
        ExameEntity attempt = new ExameEntity();
        attempt.setStudentId(studentId);
        attempt.setExamId(examId);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setDurationMinutes(duration);
        attempt.setCheated(false);
        attempt.setSubmitted(false);

        ExameEntity savedAttempt = repository.save(attempt);
        return "Exam session verified. Attempt ID: " + savedAttempt.getAttemptId();
    }

    // 5. Prevent Cheating Flagging API
    @Transactional
    public void flagCheating(Long attemptId) {
        repository.findById(attemptId).ifPresent(attempt -> {
            attempt.setCheated(true);
            repository.save(attempt);
        });
    }

    // 3 & 4. Submit Answers & Timer Validity Check Verification
    @Transactional
    public String submitAnswers(Long attemptId, String answersPayloadJson) {
        ExameEntity attempt = repository.findById(attemptId).orElse(null);
        if (attempt == null) return "Session error: Attempt matching ID not found.";
        if (attempt.isSubmitted()) return "Rejected: Submission is already locked.";

        attempt.setSubmittedAnswersJson(answersPayloadJson);
        attempt.setSubmitted(true);

        // Timer Functionality fallback evaluation limit
        LocalDateTime deadline = attempt.getStartTime().plusMinutes(attempt.getDurationMinutes());
        if (LocalDateTime.now().isAfter(deadline)) {
            repository.save(attempt);
            return "Time Expired. Assignment marked as Overdue/Late.";
        }

        repository.save(attempt);
        return attempt.isCheated()
                ? "Completed with Cheating Flag recorded."
                : "Success! Verified within valid parameters.";
    }

    public Optional<ExameEntity> findById(Long id) {
        return repository.findById(id);
    }
}