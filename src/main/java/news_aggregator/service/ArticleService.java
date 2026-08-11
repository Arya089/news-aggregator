package news_aggregator.service;

import news_aggregator.model.Article;
import news_aggregator.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Cache news by category — 15 min TTL
    @Cacheable(value = "news",
            key = "#category + '_' + #page")
    public List<Article> getByCategory(
            String category, int page) {
        return articleRepository
                .findByCategoryOrderByPublishedAtDesc(
                        category,
                        PageRequest.of(page, 20));
    }

    // Cache trending — 1 hour TTL
    @Cacheable(value = "trending",
            key = "'today'")
    public List<Article> getTrending() {
        return articleRepository
                .findTop10ByOrderByViewCountDesc();
    }

    // Cache search results — 5 min TTL
    @Cacheable(value = "search",
            key = "#keyword")
    public List<Article> search(String keyword) {
        return articleRepository
                .findByTitleContainingOrContentContaining(
                        keyword, keyword);
    }

    // Evict cache when new article saved
    @CacheEvict(value = "news",
            allEntries = true)
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    // Track article view + update trending
    public void trackView(Long articleId) {
        String key = "views:" + articleId;
        redisTemplate.opsForValue()
                .increment(key);

        // Update trending ZSet
        redisTemplate.opsForZSet()
                .incrementScore("trending:today",
                        articleId.toString(), 1.0);
    }

    // Get trending from Redis ZSet
    public List<String> getTrendingIds() {
        return redisTemplate.opsForZSet()
                .reverseRange("trending:today", 0, 9)
                .stream()
                .map(Object::toString)
                .toList();
    }
}