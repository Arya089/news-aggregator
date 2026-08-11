package news_aggregator.Kafka;

import news_aggregator.model.Article;
import news_aggregator.model.Notification;
import news_aggregator.model.User;
import news_aggregator.repository.NotificationRepository;
import news_aggregator.repository.UserRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationConsumer.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(
            topics = "news-enriched",
            groupId = "notification-group"
    )
    public void sendNotifications(
            ConsumerRecord<String, Article> record,
            Acknowledgment ack) {
        try {
            Article article = record.value();

            if (article == null) {
                ack.acknowledge();
                return;
            }

            // Get all users
            List<User> allUsers = userRepository.findAll();

            // Filter users interested in this category
            List<User> interestedUsers = allUsers
                    .stream()
                    .filter(user ->
                            isUserInterestedInCategory(
                                    user, article.getCategory()))
                    .toList();

            // Create notification for each user
            for (User user : interestedUsers) {
                try {
                    Notification notification =
                            new Notification();

                    notification.setUserId(user.getId());
                    notification.setTitle(
                            "New " +
                                    article.getCategory() +
                                    " article!");
                    notification.setMessage(
                            article.getTitle() != null ?
                                    article.getTitle() :
                                    "New article available");
                    notification.setArticleId(
                            article.getId() != null ?
                                    article.getId().toString() :
                                    "");
                    notification.setRead(false);
                    notification.setCreatedAt(
                            LocalDateTime.now());

                    notificationRepository
                            .save(notification);

                } catch (Exception e) {
                    log.error(
                            "Error creating notification " +
                                    "for user {}: ",
                            user.getId(), e);
                }
            }

            log.info(
                    "Notifications sent for article: {}",
                    article.getTitle());

            ack.acknowledge();

        } catch (Exception e) {
            log.error(
                    "Error in notification consumer: ", e);
            ack.acknowledge();
        }
    }

    // Check if user is interested in category
    private boolean isUserInterestedInCategory(
            User user, String category) {

        if (user.getPreferredCategories() == null
                || user.getPreferredCategories()
                .isEmpty()) {
            return false;
        }

        if (category == null || category.isEmpty()) {
            return false;
        }

        // Split comma-separated categories
        String[] categories = user
                .getPreferredCategories()
                .split(",");

        for (String cat : categories) {
            if (cat.trim().equalsIgnoreCase(
                    category.trim())) {
                return true;
            }
        }
        return false;
    }
}