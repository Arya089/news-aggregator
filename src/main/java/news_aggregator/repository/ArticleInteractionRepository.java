package news_aggregator.repository;

import news_aggregator.model.ArticleInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleInteractionRepository extends JpaRepository<ArticleInteraction, Long> {

    Optional<ArticleInteraction> findByUserIdAndArticleId(Long userId, Long articleId);

    List<ArticleInteraction> findByUserIdAndBookmarkedTrue(Long userId);
}