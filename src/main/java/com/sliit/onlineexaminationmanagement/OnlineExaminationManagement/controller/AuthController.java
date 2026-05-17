package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.LoginRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.RegisterRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
