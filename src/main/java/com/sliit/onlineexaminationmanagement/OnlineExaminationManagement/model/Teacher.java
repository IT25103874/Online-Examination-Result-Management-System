package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teacher")
@Data @NoArgsConstructor @AllArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Integer teacherId;

    @Column(name = "qualification", length = 45)
    private String qualification;

    @Column(name = "phone", length = 10)
    private String phone;

    @Column(name = "department", length = 125)
    private String department;

    @ManyToOne
    @JoinColumn(name = "User_user_id")
    private User user;
}