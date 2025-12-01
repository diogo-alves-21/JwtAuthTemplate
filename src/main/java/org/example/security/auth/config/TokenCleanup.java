package org.example.security.auth.config;

import org.example.security.user.User;
import org.example.security.user.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@EnableScheduling
@Configuration
public class TokenCleanup {

    private final UserRepository userRepository;

    public TokenCleanup(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 3_600_000)
    public void cleanupTokens() {

        List<User> users = userRepository.findByResetPasswordTokenIsNotNull();

        users.forEach(user -> {
            user.setResetPasswordToken(null);
            user.setResetPasswordExpiresAt(null);
            userRepository.save(user);
        });
    }
}
