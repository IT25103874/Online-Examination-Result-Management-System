package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_has_question_paper")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionHasQuestionPaper {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "question_question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "question_paper_paper_id")
    private QuestionPaper questionPaper;

    @Column(name = "sequence_no")
    private String sequenceNo;

    private String marks;
}
