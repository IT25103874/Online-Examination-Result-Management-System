package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO;

import lombok.Data;

@Data
public class PaperRequest {
    private Integer subjectId;
    private Integer count;
    private String difficulty;
    private String paperName;
    private String instructions;
}