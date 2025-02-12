package org.john.personal.urlshortify.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.john.personal.urlshortify.dto.UserDTO;
import org.john.personal.urlshortify.dto.request.LoginRequest;
import org.john.personal.urlshortify.dto.request.SignupRequest;
import org.john.personal.urlshortify.exception.UserAlreadyExistsException;
import org.john.personal.urlshortify.exception.UserDoesNotExistException;
import org.john.personal.urlshortify.models.Role;
import org.john.personal.urlshortify.models.User;
import org.john.personal.urlshortify.repositories.UserRepository;
import org.john.personal.urlshortify.security.annotation.RequireRole;
import org.john.personal.urlshortify.services.TokenBlackListService;
import org.john.personal.urlshortify.services.UserService;
import org.john.personal.urlshortify.utils.BCryptUtil;
import org.john.personal.urlshortify.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptUtil bCryptUtil;
    private final TokenBlackListService tokenBlackListService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) throws UserAlreadyExistsException {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Email already in use");
        }

        // Create new user
        User user = userService.createUser(request.getFullName(), request.getEmail(), bCryptUtil.hashPassword(request.getPassword()));

        // Generate JWT
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(token);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));
        if (!bCryptUtil.checkPassword(request.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            //  Blacklist the token
            tokenBlackListService.blackListToken(authHeader.substring(7));
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }


    @PostMapping("/admin/create")
    @RequireRole(Role.ADMIN)
    public ResponseEntity<?> createAdmin(@Valid @RequestBody SignupRequest request, @RequestHeader("Authorization") String authHeader) {
        User adminUser = userService.createAdminUser(
                request.getFullName(),
                request.getEmail(),
                bCryptUtil.hashPassword(request.getPassword())
        );
        return ResponseEntity.ok(UserDTO.fromUser(adminUser));
    }

    @PutMapping("/admin/promote/{userId}")
    @RequireRole(Role.ADMIN)
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long userId) {
        User promotedUser = userService.promoteToAdmin(userId);

        return ResponseEntity.ok(Map.of(
                "user", UserDTO.fromUser(promotedUser),
                "message", "User promoted to admin. Please logout and login again to see changes."
        ));
    }

    @PutMapping("/admin/demote/{userId}")
    @RequireRole(Role.ADMIN)
    public ResponseEntity<?> removeAdminRole(@PathVariable Long userId) {
        User demotedUser = userService.removeAdminRole(userId);
        return ResponseEntity.ok(Map.of(
                "user", UserDTO.fromUser(demotedUser),
                "message", "Admin demoted to User role. Please request user with userId " + userId +" to login again."
        ));
    }
}

