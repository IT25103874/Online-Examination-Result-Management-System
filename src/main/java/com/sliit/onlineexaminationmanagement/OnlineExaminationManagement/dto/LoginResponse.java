package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class LoginResponse {
    private Integer userId;
    private String name;
    private String email;
    private String role;
    private String status;
    private String phone;
    private String courseId;
    private String dateOfBirth;
    private String rollNumber;
}