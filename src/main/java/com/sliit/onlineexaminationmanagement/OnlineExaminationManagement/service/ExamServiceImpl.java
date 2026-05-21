package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Exam;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.ExamStatus;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;

    @Autowired
    public ExamServiceImpl(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Exam createAndScheduleExam(Exam exam) {
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule an exam in the past.");
        }

        if (exam.getEndTime().isBefore(exam.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        List<Exam> conflictingExams = examRepository.findByStartTimeBeforeAndEndTimeAfter(
                exam.getEndTime(), exam.getStartTime()
        );

        if (!conflictingExams.isEmpty()) {
            throw new IllegalStateException("An exam is already scheduled within this time slot.");
        }

        return examRepository.save(exam);
    }

    @Override
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @Override
    public Exam getExamById(int id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found with id: " + id));
    }

    @Override
    public void cancelExam(int id) {
        Exam exam = getExamById(id);
        exam.setStatus(ExamStatus.CANCELLED);
        examRepository.save(exam);
    }

    // New Addition: Update Functionality
    @Override
    public Exam updateExam(int id, Exam updatedExam) {
        Exam existingExam = getExamById(id);

        // Simple validation checks for the new timeline
        if (updatedExam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule an exam in the past.");
        }
        if (updatedExam.getEndTime().isBefore(updatedExam.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        // Overlap validation (excluding the current exam itself)
        List<Exam> conflictingExams = examRepository.findByStartTimeBeforeAndEndTimeAfter(
                updatedExam.getEndTime(), updatedExam.getStartTime()
        );
        boolean hasConflict = conflictingExams.stream().anyMatch(e -> e.getId() != id);
        if (hasConflict) {
            throw new IllegalStateException("An exam is already scheduled within this time slot.");
        }

        // Map updated fields
        existingExam.setTitle(updatedExam.getTitle());
        existingExam.setDescription(updatedExam.getDescription());
        existingExam.setDurationInMinutes(updatedExam.getDurationInMinutes());
        existingExam.setStartTime(updatedExam.getStartTime());
        existingExam.setEndTime(updatedExam.getEndTime());
        existingExam.setPasscode(updatedExam.getPasscode());
        existingExam.setStatus(updatedExam.getStatus());

        return examRepository.save(existingExam);
    }

    // New Addition: Delete Functionality
    @Override
    public void deleteExam(int id) {
        Exam exam = getExamById(id);
        examRepository.delete(exam);
    }

    // New Addition: Search Functionality
    @Override
    public List<Exam> searchExamsByTitle(String title) {
        return examRepository.findByTitleContainingIgnoreCase(title);
    }
}
