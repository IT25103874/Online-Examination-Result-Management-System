package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String courseId;
    private LocalDate dateOfBirth;
    private String phone;
}
