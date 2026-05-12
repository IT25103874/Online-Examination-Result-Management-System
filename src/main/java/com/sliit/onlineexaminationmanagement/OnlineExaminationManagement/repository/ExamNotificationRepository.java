package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.ExamNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamNotificationRepository extends JpaRepository<ExamNotification, Integer> {

    // check if notification already sent for this schedule
    // prevents duplicate notifications
    Optional<ExamNotification> findByExamSchedule_ScheduleId(Integer scheduleId);

    // get all exam notifications for admin view
    List<ExamNotification> findAllByOrderBySentAtDesc();

    // get all notifications sent for a specific course
    List<ExamNotification> findByCourseId(String courseId);
}