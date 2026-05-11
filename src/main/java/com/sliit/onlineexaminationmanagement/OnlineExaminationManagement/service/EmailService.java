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

    public void sendLecturerCredentials(String toEmail, String name, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tharindupiyangabc@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Your Lecturer Account Has Been Created");
        message.setText(
                "Dear " + name + ",\n\n" +
                        "Your lecturer account has been created.\n\n" +
                        "Login credentials:\n" +
                        "Email    : " + toEmail + "\n" +
                        "Password : " + password + "\n\n" +
                        "Please login and change your password.\n\n" +
                        "Regards,\nAdmin Team"
        );
        mailSender.send(message);
    }

    public void sendDeactivationEmail(String toEmail, String name, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tharindupiyangabc@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Your Account Has Been Deactivated");
        message.setText(
                "Dear " + name + ",\n\n" +
                        "Your account has been deactivated.\n\n" +
                        "Reason: " + reason + "\n\n" +
                        "Contact admin if you have questions.\n\n" +
                        "Regards,\nAdmin Team"
        );
        mailSender.send(message);
    }

    public void sendReactivationEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tharindupiyangabc@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Your Account Has Been Reactivated");
        message.setText(
                "Dear " + name + ",\n\n" +
                        "Your account has been reactivated.\n\n" +
                        "You can now login using your credentials.\n\n" +
                        "Regards,\nAdmin Team"
        );
        mailSender.send(message);
    }

    public void sendRejectionEmail(String toEmail, String name, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tharindupiyangabc@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Your Registration Has Been Rejected");
        message.setText(
                "Dear " + name + ",\n\n" +
                        "Unfortunately, your registration has been rejected.\n\n" +
                        "Reason: " + reason + "\n\n" +
                        "You may re-register with corrected details at any time.\n" +
                        "Please fix the issue mentioned above before registering again.\n\n" +
                        "Regards,\nAdmin Team"
        );
        mailSender.send(message);
    }
}