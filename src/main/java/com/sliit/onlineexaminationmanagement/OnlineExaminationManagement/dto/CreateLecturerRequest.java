package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreateLecturerRequest {
    private String name;
    private String email;
    private String phone;
    private String department;
    private String qualification;
}