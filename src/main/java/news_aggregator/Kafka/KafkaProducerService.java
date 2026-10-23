package news_aggregator.Kafka;

import news_aggregator.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publishNewsEnriched(Long articleId, String title, String category) {
        Map<String, Object> event = Map.of(
                "articleId", articleId,
                "title", title,
                "category", category,
                "eventType", "ARTICLE_CREATED"
        );
        kafkaTemplate.send("news-enriched", articleId.toString(), event);
    }

    public void publishArticle(Article article) {
        try {
            kafkaTemplate.send(
                    "news-raw-fetched",
                    article.getCategory(),
                    article);
        } catch (Exception e) {
            System.err.println(
                    "Kafka publish failed: " + e.getMessage());
        }
    }

    public void publishUserActivity(Long userId, Long articleId, String activityType) {
        Map<String, Object> event = Map.of(
                "userId", userId,
                "articleId", articleId,
                "activityType", activityType
        );
        kafkaTemplate.send("user-activity-events", userId.toString(), event);
    }
}