package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.LoginRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.RegisterRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.UpdateProfileRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String result = authService.registerStudent(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginUser(request));
    }

    @PutMapping("/update-profile/{userId}")
    public ResponseEntity<String> updateProfile(
            @org.springframework.web.bind.annotation.PathVariable Integer userId,
            @RequestBody UpdateProfileRequest request
    ) {

        String result = authService.updateProfile(userId, request);

        return ResponseEntity.ok(result);
    }
}
