package org.example.security.auth.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.example.security.auth.dto.*;
import org.example.security.auth.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody RegisterUserDto registerUserDto) {

        UserResponseDto user = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginUserDto loginUserDto) {

        TokenResponseDto token = authenticationService.authenticate(loginUserDto);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody ResetPasswordDto request)
            throws MessagingException {

        authenticationService.processPasswordReset(request.getEmail());

        return ResponseEntity.ok("A reset password request was sent to your email");
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> validateToken(@Valid @RequestParam("token") String token) {

        authenticationService.validatePasswordResetToken(token);

        return ResponseEntity.ok("Token has been validated");
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDto request) {

        authenticationService.updatePassword(request);

        return ResponseEntity.ok("Password updated");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponseDto> refreshToken(HttpServletRequest request) {

        TokenResponseDto tokenResponseDto = authenticationService.refreshToken(request);

        return ResponseEntity.ok(tokenResponseDto);
    }
}
