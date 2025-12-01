package org.example.security.user;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {

        log.debug("Authenticating user");

        User currentUser = userService.findByEmail().orElseThrow();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("")
    public ResponseEntity<List<User>> allUsers() {

        List<User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }
}
