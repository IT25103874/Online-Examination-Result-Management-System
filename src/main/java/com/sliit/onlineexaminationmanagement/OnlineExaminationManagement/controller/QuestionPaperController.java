package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.PaperRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.QuestionPaper;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.QuestionPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
public class QuestionPaperController {

    private final QuestionPaperService paperService;

    @PostMapping("/build")
    public ResponseEntity<QuestionPaper> buildPaper(@RequestBody PaperRequest request) {
        return ResponseEntity.ok(paperService.generateAutoPaper(
                request.getSubjectId(),
                request.getCount(),
                request.getDifficulty(),
                request.getPaperName(),
                request.getInstructions()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionPaper> getPaperById(@PathVariable int id) {
        // මෙහි 'questionPaperService' වෙනුවට 'paperService' භාවිතා කරන්න
        QuestionPaper paper = paperService.getPaperById(id);
        if (paper != null) {
            return ResponseEntity.ok(paper);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}