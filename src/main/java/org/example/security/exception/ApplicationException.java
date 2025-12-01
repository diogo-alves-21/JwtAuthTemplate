package org.example.security.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ApplicationExceptionCode error;

    public ApplicationException(ApplicationExceptionCode error) {
        super(error.getErrorMessage());
        this.error = error;
    }

}
