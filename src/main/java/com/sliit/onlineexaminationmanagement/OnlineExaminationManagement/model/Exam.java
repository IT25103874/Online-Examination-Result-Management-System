package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "exam")
@Data @NoArgsConstructor @AllArgsConstructor
public class Exam {

    @Id
    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "exam_name", length = 45)
    private String examName;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(name = "total_marks")
    private Integer totalMarks;

    @ManyToOne
    @JoinColumn(name = "subject_subject_id")
    private Subject subject;
}