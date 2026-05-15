package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subject")
@Getter @Setter
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // මෙය අනිවාර්යයෙන්ම තිබිය යුතුය
    private Integer subjectId;

    @Column(nullable = false, length = 125)
    private String name;

    @Column(length = 150)
    private String description;
    
}