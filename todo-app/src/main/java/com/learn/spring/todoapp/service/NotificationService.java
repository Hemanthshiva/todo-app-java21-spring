package com.learn.spring.todoapp.service;

import com.learn.spring.todoapp.entity.Notification;
import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(User recipient, String message, Long relatedTodoId) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setRelatedTodoId(relatedTodoId);
        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(String username) {
        return notificationRepository.findByRecipientUsernameOrderByCreatedAtDesc(username);
    }

    public List<Notification> getUnreadUserNotifications(String username) {
        return notificationRepository.findByRecipientUsernameAndIsReadFalseOrderByCreatedAtDesc(username);
    }
    
    public long getUnreadCount(String username) {
        return notificationRepository.countByRecipientUsernameAndIsReadFalse(username);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}
