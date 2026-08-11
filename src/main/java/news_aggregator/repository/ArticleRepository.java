package news_aggregator.repository;

import news_aggregator.model.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository
        extends JpaRepository<Article, Long> {

    List<Article> findByCategoryOrderByPublishedAtDesc(
            String category, Pageable pageable);

    List<Article> findTop10ByOrderByViewCountDesc();

    List<Article> findByTitleContainingOrContentContaining(
            String title, String content);

    boolean existsByUrl(String url);

    List<Article> findBySource(String source);
}