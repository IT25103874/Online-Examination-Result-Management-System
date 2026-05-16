package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.ExamNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExamNotificationRepository
        extends JpaRepository<ExamNotification, Integer> {

    // check if notification already sent for this schedule
    Optional<ExamNotification>
    findByExamSchedule_ScheduleId(Integer scheduleId);

    // get all exam notifications for admin view
    List<ExamNotification> findAllByOrderBySentAtDesc();

    // get all notifications sent for a specific course
    List<ExamNotification> findByCourseId(String courseId);

    // FIXED DELETE QUERY
    @Modifying
    @Transactional
    @Query("DELETE FROM ExamNotification e WHERE e.notification.notificationId = :notificationId")
    void deleteByNotificationId(
            @Param("notificationId")
            Integer notificationId
    );
}