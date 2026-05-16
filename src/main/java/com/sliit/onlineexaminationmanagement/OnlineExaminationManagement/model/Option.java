package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Integer optionId;

    @Column(name = "option_text")
    private String optionText;

    @Column(name = "is_correct")
    private String isCorrect;

    @ManyToOne
    @JoinColumn(name = "question_question_id")
    private Question question;
}