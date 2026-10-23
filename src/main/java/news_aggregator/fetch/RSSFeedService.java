package news_aggregator.fetch;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import news_aggregator.model.Article;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RSSFeedService {

    public List<Article> fetchFromRSS(
            String feedUrl, String category) {
        List<Article> articles = new ArrayList<>();

        try {
            URL url = new URL(feedUrl);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(
                    new XmlReader(url));

            for (SyndEntry entry : feed.getEntries()) {
                try {
                    Article article = new Article();
                    article.setTitle(entry.getTitle());
                    article.setUrl(
                            entry.getLink());
                    article.setCategory(category);
                    article.setSentiment("NEUTRAL");
                    article.setFetchedAt(
                            LocalDateTime.now());

                    if (entry.getDescription() != null) {
                        article.setContent(
                                entry.getDescription()
                                        .getValue());
                    }

                    if (article.getTitle() != null
                            && article.getUrl() != null) {
                        articles.add(article);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            System.err.println(
                    "RSS fetch failed: " + feedUrl);
        }
        return articles;
    }
}