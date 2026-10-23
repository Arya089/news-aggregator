package news_aggregator.service;

import news_aggregator.dto.AdminStats;
import news_aggregator.dto.UserSummary;
import news_aggregator.repository.ArticleRepository;
import news_aggregator.repository.SourceRepository;
import news_aggregator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private SourceRepository sourceRepository;

    public List<UserSummary> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserSummary(u.getId(), u.getUsername(), u.getEmail(), u.getRole(), u.getCreatedAt()))
                .toList();
    }

    public AdminStats getStats() {
        return new AdminStats(
                userRepository.count(),
                articleRepository.count(),
                sourceRepository.count()
        );
    }
}