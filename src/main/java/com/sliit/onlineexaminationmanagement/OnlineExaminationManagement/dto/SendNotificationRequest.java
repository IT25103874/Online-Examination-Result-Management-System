package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto;

import lombok.Data;

@Data
public class SendNotificationRequest {
    private String title;
    private String message;
    private Integer sentBy; // admin userId
}