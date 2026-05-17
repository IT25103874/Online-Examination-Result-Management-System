package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.CreateExamScheduleRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.*;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class  ExamScheduleService {

    private final ExamRepository examRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final ExamNotificationRepository examNotificationRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final EmailService emailService;

    // ── CREATE EXAM SCHEDULE + AUTO SEND NOTIFICATION ─────────────────────────
    public String createExamScheduleAndNotify(CreateExamScheduleRequest request) {

        // STEP 1: find exam
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // STEP 2: find subject from exam
        Subject subject = exam.getSubject();
        if (subject == null) {
            throw new RuntimeException("Subject not found for this exam");
        }

        // STEP 3: find course from subject
        Integer courseCourseId = subject.getCourseCourseId();
        if (courseCourseId == null) {
            throw new RuntimeException("Course not found for this subject");
        }

        // STEP 4: determine course name IT or BM
        // courseCourseId 1 = IT, 2 = BM (based on your course table)
        // we search students by courseId containing IT or BM
        String courseSearch = courseCourseId == 1 ? "IT" : "BM";

        // STEP 5: save exam schedule
        ExamSchedule examSchedule = new ExamSchedule();
        examSchedule.setStartTime(request.getStartTime());
        examSchedule.setEndTime(request.getEndTime());
        examSchedule.setVenueMode(request.getVenueMode());
        examSchedule.setExam(exam);
        examScheduleRepository.save(examSchedule);

        // STEP 6: check if notification already sent for this schedule
        boolean alreadySent = examNotificationRepository
                .findByExamSchedule_ScheduleId(examSchedule.getScheduleId())
                .isPresent();

        if (alreadySent) {
            return "Exam schedule created but notification already sent for this schedule.";
        }

        // STEP 7: find all ACTIVE students in that course
        List<Student> students = studentRepository
                .findByCourseIdContainingIgnoreCase(courseSearch)
                .stream()
                .filter(s -> s.getUser().getStatus().equals("ACTIVE"))
                .toList();

        if (students.isEmpty()) {
            return "Exam schedule created. No active students found in " + courseSearch + " course.";
        }

        // STEP 8: auto build notification message from exam data
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

        // STEP 9: save notification
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType("EXAM_REMINDER");
        notification.setTarget(courseSearch);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSentBy(request.getSentBy());
        notificationRepository.save(notification);

        // STEP 10: save exam_notification row
        // links exam_schedule + notification together
        ExamNotification examNotification = new ExamNotification();
        examNotification.setExamSchedule(examSchedule);
        examNotification.setNotification(notification);
        examNotification.setSentAt(LocalDateTime.now());
        examNotification.setCourseId(courseSearch);
        examNotificationRepository.save(examNotification);

        // STEP 11: save recipient row per student + send email
        int emailCount = 0;
        for (Student student : students) {
            // save recipient row
            NotificationRecipient recipient = new NotificationRecipient();
            recipient.setNotification(notification);
            recipient.setUser(student.getUser());
            recipient.setIsRead(false);
            recipient.setReadAt(null);
            notificationRecipientRepository.save(recipient);

            // send email
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

    // ── GET ALL EXAM SCHEDULES ────────────────────────────────────────────────
    public List<ExamSchedule> getAllExamSchedules() {
        return examScheduleRepository.findAll();
    }

    // ── GET EXAM SCHEDULES BY EXAM ────────────────────────────────────────────
    public List<ExamSchedule> getExamSchedulesByExam(Integer examId) {
        return examScheduleRepository.findByExam_ExamId(examId);
    }

    // ── GET ALL EXAM NOTIFICATIONS — admin view ───────────────────────────────
    public List<ExamNotification> getAllExamNotifications() {
        return examNotificationRepository.findAllByOrderBySentAtDesc();
    }

    // ── GET EXAM NOTIFICATIONS BY COURSE ─────────────────────────────────────
    public List<ExamNotification> getExamNotificationsByCourse(String courseId) {
        return examNotificationRepository.findByCourseId(courseId);
    }
}