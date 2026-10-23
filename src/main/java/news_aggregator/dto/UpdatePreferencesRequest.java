package news_aggregator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePreferencesRequest {
    private String preferredCategories; // comma-separated, e.g. "Technology,Sports"
}