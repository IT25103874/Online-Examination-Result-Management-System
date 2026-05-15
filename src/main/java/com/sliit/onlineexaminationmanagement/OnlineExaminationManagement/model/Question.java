package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer question_id;

    @Column(name = "question_text")
    private String questionText;

    @Column(name = "question_type")
    private String questionType;

    private String marks;

    // පවතින Question class එක ඇතුළත මේවා එකතු කරන්න
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    private String difficultyLevel; // "Easy", "Medium", "Hard"

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Option> options;
}