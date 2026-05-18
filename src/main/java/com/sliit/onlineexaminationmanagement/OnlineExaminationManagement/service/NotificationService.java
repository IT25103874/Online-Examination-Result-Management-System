package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.NotificationResponse;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.SendNotificationRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Notification;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.NotificationRecipient;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Student;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ExamNotificationRepository examNotificationRepository;

    // ── SEND TO ALL STUDENTS (both IT and BM) ────────────────────────────────
    public String sendToAll(SendNotificationRequest request) {

        // save notification
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getCategory());
        notification.setTarget("ALL");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSentBy(request.getSentBy());
        notificationRepository.save(notification);

        // find all active students
        List<User> allStudents = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole().equals("STUDENT") && u.getStatus().equals("ACTIVE"))
                .collect(Collectors.toList());

        // save recipient row for each student + send email
        for (User student : allStudents) {
            saveRecipient(notification, student);
            emailService.sendNotificationEmail(
                    student.getEmail(),
                    student.getName(),
                    request.getTitle(),
                    request.getMessage()
            );
        }

        return "Notification sent to " + allStudents.size() + " students.";
    }

    // ── SEND TO SPECIFIC COURSE (IT or BM) ────────────────────────────────────
    public String sendToCourse(String courseId, SendNotificationRequest request) {

        // save notification
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getCategory());
        notification.setTarget(courseId.toUpperCase());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSentBy(request.getSentBy());
        notificationRepository.save(notification);

        // find all active students in that course
        List<Student> courseStudents = studentRepository.findAll()
                .stream()
                .filter(s -> s.getCourseId() != null
                        && s.getCourseId().trim().toUpperCase()
                        .contains(courseId.trim().toUpperCase())
                        && s.getUser().getStatus().equals("ACTIVE"))
                .collect(Collectors.toList());

        // save recipient row for each + send email
        for (Student student : courseStudents) {
            saveRecipient(notification, student.getUser());
            emailService.sendNotificationEmail(
                    student.getUser().getEmail(),
                    student.getUser().getName(),
                    request.getTitle(),
                    request.getMessage()
            );
        }

        return "Notification sent to " + courseStudents.size() + " students in " + courseId.toUpperCase() + " course.";
    }

    // ── SEND TO SPECIFIC USER (exam reminder / schedule) ──────────────────────
    public String sendToUser(Integer userId, SendNotificationRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Cannot send notification to inactive user");
        }

        // save notification
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getCategory());
        notification.setTarget("USER_" + userId);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSentBy(request.getSentBy());
        notificationRepository.save(notification);

        // save one recipient row
        saveRecipient(notification, user);

        // send email
        emailService.sendNotificationEmail(
                user.getEmail(),
                user.getName(),
                request.getTitle(),
                request.getMessage()
        );

        return "Notification sent to " + user.getName() + " (" + user.getEmail() + ").";
    }

    // ── STUDENT VIEWS ALL THEIR NOTIFICATIONS ────────────────────────────────
    public List<NotificationResponse> getMyNotifications(Integer userId) {
        return recipientRepository
                .findByUser_UserIdOrderByNotification_CreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── STUDENT VIEWS ONLY UNREAD ─────────────────────────────────────────────
    public List<NotificationResponse> getUnreadNotifications(Integer userId) {
        return recipientRepository
                .findByUser_UserIdAndIsReadOrderByNotification_CreatedAtDesc(userId, false)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── MARK AS READ ──────────────────────────────────────────────────────────
    public String markAsRead(Integer recipientId) {
        NotificationRecipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        recipient.setIsRead(true);
        recipient.setReadAt(LocalDateTime.now());
        recipientRepository.save(recipient);

        return "Notification marked as read.";
    }

    // ── MARK AS UNREAD ────────────────────────────────────────────────────────
    public String markAsUnread(Integer recipientId) {
        NotificationRecipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        recipient.setIsRead(false);
        recipient.setReadAt(null);
        recipientRepository.save(recipient);

        return "Notification marked as unread.";
    }

    // ── NOTIFICATION HISTORY (all — read and unread) ──────────────────────────
    public List<NotificationResponse> getHistory(Integer userId) {
        // same as getMyNotifications — returns everything newest first
        return recipientRepository
                .findByUser_UserIdOrderByNotification_CreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── COUNT UNREAD ──────────────────────────────────────────────────────────
    public int countUnread(Integer userId) {
        return recipientRepository.countByUser_UserIdAndIsRead(userId, false);
    }

    // ── ADMIN VIEWS ALL SENT NOTIFICATIONS ───────────────────────────────────
    public List<Notification> getAllSentNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }

    // ── HELPER: save recipient row ────────────────────────────────────────────
    private void saveRecipient(Notification notification, User user) {
        NotificationRecipient recipient = new NotificationRecipient();
        recipient.setNotification(notification);
        recipient.setUser(user);
        recipient.setIsRead(false);
        recipient.setReadAt(null);
        recipientRepository.save(recipient);
    }

    // ── HELPER: convert to response DTO ──────────────────────────────────────
    private NotificationResponse toResponse(NotificationRecipient r) {
        return new NotificationResponse(
                r.getRecipientId(),
                r.getNotification().getTitle(),
                r.getNotification().getMessage(),
                r.getNotification().getType(),
                r.getIsRead(),
                r.getReadAt(),
                r.getNotification().getCreatedAt()
        );
    }

    // ── ADMIN DELETE NOTIFICATION ─────────────────────────────────────────────
    @org.springframework.transaction.annotation.Transactional
    public String deleteNotification(Integer notificationId) {

        if (!notificationRepository.existsById(notificationId)) {
            throw new RuntimeException("Notification not found");
        }

        // delete exam notification links first
        examNotificationRepository
                .deleteByNotificationId(notificationId);

        // delete recipient links
        recipientRepository
                .deleteByNotificationId(notificationId);

        // finally delete notification
        notificationRepository.deleteById(notificationId);

        return "Notification deleted successfully.";
    }

    // ── STUDENT DELETE NOTIFICATION (removes from their view only) ────────────
    public String deleteMyNotification(Integer recipientId) {
        if (!recipientRepository.existsById(recipientId)) {
            throw new RuntimeException("Notification not found");
        }
        recipientRepository.deleteById(recipientId);
        return "Notification removed.";
    }
}