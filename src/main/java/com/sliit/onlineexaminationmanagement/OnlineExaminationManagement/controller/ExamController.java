package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.ExamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> enrollAndStart(
            @RequestParam int studentId,
            @RequestParam int examId,
            @RequestParam(defaultValue = "30") int duration) {
        return ResponseEntity.ok(examService.startExam(studentId, examId, duration));
    }

    @PostMapping("/report-cheat/{id}")
    public ResponseEntity<Void> reportCheating(@PathVariable int id) {
        examService.flagCheating(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/submit/{id}")
    public ResponseEntity<Map<String, String>> submitAnswers(
            @PathVariable int id,
            @RequestBody Map<String, String> answersMap) {

        String rawJsonString = answersMap.toString();
        String message = examService.submitAnswers(id, rawJsonString);

        return ResponseEntity.ok(Map.of("message", message));
    }
}