package news_aggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminStats {
    private long totalUsers;
    private long totalArticles;
    private long totalSources;
}