package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.*;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Student;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.User;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.StudentRepository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public String registerStudent(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword("");
        user.setRole("STUDENT");
        user.setStatus("PENDING");
        userRepository.save(user);

        Student student = new Student();
        student.setCourseId(request.getCourseId());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setPhone(request.getPhone());
        student.setUser(user);
        studentRepository.save(student);

        return "Registration submitted successfully.";
    }

}