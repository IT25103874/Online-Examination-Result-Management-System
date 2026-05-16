package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Subject;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Frontend එක සමඟ සම්බන්ධ වීමට පහසු වේ
public class SubjectController {

    private final SubjectRepository subjectRepository;

    @PostMapping("/add")
    public ResponseEntity<Subject> addSubject(@RequestBody Subject subject) {
        // repository.save(subject) පසු වරහන වැසීමට අමතක කරන්න එපා
        return ResponseEntity.ok(subjectRepository.save(subject));
    }

    // 2. සියලුම විෂයන් ලබා ගැනීම
    @GetMapping("/all")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectRepository.findAll());
    }

    // 3. එක් විෂයයක් ID එක අනුව ලබා ගැනීම
    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Integer id) {
        return subjectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}