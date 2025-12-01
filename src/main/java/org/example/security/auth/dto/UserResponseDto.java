package org.example.security.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.example.security.user.Role;

import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
