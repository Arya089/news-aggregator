package news_aggregator.controller;

import news_aggregator.dto.UpdatePreferencesRequest;
import news_aggregator.dto.UpdateProfileRequest;
import news_aggregator.model.User;
import news_aggregator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            User user = userService.getUserByUsername(currentUsername());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            User updated = userService.updateProfile(
                    currentUsername(),
                    request.getDisplayName(),
                    request.getBio()
            );
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/preferences")
    public ResponseEntity<?> getPreferences() {
        try {
            User user = userService.getUserByUsername(currentUsername());
            return ResponseEntity.ok(user.getPreferredCategories());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/preferences")
    public ResponseEntity<?> updatePreferences(@RequestBody UpdatePreferencesRequest request) {
        try {
            User updated = userService.updatePreferences(
                    currentUsername(),
                    request.getPreferredCategories()
            );
            return ResponseEntity.ok(updated.getPreferredCategories());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}