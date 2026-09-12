package news_aggregator.fetch;

import news_aggregator.Kafka.KafkaProducerService;
import news_aggregator.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsScheduler {

    @Autowired
    private NewsAPIService newsAPIService;

    @Autowired
    private RSSFeedService rssService;

    @Autowired
    private KafkaProducerService kafkaProducer;

    // Fetch from NewsAPI every 15 mins
    @Scheduled(fixedDelay = 900000)
    public void fetchFromNewsAPI() {
        List<String> categories = List.of(
                "technology", "sports",
                "business", "health",
                "entertainment");

        categories.forEach(category -> {
            List<Article> articles =
                    newsAPIService.fetchByCategory(
                            category);
            articles.forEach(article ->
                    kafkaProducer.publishArticle(article));
            System.out.println("Fetched " +
                    articles.size() +
                    " articles for " + category);
        });
    }

    // Fetch from RSS every 30 mins
    @Scheduled(fixedDelay = 1800000)
    public void fetchFromRSS() {
        // BBC News
        fetchRSSAndPublish(
                "http://feeds.bbci.co.uk/news/rss.xml",
                "world");

        // NDTV
        fetchRSSAndPublish(
                "https://feeds.feedburner.com/ndtvnews-top-stories",
                "india");

        // Times of India
        fetchRSSAndPublish(
                "https://timesofindia.indiatimes.com/rssfeedstopstories.cms",
                "india");
    }

    private void fetchRSSAndPublish(
            String url, String category) {
        List<Article> articles =
                rssService.fetchFromRSS(url, category);
        articles.forEach(article ->
                kafkaProducer.publishArticle(article));
        System.out.println("RSS fetched " +
                articles.size() +
                " from " + url);
    }
}