package com.learn.spring.todoapp.repository;

import com.learn.spring.todoapp.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientUsernameOrderByCreatedAtDesc(String recipientUsername);
    List<Notification> findByRecipientUsernameAndIsReadFalseOrderByCreatedAtDesc(String recipientUsername);
    long countByRecipientUsernameAndIsReadFalse(String recipientUsername);
}
