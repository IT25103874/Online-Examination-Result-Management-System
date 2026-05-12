package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;


import jakarta.persistence.*;
import lombok.*;

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
}
