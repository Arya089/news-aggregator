package news_aggregator.controller;

import news_aggregator.dto.AdminStats;
import news_aggregator.dto.UserSummary;
import news_aggregator.model.Source;
import news_aggregator.service.AdminService;
import news_aggregator.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private SourceService sourceService;

    @GetMapping("/users")
    public ResponseEntity<List<UserSummary>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStats> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @PostMapping("/sources")
    public ResponseEntity<Source> createSource(@RequestBody Source source) {
        return ResponseEntity.ok(sourceService.createSource(source));
    }

    @DeleteMapping("/sources/{id}")
    public ResponseEntity<?> deleteSource(@PathVariable Long id) {
        try {
            sourceService.deleteSource(id);
            return ResponseEntity.ok("Source deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}