package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class LoginResponse {
    private Integer userId;
    private String name;
    private String email;
    private String role;
    private String status;
}