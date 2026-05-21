package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "LegacySubject")
@Table(name = "subject")
@Data @NoArgsConstructor @AllArgsConstructor
public class LegacySubject {

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
