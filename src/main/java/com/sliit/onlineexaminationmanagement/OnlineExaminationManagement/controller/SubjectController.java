package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Subject;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    // CREATE: Add a subject to a specific course
    @PostMapping("/course/{courseId}")
    public ResponseEntity<Subject> createSubject(@PathVariable Long courseId, @RequestBody Subject subject) {
        Subject createdSubject = subjectService.createSubject(courseId, subject);
        return new ResponseEntity<>(createdSubject, HttpStatus.CREATED);
    }

    // READ: Get all subjects
    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    // READ: Get subject by ID
    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    // READ: Get subject by Subject Code
    @GetMapping("/code/{subjectCode}")
    public ResponseEntity<Subject> getSubjectByCode(@PathVariable String subjectCode) {
        return ResponseEntity.ok(subjectService.getSubjectByCode(subjectCode));
    }

    // READ: Get all subjects belonging to a specific course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Subject>> getSubjectsByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(subjectService.getSubjectsByCourseId(courseId));
    }

    // UPDATE: Update an existing subject
    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @RequestBody Subject subjectDetails) {
        return ResponseEntity.ok(subjectService.updateSubject(id, subjectDetails));
    }

    // DELETE: Delete a subject
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok("Subject deleted successfully.");
    }
}
