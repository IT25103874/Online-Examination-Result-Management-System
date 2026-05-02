package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "student")
@Data @NoArgsConstructor @AllArgsConstructor
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "std_id")
    private Integer stdId;

    @Column(name = "roll_number", length = 45)
    private String rollNumber; // filled later after admin approves

    @Column(name = "course_id", length = 30)
    private String courseId;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone", length = 10)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "User_user_id")
    private User user;
}
