package news_aggregator.controller;

import news_aggregator.model.Article;
import news_aggregator.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private ArticleService articleService;

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Article>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(articleService.getArticlesByCategory(category));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Article>> getTrending() {
        return ResponseEntity.ok(articleService.getTrending());
    }

    @GetMapping("/feed")
    public ResponseEntity<?> getFeed() {
        try {
            return ResponseEntity.ok(articleService.getFeedForUser(currentUsername()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(articleService.getArticleById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Article>> search(@RequestParam String q) {
        return ResponseEntity.ok(articleService.searchByTitle(q));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            articleService.markAsRead(currentUsername(), id);
            return ResponseEntity.ok("Marked as read");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeArticle(@PathVariable Long id) {
        try {
            articleService.likeArticle(currentUsername(), id);
            return ResponseEntity.ok("Article liked");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/bookmark")
    public ResponseEntity<?> bookmarkArticle(@PathVariable Long id) {
        try {
            articleService.bookmarkArticle(currentUsername(), id);
            return ResponseEntity.ok("Article bookmarked");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}