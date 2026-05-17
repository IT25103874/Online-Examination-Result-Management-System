package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private Integer recipientId;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}