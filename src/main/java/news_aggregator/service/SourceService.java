package news_aggregator.service;

import news_aggregator.model.Source;
import news_aggregator.repository.SourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SourceService {

    @Autowired
    private SourceRepository sourceRepository;

    public List<Source> getAllSources() {
        return sourceRepository.findAll();
    }

    public List<Source> getSourcesByCategory(String category) {
        return sourceRepository.findByCategory(category);
    }

    public Source createSource(Source source) {
        return sourceRepository.save(source);
    }

    public void deleteSource(Long id) {
        if (!sourceRepository.existsById(id)) {
            throw new RuntimeException("Source not found with ID: " + id);
        }
        sourceRepository.deleteById(id);
    }
}