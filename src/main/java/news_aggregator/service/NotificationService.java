package news_aggregator.service;

import news_aggregator.model.Notification;
import news_aggregator.model.User;
import news_aggregator.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    public List<Notification> getNotifications(String username) {
        User user = userService.getUserByUsername(username);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public void markAsRead(String username, Long notificationId) {
        User user = userService.getUserByUsername(username);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!notification.getUserId().equals(user.getId())) {
            throw new RuntimeException("This notification does not belong to you");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(String username) {
        User user = userService.getUserByUsername(username);
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(user.getId());
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    // Simple helper for creating notifications (e.g. from other services later)
    public Notification createNotification(Long userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        return notificationRepository.save(notification);
    }
}