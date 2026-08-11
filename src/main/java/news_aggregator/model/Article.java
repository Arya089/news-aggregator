package news_aggregator.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // Keep old field
    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    // Add url field (used by consumers)
    @Column(length = 500, unique = true)
    private String url;

    @Column(length = 100)
    private String category;

    @Column(length = 150)
    private String author;

    // Add source name field
    @Column(length = 100)
    private String source;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    // Add sentiment field
    @Column(length = 20)
    private String sentiment = "NEUTRAL";

    // Add fetchedAt field
    @Column(name = "fetched_at")
    private LocalDateTime fetchedAt;

    // Add viewCount for trending
    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    @Column(name = "read_count")
    private Long readCount = 0L;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}