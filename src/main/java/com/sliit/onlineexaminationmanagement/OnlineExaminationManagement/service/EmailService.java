package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendApprovalEmail(String toEmail, String name, String rollNumber, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tharindupiyangabc@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Your Registration Has Been Approved!");
        message.setText(
                "Dear " + name + ",\n\n" +
                        "Your registration has been approved!\n\n" +
                        "Your login credentials:\n" +
                        "Roll Number : " + rollNumber + "\n" +
                        "Password    : " + password + "\n\n" +
                        "You can login using your email or roll number.\n\n" +
                        "Regards,\nAdmin Team"
        );
        mailSender.send(message);
    }

}