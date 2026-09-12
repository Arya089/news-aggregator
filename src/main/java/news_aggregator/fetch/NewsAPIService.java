package news_aggregator.fetch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import news_aggregator.model.Article;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsAPIService {

    @Value("${newsapi.key}")
    private String apiKey;

    @Value("${newsapi.url}")
    private String baseUrl;

    private WebClient buildClient() {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    public List<Article> fetchByCategory(String category) {
        List<Article> articles = new ArrayList<>();

        try {
            NewsApiResponse response = buildClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/top-headlines")
                            .queryParam("category", category)
                            .queryParam("country", "us")
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(NewsApiResponse.class)
                    .block();

            if (response != null && response.articles() != null) {
                for (NewsApiArticleDto dto : response.articles()) {
                    if (dto.title() == null || dto.url() == null) {
                        continue;
                    }

                    Article article = new Article();
                    article.setTitle(dto.title());
                    article.setContent(dto.description() != null ? dto.description() : dto.content());
                    article.setUrl(dto.url());
                    article.setSourceUrl(dto.url());
                    article.setCategory(category);
                    article.setAuthor(dto.author());
                    article.setSource(dto.source() != null ? dto.source().name() : "NewsAPI");
                    article.setSentiment("NEUTRAL");
                    article.setFetchedAt(LocalDateTime.now());

                    if (dto.publishedAt() != null) {
                        try {
                            article.setPublishedAt(
                                    OffsetDateTime.parse(dto.publishedAt()).toLocalDateTime());
                        } catch (Exception ignored) {
                            // leave publishedAt null if parsing fails
                        }
                    }

                    articles.add(article);
                }
            }
        } catch (Exception e) {
            System.err.println("NewsAPI fetch failed for category " + category + ": " + e.getMessage());
        }

        return articles;
    }

    // ---- Response DTOs for JSON mapping ----

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NewsApiResponse(String status, List<NewsApiArticleDto> articles) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NewsApiArticleDto(
            NewsApiSourceDto source,
            String author,
            String title,
            String description,
            String url,
            String urlToImage,
            String publishedAt,
            String content
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NewsApiSourceDto(String id, String name) {}
}