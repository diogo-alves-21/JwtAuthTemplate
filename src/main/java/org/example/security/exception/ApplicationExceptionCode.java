package org.example.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApplicationExceptionCode {

    USER_NOT_FOUND(1001, "This user does not exist", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXIST(1002, "This user already exists", HttpStatus.CONFLICT),
    ACCESS_DENIED(1003, "Access denied", HttpStatus.FORBIDDEN),
    ACCESS_TOKEN_INVALID(1004, "Access token is invalid", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_INVALID(1005, "Refresh token is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_RESET_TOKEN_INVALID(1006, "Password reset token is invalid or has expired", HttpStatus.BAD_REQUEST),
    LOGIN_INVALID(1007, "Login invalid", HttpStatus.BAD_REQUEST);

    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;
}