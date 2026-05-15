package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List; // මෙතැන List එකතු කිරීමට අමතක කරන්න එපා

@Entity
@Table(name = "question_paper")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paper_id")
    private Integer paperId;

    @Column(name = "paper_name")
    private String paperName;

    private String instructions;

    @Column(name = "exam_exam_id")
    private Integer examId;

    // --- අලුතින් එකතු කළ යුතු කොටස ---
    @ManyToMany
    @JoinTable(
            name = "paper_questions", // මෙය database එකේ අලුතින් හැදෙන junction table එකයි
            joinColumns = @JoinColumn(name = "paper_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions;
    // ---------------------------------
}