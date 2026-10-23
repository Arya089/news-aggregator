package news_aggregator.controller;

import news_aggregator.model.Article;
import news_aggregator.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    @Autowired
    private ArticleService articleService;

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping
    public ResponseEntity<?> getBookmarks() {
        try {
            List<Article> bookmarks = articleService.getBookmarkedArticles(currentUsername());
            return ResponseEntity.ok(bookmarks);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/{articleId}")
    public ResponseEntity<?> addBookmark(@PathVariable Long articleId) {
        try {
            articleService.bookmarkArticle(currentUsername(), articleId);
            return ResponseEntity.ok("Article bookmarked");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> removeBookmark(@PathVariable Long articleId) {
        try {
            articleService.removeBookmark(currentUsername(), articleId);
            return ResponseEntity.ok("Bookmark removed");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}