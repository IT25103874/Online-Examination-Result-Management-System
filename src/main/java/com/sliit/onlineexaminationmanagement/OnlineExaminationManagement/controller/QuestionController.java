package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Question;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/add")
    public ResponseEntity<Question> create(@RequestBody Question question) {
        return ResponseEntity.ok(questionService.addQuestion(question));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Question> update(@PathVariable Integer id, @RequestBody Question question) {
        return ResponseEntity.ok(questionService.updateQuestion(id, question));
    }

    @GetMapping("/bank")
    public ResponseEntity<List<Question>> getAll() {
        return ResponseEntity.ok(questionService.getQuestionBank());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(questionService.getQuestionById(id)); // මෙය Service එකේ තිබිය යුතුය
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok("Question removed successfully!");
    }
}