package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity(name = "LegacySubject")
@Table(name = "subject")
@Data @NoArgsConstructor @AllArgsConstructor
public class Subject {

    @Id
    @Column(name = "subject_id")
    private Integer subjectId;

    @Column(name = "name", length = 125)
    private String name;

    @Column(name = "description", length = 150)
    private String description;

    @Column(name = "course_course_id")
    private Integer courseCourseId;
}
