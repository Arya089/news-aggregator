package news_aggregator.service;

import news_aggregator.model.User;
import news_aggregator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String username, String email, String rawPassword) {

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already taken");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("USER");

        return userRepository.save(user);
    }

    public User validateCredentials(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return user;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public User updateProfile(String username, String displayName, String bio) {
        User user = getUserByUsername(username);
        user.setDisplayName(displayName);
        user.setBio(bio);
        return userRepository.save(user);
    }

    public User updatePreferences(String username, String preferredCategories) {
        User user = getUserByUsername(username);
        user.setPreferredCategories(preferredCategories);
        return userRepository.save(user);
    }
}