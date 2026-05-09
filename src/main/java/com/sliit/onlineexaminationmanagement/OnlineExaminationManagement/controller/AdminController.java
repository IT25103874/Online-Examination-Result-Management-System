package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.CreateLecturerRequest;
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

    @PostMapping("/create-lecturer")
    public ResponseEntity<String> createLecturer(@RequestBody CreateLecturerRequest request) {
        return ResponseEntity.ok(authService.createLecturer(request));
    }

    // get all users
    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // delete user by id
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }
}
