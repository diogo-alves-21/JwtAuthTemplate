package org.example.security.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.security.auth.model.Token;
import org.example.security.auth.repository.TokenRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;

    public CustomLogoutHandler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {

            return;
        }

        String token = header.substring(7);

        Token storedToken = tokenRepository.findByAccessToken(token).orElse(null);

        if (storedToken != null) {

            storedToken.setLoggedOut(true);
            tokenRepository.save(storedToken);
        }
    }
}
