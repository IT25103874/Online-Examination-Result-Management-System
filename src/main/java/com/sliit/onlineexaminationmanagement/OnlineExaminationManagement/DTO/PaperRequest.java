package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO;

import lombok.Data;

@Data
public class PaperRequest { // මෙතැන අමතර 'public class PaperRequest' පේළියක් තිබේ නම් එය ඉවත් කරන්න
    private Integer subjectId;
    private Integer count;
    private String difficulty;
    private String paperName;
    private String instructions;
}