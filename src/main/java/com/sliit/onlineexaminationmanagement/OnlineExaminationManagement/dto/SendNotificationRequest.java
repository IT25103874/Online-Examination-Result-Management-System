package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.DTO;

import lombok.Data;

@Data
public class SendNotificationRequest {
    private String title;
    private String message;
    private String category;
    private Integer sentBy; // admin userId
}