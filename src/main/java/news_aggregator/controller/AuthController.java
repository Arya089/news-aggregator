package news_aggregator.controller;

import news_aggregator.dto.LoginRequest;
import news_aggregator.dto.LoginResponse;
import news_aggregator.dto.RefreshTokenRequest;
import news_aggregator.dto.RegisterRequest;
import news_aggregator.model.User;
import news_aggregator.security.JwtUtil;
import news_aggregator.security.TokenBlacklistService;
import news_aggregator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User newUser = userService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword()
            );
            return ResponseEntity.ok("User registered successfully with ID: " + newUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.validateCredentials(
                    request.getUsername(),
                    request.getPassword()
            );
            String accessToken = jwtUtil.generateAccessToken(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
            return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklist(token);
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String username = jwtUtil.extractUsername(request.getRefreshToken());

            if (!jwtUtil.isTokenValid(request.getRefreshToken(), username)) {
                return ResponseEntity.status(401).body("Invalid or expired refresh token");
            }

            String newAccessToken = jwtUtil.generateAccessToken(username);
            return ResponseEntity.ok(new LoginResponse(newAccessToken, request.getRefreshToken()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> whoAmI() {
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        return ResponseEntity.ok("You are logged in as: " + username);
    }
}