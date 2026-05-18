package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.LegacySubject;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubjectController {

    private final SubjectRepository subjectRepository;

    @PostMapping("/add")
    public ResponseEntity<LegacySubject> addSubject(@RequestBody LegacySubject subject) {

        return ResponseEntity.ok(subjectRepository.save(subject));
    }


    @GetMapping("/all")
    public ResponseEntity<List<LegacySubject>> getAllSubjects() {
        return ResponseEntity.ok(subjectRepository.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<LegacySubject> getSubjectById(@PathVariable Integer id) {
        return subjectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
