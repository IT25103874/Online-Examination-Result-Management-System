package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.CreateExamScheduleRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.*;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamScheduleService {

    private final ExamModelRepository examRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final ExamNotificationRepository examNotificationRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final StudentRepository studentRepository;
    private final EmailService emailService;

    
    public String createExamScheduleAndNotify(CreateExamScheduleRequest request) {

        LegacyExam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        
        LegacySubject subject = exam.getSubject();
        if (subject == null) {
            throw new RuntimeException("Subject not found for this exam");
        }

        
        Integer courseCourseId = subject.getCourseCourseId();
        if (courseCourseId == null) {
            throw new RuntimeException("Course not found for this subject");
        }

       
        String courseSearch = courseCourseId == 1 ? "IT" : "BM";

        
        ExamSchedule examSchedule = new ExamSchedule();
        examSchedule.setStartTime(request.getStartTime());
        examSchedule.setEndTime(request.getEndTime());
        examSchedule.setVenueMode(request.getVenueMode());
        examSchedule.setExam(exam);
        examScheduleRepository.save(examSchedule);

        
        boolean alreadySent = examNotificationRepository
                .findByExamSchedule_ScheduleId(examSchedule.getScheduleId())
                .isPresent();

        if (alreadySent) {
            return "Exam schedule created but notification already sent for this schedule.";
        }

        
        List<Student> students = studentRepository
                .findByCourseIdContainingIgnoreCase(courseSearch)
                .stream()
                .filter(s -> s.getUser().getStatus().equals("ACTIVE"))
                .toList();

        if (students.isEmpty()) {
            return "Exam schedule created. No active students found in " + courseSearch + " course.";
        }

       
        String title = "Exam Reminder — " + exam.getExamName();
        String message =
                "Your exam is scheduled as follows:\n\n" +
                        "Exam    : " + exam.getExamName() + "\n" +
                        "Subject : " + subject.getName() + "\n" +
                        "Date    : " + exam.getExamDate() + "\n" +
                        "Time    : " + request.getStartTime() + " — " + request.getEndTime() + "\n" +
                        "Venue   : " + request.getVenueMode() + "\n\n" +
                        "Please be present 15 minutes early.\n\n" +
                        "Regards,\nAdmin Team";

        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType("EXAM_REMINDER");
        notification.setTarget(courseSearch);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSentBy(request.getSentBy());
        notificationRepository.save(notification);

       
        ExamNotification examNotification = new ExamNotification();
        examNotification.setExamSchedule(examSchedule);
        examNotification.setNotification(notification);
        examNotification.setSentAt(LocalDateTime.now());
        examNotification.setCourseId(courseSearch);
        examNotificationRepository.save(examNotification);

       
        int emailCount = 0;
        for (Student student : students) {
            
            NotificationRecipient recipient = new NotificationRecipient();
            recipient.setNotification(notification);
            recipient.setUser(student.getUser());
            recipient.setIsRead(false);
            recipient.setReadAt(null);
            notificationRecipientRepository.save(recipient);

           
            emailService.sendNotificationEmail(
                    student.getUser().getEmail(),
                    student.getUser().getName(),
                    title,
                    message
            );
            emailCount++;
        }

        return "Exam schedule created. Notification sent to "
                + emailCount + " students in "
                + courseSearch + " course.";
    }

   
    public List<ExamSchedule> getAllExamSchedules() {
        return examScheduleRepository.findAll();
    }

    
    public List<ExamSchedule> getExamSchedulesByExam(Integer examId) {
        return examScheduleRepository.findByExam_ExamId(examId);
    }

   
    public List<ExamNotification> getAllExamNotifications() {
        return examNotificationRepository.findAllByOrderBySentAtDesc();
    }

    
    public List<ExamNotification> getExamNotificationsByCourse(String courseId) {
        return examNotificationRepository.findByCourseId(courseId);
    }
}
