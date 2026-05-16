package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.controller;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.dto.CreateExamScheduleRequest;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.ExamNotification;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.ExamSchedule;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.ExamScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/exam-schedule")
@RequiredArgsConstructor
public class ExamScheduleController {

    private final ExamScheduleService examScheduleService;

    // create exam schedule + auto send notification
    @PostMapping("/create")
    public ResponseEntity<String> createExamSchedule(
            @RequestBody CreateExamScheduleRequest request) {
        return ResponseEntity.ok(
                examScheduleService.createExamScheduleAndNotify(request));
    }

    // get all exam schedules
    @GetMapping("/all")
    public ResponseEntity<List<ExamSchedule>> getAllExamSchedules() {
        return ResponseEntity.ok(
                examScheduleService.getAllExamSchedules());
    }

    // get schedules by exam
    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<ExamSchedule>> getByExam(
            @PathVariable Integer examId) {
        return ResponseEntity.ok(
                examScheduleService.getExamSchedulesByExam(examId));
    }

    // admin views all exam notifications sent
    @GetMapping("/notifications")
    public ResponseEntity<List<ExamNotification>> getAllExamNotifications() {
        return ResponseEntity.ok(
                examScheduleService.getAllExamNotifications());
    }

    // admin views exam notifications by course
    @GetMapping("/notifications/course/{courseId}")
    public ResponseEntity<List<ExamNotification>> getBycourse(
            @PathVariable String courseId) {
        return ResponseEntity.ok(
                examScheduleService.getExamNotificationsByCourse(courseId));
    }
}