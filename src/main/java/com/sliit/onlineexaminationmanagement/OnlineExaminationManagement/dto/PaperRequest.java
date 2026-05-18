package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto;

import lombok.Data;

@Data
public class PaperRequest {
    private Integer subjectId;
    private Integer count;
    private String difficulty;
    private String paperName;
    private String instructions;
}