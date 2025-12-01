package org.example.security.auth.dto;

import lombok.Data;

@Data
public class LoginResponseDto {

    private String token;
    private long expireTime;
}
