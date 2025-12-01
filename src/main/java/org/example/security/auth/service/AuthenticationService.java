package org.example.security.auth.service;

import static org.example.security.exception.ApplicationExceptionCode.*;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.example.security.auth.dto.*;
import org.example.security.auth.model.Token;
import org.example.security.auth.repository.TokenRepository;
import org.example.security.exception.ApplicationException;
import org.example.security.user.Role;
import org.example.security.user.User;
import org.example.security.user.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final TokenRepository tokenRepository;

    private final JwtService jwtService;

    private final EmailService emailService;

    public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder, TokenRepository tokenRepository, JwtService jwtService,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Transactional
    public UserResponseDto signup(RegisterUserDto registerUserDto) {

        if (userRepository.findByEmail(registerUserDto.getEmail()).isPresent()) {

            throw new ApplicationException(USER_ALREADY_EXIST);
        }

        User user = new User();
        user.setEmail(registerUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setFirstName(registerUserDto.getFirstName());
        user.setLastName(registerUserDto.getLastName());
        user.setRole(Role.USER);

        user = userRepository.save(user);

        return UserResponseDto.builder().id(user.getId()).email(user.getEmail()).firstName(user.getFirstName())
                .lastName(user.getLastName()).role(user.getRole()).build();
    }

    @Transactional
    public TokenResponseDto authenticate(LoginUserDto loginUserDto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(), loginUserDto.getPassword()));

        User user = userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllTokenByUser(user);
        saveUserToken(accessToken, refreshToken, user);

        return TokenResponseDto.builder().accessToken(accessToken).expiresAt(jwtService.getExpirationTime()).build();
    }

    public TokenResponseDto refreshToken(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            throw new ApplicationException(REFRESH_TOKEN_INVALID);
        }

        String token = authHeader.substring(7);

        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

        if (jwtService.isValidRefreshToken(token, user)) {

            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllTokenByUser(user);
            saveUserToken(accessToken, refreshToken, user);

            return TokenResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken)
                    .expiresAt(jwtService.getRefreshExpirationTime()).build();
        }

        throw new ApplicationException(REFRESH_TOKEN_INVALID);
    }

    @Transactional
    public void processPasswordReset(String email) throws MessagingException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

        String token = createResetPasswordToken();

        user.setResetPasswordToken(token);
        user.setResetPasswordExpiresAt(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        emailService.sendEmail(email, token);
    }

    public void validatePasswordResetToken(String token) {

        Optional<User> userToReset = userRepository.findByResetPasswordToken(token);

        if (userToReset.isEmpty() || userToReset.get().getResetPasswordExpiresAt().isBefore(LocalDateTime.now())) {

            throw new ApplicationException(PASSWORD_RESET_TOKEN_INVALID);
        }
    }

    @Transactional
    public void updatePassword(ResetPasswordDto request) {

        Optional<User> userToReset = userRepository.findByResetPasswordToken(request.getToken());

        if (userToReset.isEmpty() || userToReset.get().getResetPasswordExpiresAt().isBefore(LocalDateTime.now())) {

            throw new ApplicationException(PASSWORD_RESET_TOKEN_INVALID);
        }

        User user = userToReset.get();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiresAt(null);
        userRepository.save(user);
    }

    private void revokeAllTokenByUser(User user) {

        List<Token> validTokens = tokenRepository.findAllAccessTokensByUser(user.getId());

        if (validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t -> t.setLoggedOut(true));
        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {

        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    private String createResetPasswordToken() {

        byte[] randomBytes = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(randomBytes);
    }
}
