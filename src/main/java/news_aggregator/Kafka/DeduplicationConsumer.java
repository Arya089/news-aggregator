package news_aggregator.Kafka;

import news_aggregator.model.Article;
import news_aggregator.repository.ArticleRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeduplicationConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(DeduplicationConsumer.class);

    // Simple in-memory deduplication set
    private final ConcurrentHashMap<String, Boolean> seenUrls =
            new ConcurrentHashMap<>();

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = "news-raw-fetched",
            groupId = "dedup-group"
    )
    public void deduplicate(
            ConsumerRecord<String, Article> record,
            Acknowledgment ack) {
        try {
            Article article = record.value();

            if (article == null || article.getUrl() == null) {
                ack.acknowledge();
                return;
            }

            String url = article.getUrl();

            // Check if already seen
            if (seenUrls.containsKey(url)) {
                log.info("Duplicate article skipped: {}", url);
                ack.acknowledge();
                return;
            }

            // Check DB too
            if (articleRepository.existsByUrl(url)) {
                seenUrls.put(url, Boolean.TRUE);
                log.info("Article exists in DB: {}", url);
                ack.acknowledge();
                return;
            }

            // New article! Mark as seen and pass forward
            seenUrls.put(url, Boolean.TRUE);
            kafkaTemplate.send("news-processed",
                    article.getCategory(), article);
            log.info("New article passed: {}", url);

            ack.acknowledge();

        } catch (Exception e) {
            log.error("Error in deduplication: ", e);
            ack.acknowledge();
        }
    }
}