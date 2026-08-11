package news_aggregator.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Keep userId for simple queries
    @Column(name = "user_id")
    private Long userId;

    // Add User relationship for consumer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            insertable = false, updatable = false)
    private User user;

    // Add title field
    @Column(length = 200)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    // Add articleId field
    @Column(name = "article_id",
            length = 100)
    private String articleId;

    // Changed from isRead to read
    @Column(nullable = false)
    private boolean read = false;

    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt =
            LocalDateTime.now();
}