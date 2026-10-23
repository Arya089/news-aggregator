package news_aggregator.controller;

import news_aggregator.model.Source;
import news_aggregator.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sources")
public class SourceController {

    @Autowired
    private SourceService sourceService;

    @GetMapping
    public ResponseEntity<List<Source>> getAllSources() {
        return ResponseEntity.ok(sourceService.getAllSources());
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<Source>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(sourceService.getSourcesByCategory(category));
    }
}