package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.CreateLecturerRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.LoginRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.LoginResponse;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.UpdateProfileRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.*;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Student;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.User;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Teacher;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.TeacherRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.StudentRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.UserRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.exception.DuplicateEmailException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    // ── REGISTER ──────────────────────────────────────────────────────────────
    public String registerStudent(com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO.RegisterRequest request) {

        // 1. Strict Inputs Validation
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Please provide a valid email address.");
        }
        if (request.getCourseId() == null || request.getCourseId().trim().isEmpty()) {
            throw new IllegalArgumentException("Course selection is required.");
        }
        if (request.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Date of birth is required.");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number is required.");
        }

        // 2. Database Constraint Sanitization (Strip non-digits and cap length at 10 to fit in DB)
        String sanitizedPhone = request.getPhone().replaceAll("\\D", "");
        if (sanitizedPhone.length() > 10) {
            sanitizedPhone = sanitizedPhone.substring(sanitizedPhone.length() - 10);
        }
        if (sanitizedPhone.length() < 9) {
            throw new IllegalArgumentException("Please enter a valid phone number (at least 9-10 digits).");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // 3. Resilient Duplicate Email Verification
        if (userRepository.existsByEmail(normalizedEmail)) {
            User existing = userRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if ("REJECTED".equals(existing.getStatus())) {
                studentRepository.findByUser_UserId(existing.getUserId())
                        .ifPresent(studentRepository::delete);
                userRepository.delete(existing);
            } else {
                throw new DuplicateEmailException("The email address '" + request.getEmail() + "' is already registered.");
            }
        }

        // 4. Persistence
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(""); // Encrypted/assigned later upon admin approval
        user.setRole("STUDENT");
        user.setStatus("PENDING");
        userRepository.save(user);

        Student student = new Student();
        student.setCourseId(request.getCourseId().trim());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setPhone(sanitizedPhone);
        student.setUser(user);
        studentRepository.save(student);

        return "Registration submitted successfully.";
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    public LoginResponse loginUser(LoginRequest request) {

        User user;
        Student student = null;

        if (userRepository.existsByEmail(request.getEmail())) {

            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if(user.getRole().equals("STUDENT")) {

                student = studentRepository
                        .findByUser_UserId(user.getUserId())
                        .orElse(null);
            }

        } else {

            student = studentRepository.findByRollNumber(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user = student.getUser();
        }

        if ("PENDING".equals(user.getStatus())) {
            throw new IllegalArgumentException("Your account is pending approval.");
        }
        if ("REJECTED".equals(user.getStatus())) {
            throw new IllegalArgumentException("Your registration was rejected. Contact admin.");
        }
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new IllegalArgumentException("Account is not active. Contact admin.");
        }

        if (!request.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // CHANGE 1: track last login time — used for 7-day never-logged-in check
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return new LoginResponse(

                user.getUserId(),

                user.getName(),

                user.getEmail(),

                user.getRole(),

                user.getStatus(),

                student != null ? student.getPhone() : null,

                student != null ? student.getCourseId() : null,

                student != null && student.getDateOfBirth() != null
                        ? student.getDateOfBirth().toString()
                        : null,

                student != null ? student.getRollNumber() : null
        );
    }

    // ── APPROVE ───────────────────────────────────────────────────────────────
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
        // CHANGE 2: save when credentials were sent — starts 7-day clock
        user.setCredentialsSentAt(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendApprovalEmail(user.getEmail(), user.getName(), rollNumber, rawPassword);

        return "Student approved successfully. Email sent to " + user.getEmail();
    }

    // ── REJECT ────────────────────────────────────────────────────────────────
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

    // ── RULE 1: admin views users who never logged in after 7 days ────────────
    public List<User> getNeverLoggedInUsers() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return userRepository
                .findByStatusAndLastLoginAtIsNullAndCredentialsSentAtBefore(
                        "ACTIVE", sevenDaysAgo);
    }

    // ── RULE 2a: admin manually DEACTIVATES — reason required ─────────────────
    public String deactivateUser(Integer userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if ("ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("Protected Account: Administrative accounts cannot be deactivated.");
        }

        if (!user.getStatus().equals("ACTIVE")) {
            throw new IllegalArgumentException("User is not ACTIVE. Current status: " + user.getStatus());
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required when deactivating");
        }

        user.setStatus("INACTIVE");
        user.setDeactivationReason(reason);
        userRepository.save(user);

        emailService.sendDeactivationEmail(user.getEmail(), user.getName(), reason);

        return "User deactivated. Email sent to " + user.getEmail();
    }

    // ── RULE 2b: admin manually REACTIVATES ───────────────────────────────────
    public String reactivateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getStatus().equals("INACTIVE")) {
            throw new RuntimeException("User is not INACTIVE. Current status: " + user.getStatus());
        }

        user.setStatus("ACTIVE");
        user.setDeactivationReason(null);
        userRepository.save(user);

        emailService.sendReactivationEmail(user.getEmail(), user.getName());

        return "User reactivated. Email sent to " + user.getEmail();
    }

    // ── CREATE LECTURER ───────────────────────────────────────────────────────
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
        // CHANGE 3: save when credentials were sent — starts 7-day clock
        user.setCredentialsSentAt(LocalDateTime.now());
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

    // ── UPDATE PROFILE ────────────────────────────────────────────────────────
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

        String prefix =
                courseId.toUpperCase().startsWith("IT")
                        ? "IT"
                        : "BM";

        String rollNumber;

        do {

            int random = 1000 + new java.util.Random().nextInt(9000);

            rollNumber = prefix + year + random;

        } while(studentRepository.findByRollNumber(rollNumber).isPresent());

        return rollNumber;
    }

    private String generatePassword() {
        return java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} // 👈 AuthService පන්තිය වැහෙන bracket එක පමණක් ඉතිරි කරන්න
