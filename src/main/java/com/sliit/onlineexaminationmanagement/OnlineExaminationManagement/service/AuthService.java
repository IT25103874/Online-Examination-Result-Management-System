package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.*;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Student;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.User;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Teacher;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.TeacherRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.StudentRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final EmailService emailService;
    private final TeacherRepository teacherRepository;

    public AuthService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       EmailService emailService,
                       TeacherRepository teacherRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
        this.teacherRepository = teacherRepository;
    }

    public String registerStudent(RegisterRequest request) {

        // check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            User existing = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (existing.getStatus().equals("REJECTED")) {
                // delete old student record first
                studentRepository.findByUser_UserId(existing.getUserId())
                        .ifPresent(studentRepository::delete);
                // delete old user record
                userRepository.delete(existing);
                // now continue to re-register below

            } else {
                // PENDING, ACTIVE, INACTIVE — block re-register
                throw new RuntimeException("Email already registered");
            }
        }

        // create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword("");
        user.setRole("STUDENT");
        user.setStatus("PENDING");
        userRepository.save(user);

        // create new student
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

        // try find by email first
        if (userRepository.existsByEmail(request.getEmail())) {
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            // try find by roll number
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getStatus().equals("PENDING")) {
            throw new RuntimeException("User is not pending");
        }

        Student student = studentRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String rollNumber = generateRollNumber(student.getCourseId());
        String rawPassword = generatePassword();

        student.setRollNumber(rollNumber);
        studentRepository.save(student);

        user.setPassword(rawPassword);
        user.setStatus("ACTIVE");
        userRepository.save(user);

        emailService.sendApprovalEmail(user.getEmail(), user.getName(), rollNumber, rawPassword);

        return "Student approved successfully. Email sent to " + user.getEmail();
    }

    // ── REJECT student registration ──────────────────────────────────────────
    public String rejectStudent(Integer userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals("STUDENT")) {
            throw new RuntimeException("User is not a student");
        }
        if (!user.getStatus().equals("PENDING")) {
            throw new RuntimeException("Only PENDING students can be rejected");
        }

        user.setStatus("REJECTED");
        user.setRejectionReason(reason);
        userRepository.save(user);

        emailService.sendRejectionEmail(user.getEmail(), user.getName(), reason);

        return "Student rejected. Email sent to " + user.getEmail();
    }

    // ── DEACTIVATE / REACTIVATE user ─────────────────────────────────────────
    public String toggleUserStatus(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus().equals("ACTIVE")) {
            user.setStatus("INACTIVE");
            userRepository.save(user);
            return "User deactivated successfully.";
        } else if (user.getStatus().equals("INACTIVE")) {
            user.setStatus("ACTIVE");
            userRepository.save(user);
            return "User reactivated successfully.";
        } else {
            throw new RuntimeException("Cannot toggle status. Current status: " + user.getStatus());
        }
    }

    public String createLecturer(CreateLecturerRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String rawPassword = generatePassword();

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(rawPassword);
        user.setRole("LECTURER");
        user.setStatus("ACTIVE");
        userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setPhone(request.getPhone());
        teacher.setDepartment(request.getDepartment());
        teacher.setQualification(request.getQualification());
        teacher.setUser(user);
        teacherRepository.save(teacher);

        emailService.sendLecturerCredentials(request.getEmail(), request.getName(), rawPassword);

        return "Lecturer created successfully. Email sent to " + request.getEmail();
    }

    public String updateProfile(Integer userId, UpdateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        userRepository.save(user);

        if (user.getRole().equals("STUDENT")) {
            studentRepository.findByUser_UserId(userId).ifPresent(student -> {
                student.setPhone(request.getPhone());
                studentRepository.save(student);
            });
        } else if (user.getRole().equals("LECTURER")) {
            teacherRepository.findByUser_UserId(userId).ifPresent(teacher -> {
                teacher.setPhone(request.getPhone());
                teacherRepository.save(teacher);
            });
        }

        return "Profile updated successfully.";
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