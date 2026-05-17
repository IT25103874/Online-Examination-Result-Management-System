package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Entity
@Table(name = "users")

@Data @NoArgsConstructor @AllArgsConstructor


public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "name", length = 125)
    private String name;

    @Column(name = "email", length = 125, unique = true)
    private String email;

    @Column(name = "password", length = 45)
    private String password;

    @Column(name = "role", length = 45)
    private String role; // "STUDENT"

    @Column(name = "status", length = 25)
    private String status; // "PENDING"

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason; // filled when admin rejects

    @Column(name = "credentials_sent_at")
    private java.time.LocalDateTime credentialsSentAt;

    @Column(name = "last_login_at")
    private java.time.LocalDateTime lastLoginAt;

    @Column(name = "deactivation_reason", length = 255)
    private String deactivationReason;
}
