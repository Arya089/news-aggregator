package news_aggregator.service;

import news_aggregator.model.Article;
import news_aggregator.model.ArticleInteraction;
import news_aggregator.model.User;
import news_aggregator.repository.ArticleInteractionRepository;
import news_aggregator.repository.ArticleRepository;
import news_aggregator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleInteractionRepository interactionRepository;

    @Autowired
    private UserRepository userRepository;

    // ================= Existing methods (kept as-is) =================

    public List<Article> getByCategory(String category, Pageable pageable) {
        return articleRepository.findByCategoryOrderByPublishedAtDesc(category, pageable);
    }

    @Cacheable("trending")
    public List<Article> getTrending() {
        return articleRepository.findTop10ByOrderByViewCountDesc();
    }

    public List<Article> search(String keyword) {
        return articleRepository.findByTitleContainingOrContentContaining(keyword, keyword);
    }

    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    public void trackView(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);
    }

    public List<String> getTrendingIds() {
        return getTrending().stream()
                .map(a -> a.getId().toString())
                .collect(Collectors.toList());
    }

    // ================= New methods required by controllers =================

    public Article createArticle(Article article) {
        if (article.getUrl() != null && articleRepository.existsByUrl(article.getUrl())) {
            throw new RuntimeException("Article with this URL already exists");
        }
        if (article.getCreatedAt() == null) {
            article.setCreatedAt(LocalDateTime.now());
        }
        if (article.getFetchedAt() == null) {
            article.setFetchedAt(LocalDateTime.now());
        }
        return articleRepository.save(article);
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));
    }

    public List<Article> getArticlesByCategory(String category) {
        return getByCategory(category, PageRequest.of(0, 20));
    }

    public List<Article> searchByTitle(String keyword) {
        return search(keyword);
    }

    public Article updateArticle(Long id, Article updated) {
        Article existing = getArticleById(id);
        existing.setTitle(updated.getTitle());
        existing.setContent(updated.getContent());
        existing.setCategory(updated.getCategory());
        existing.setAuthor(updated.getAuthor());
        existing.setSourceUrl(updated.getSourceUrl());
        existing.setUrl(updated.getUrl());
        existing.setSource(updated.getSource());
        existing.setPublishedAt(updated.getPublishedAt());
        return articleRepository.save(existing);
    }

    @CacheEvict(value = "trending", allEntries = true)
    public void deleteArticle(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new RuntimeException("Article not found with id: " + id);
        }
        articleRepository.deleteById(id);
    }

    // ---------- User lookup helper ----------

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    private ArticleInteraction getOrCreateInteraction(Long userId, Long articleId) {
        return interactionRepository.findByUserIdAndArticleId(userId, articleId)
                .orElseGet(() -> {
                    ArticleInteraction interaction = new ArticleInteraction();
                    interaction.setUserId(userId);
                    interaction.setArticleId(articleId);
                    return interaction;
                });
    }

    // ---------- Bookmarks ----------

    public List<Article> getBookmarkedArticles(String username) {
        User user = getUserByUsername(username);
        List<Long> articleIds = interactionRepository.findByUserIdAndBookmarkedTrue(user.getId())
                .stream()
                .map(ArticleInteraction::getArticleId)
                .collect(Collectors.toList());
        return articleRepository.findAllById(articleIds);
    }

    public void bookmarkArticle(String username, Long articleId) {
        User user = getUserByUsername(username);
        getArticleById(articleId); // validates article exists
        ArticleInteraction interaction = getOrCreateInteraction(user.getId(), articleId);
        interaction.setBookmarked(true);
        interactionRepository.save(interaction);
    }

    public void removeBookmark(String username, Long articleId) {
        User user = getUserByUsername(username);
        ArticleInteraction interaction = interactionRepository
                .findByUserIdAndArticleId(user.getId(), articleId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        interaction.setBookmarked(false);
        interactionRepository.save(interaction);
    }

    // ---------- Feed ----------

    public List<Article> getFeedForUser(String username) {
        User user = getUserByUsername(username);
        String preferred = user.getPreferredCategories();

        if (preferred == null || preferred.isBlank()) {
            return getTrending();
        }

        List<String> categories = Arrays.stream(preferred.split(","))
                .map(String::trim)
                .filter(c -> !c.isEmpty())
                .collect(Collectors.toList());

        return categories.stream()
                .flatMap(category -> getByCategory(category, PageRequest.of(0, 10)).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    // ---------- Read / Like ----------

    public void markAsRead(String username, Long articleId) {
        User user = getUserByUsername(username);
        Article article = getArticleById(articleId);
        ArticleInteraction interaction = getOrCreateInteraction(user.getId(), articleId);

        if (!interaction.isRead()) {
            interaction.setRead(true);
            interactionRepository.save(interaction);
            article.setReadCount(article.getReadCount() + 1);
            articleRepository.save(article);
        }
    }

    @CacheEvict(value = "trending", allEntries = true)
    public void likeArticle(String username, Long articleId) {
        User user = getUserByUsername(username);
        Article article = getArticleById(articleId);
        ArticleInteraction interaction = getOrCreateInteraction(user.getId(), articleId);

        if (!interaction.isLiked()) {
            interaction.setLiked(true);
            interactionRepository.save(interaction);
            article.setLikeCount(article.getLikeCount() + 1);
            articleRepository.save(article);
        }
    }

    // ---------- Search suggestions ----------

    public List<String> getSearchSuggestions(String query) {
        return search(query).stream()
                .map(Article::getTitle)
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
    }
}