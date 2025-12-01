package org.example.security.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    USER(1, "USER"),
    ADMIN(2, "ADMIN");

    private final int value;
    private final String name;
}
