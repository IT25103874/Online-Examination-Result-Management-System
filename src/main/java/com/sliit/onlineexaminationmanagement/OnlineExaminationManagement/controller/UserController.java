package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.UpdateProfileRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.UpdateProfileRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @PutMapping("/update-profile/{userId}")
    public <UpdateProfileRequest> ResponseEntity<String> updateProfile(
            @PathVariable Integer userId,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(userId, (com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.UpdateProfileRequest) request));
    }
}