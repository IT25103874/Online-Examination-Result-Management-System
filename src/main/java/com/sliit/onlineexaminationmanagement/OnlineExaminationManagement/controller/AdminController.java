package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.User;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.UserRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final UserRepository userRepository;

    // get all pending students
    @GetMapping("/pending-students")
    public ResponseEntity<List<User>> getPendingStudents() {
        List<User> pending = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole().equals("STUDENT") && u.getStatus().equals("PENDING"))
                .toList();
        return ResponseEntity.ok(pending);
    }

    // approve a student
    @PutMapping("/approve/{userId}")
    public ResponseEntity<String> approveStudent(@PathVariable Integer userId) {
        return ResponseEntity.ok(authService.approveStudent(userId));
    }
}
