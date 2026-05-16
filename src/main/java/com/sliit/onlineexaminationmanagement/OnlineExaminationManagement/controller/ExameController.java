package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.ExameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController // Automatically appends @ResponseBody to all mappings
@CrossOrigin(origins = "*")
@RequestMapping("/api/exams")
public class ExameController {

    private final ExameService examService;

    public ExameController(ExameService examService) {
        this.examService = examService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> enrollAndStart(
            @RequestParam Long studentId,
            @RequestParam Long examId,
            @RequestParam(defaultValue = "30") int duration) {
        return ResponseEntity.ok(examService.startExam(studentId, examId, duration));
    }

    @PostMapping("/report-cheat/{id}")
    public ResponseEntity<Void> reportCheating(@PathVariable Long id) {
        examService.flagCheating(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/submit/{id}")
    public ResponseEntity<Map<String, String>> submitAnswers(
            @PathVariable Long id,
            @RequestBody Map<String, String> answersMap) {

        // Serialize the Map block cleanly into a simple string structure
        String rawJsonString = answersMap.toString();
        String message = examService.submitAnswers(id, rawJsonString);

        return ResponseEntity.ok(Map.of("message", message));
    }
}