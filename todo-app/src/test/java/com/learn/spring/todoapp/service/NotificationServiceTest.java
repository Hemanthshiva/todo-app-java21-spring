package com.learn.spring.todoapp.service;

import com.learn.spring.todoapp.entity.Notification;
import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password", "test@example.com");
        notification = new Notification();
        notification.setId(1L);
        notification.setRecipient(user);
        notification.setMessage("Test Notification");
        notification.setRead(false);
    }

    @Test
    void createNotification_ShouldSaveNotification() {
        // Given
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // When
        notificationService.createNotification(user, "Test Message", 1L);

        // Then
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getUnreadNotifications_ShouldReturnList() {
        // Given
        when(notificationRepository.findByRecipientUsernameAndIsReadFalseOrderByCreatedAtDesc("testuser"))
                .thenReturn(Collections.singletonList(notification));

        // When
        List<Notification> results = notificationService.getUnreadUserNotifications("testuser");

        // Then
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Test Notification", results.get(0).getMessage());
    }

    @Test
    void markAsRead_ShouldUpdateStatus() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        
        // When
        notificationService.markAsRead(1L);

        // Then
        assertTrue(notification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }
}
