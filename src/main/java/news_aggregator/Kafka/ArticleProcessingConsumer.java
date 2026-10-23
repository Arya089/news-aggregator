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

import java.time.LocalDateTime;

@Service
public class ArticleProcessingConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(ArticleProcessingConsumer.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = "news-processed",
            groupId = "processing-group"
    )
    public void processArticle(
            ConsumerRecord<String, Article> record,
            Acknowledgment ack) {
        try {
            Article article = record.value();

            if (article == null) {
                ack.acknowledge();
                return;
            }

            // Step 1: Clean title
            if (article.getTitle() != null) {
                article.setTitle(cleanText(article.getTitle()));
            }

            // Step 2: Clean content
            if (article.getContent() != null) {
                article.setContent(cleanText(article.getContent()));
            }

            // Step 3: Set fetch time
            article.setFetchedAt(LocalDateTime.now());

            // Step 4: Set default sentiment
            if (article.getSentiment() == null) {
                article.setSentiment("NEUTRAL");
            }

            // Step 5: Save to database
            articleRepository.save(article);
            log.info("Article saved: {}", article.getTitle());

            // Step 6: Send to enrichment topic
            kafkaTemplate.send("news-enriched",
                    article.getCategory(), article);

            ack.acknowledge();

        } catch (Exception e) {
            log.error("Error processing article: ", e);
            ack.acknowledge();
        }
    }

    // Remove HTML tags and clean text
    private String cleanText(String text) {
        if (text == null) return "";
        // Remove HTML tags
        text = text.replaceAll("<[^>]*>", "");
        // Remove extra spaces
        text = text.replaceAll("\\s+", " ").trim();
        // Remove special characters
        text = text.replaceAll("[^\\x00-\\x7F]", "");
        return text;
    }
}