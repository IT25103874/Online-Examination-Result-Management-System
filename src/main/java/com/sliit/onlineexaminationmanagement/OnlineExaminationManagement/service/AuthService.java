package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.*;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Student;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.User;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.StudentRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
    }

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

    public LoginResponse loginUser(LoginRequest request) {
        User user;

        if (userRepository.existsByEmail(request.getEmail())) {
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            Student student = studentRepository.findByRollNumber(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user = student.getUser();
        }

        if (!user.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Account is not active yet");
        }

        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return new LoginResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }

    public String approveStudent(Integer userId) {
        // find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getStatus().equals("PENDING")) {
            throw new RuntimeException("User is not pending");
        }

        // find student
        Student student = studentRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // generate roll number based on course
        String rollNumber = generateRollNumber(student.getCourseId());

        // generate random password
        String rawPassword = generatePassword();

        // update student
        student.setRollNumber(rollNumber);
        studentRepository.save(student);

        // update user
        user.setPassword(rawPassword);
        user.setStatus("ACTIVE");
        userRepository.save(user);

        // send email
        emailService.sendApprovalEmail(user.getEmail(), user.getName(), rollNumber, rawPassword);

        return "Student approved successfully. Email sent to " + user.getEmail();
    }

    private String generateRollNumber(String courseId) {
        String year = String.valueOf(java.time.Year.now().getValue()).substring(2);
        String prefix = courseId.toUpperCase().startsWith("IT") ? "IT" : "BM";
        long count = studentRepository.countByCourseId(courseId) + 1;
        String sequence = String.format("%04d", count);
        return prefix + year + sequence;
    }

    private String generatePassword() {
        return java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}