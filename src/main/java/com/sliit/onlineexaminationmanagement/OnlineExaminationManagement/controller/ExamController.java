package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Exam;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*")
public class ExamController {

    private final ExamService examService;

    @Autowired
    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<Exam> scheduleExam(@RequestBody Exam exam) {
        Exam createdExam = examService.createAndScheduleExam(exam);
        return new ResponseEntity<>(createdExam, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examService.getAllExams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable int id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelExam(@PathVariable int id) {
        examService.cancelExam(id);
        return ResponseEntity.ok("Exam cancelled successfully.");
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable int id, @RequestBody Exam exam) {
        Exam updated = examService.updateExam(id, exam);
        return ResponseEntity.ok(updated);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExam(@PathVariable int id) {
        examService.deleteExam(id);
        return ResponseEntity.ok("Exam deleted successfully from the system.");
    }

    
    @GetMapping("/search")
    public ResponseEntity<List<Exam>> searchExams(@RequestParam String title) {
        List<Exam> results = examService.searchExamsByTitle(title);
        return ResponseEntity.ok(results);
    }
}
