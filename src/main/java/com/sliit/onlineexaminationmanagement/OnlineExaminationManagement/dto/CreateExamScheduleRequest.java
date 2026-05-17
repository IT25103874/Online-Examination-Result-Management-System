package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO;

import lombok.Data;

@Data
public class CreateExamScheduleRequest {
    private Integer examId;      // which exam this schedule is for
    private String startTime;    // "09:00 AM"
    private String endTime;      // "11:00 AM"
    private String venueMode;    // "Hall 3" or "Online"
    private Integer sentBy;      // admin userId
}