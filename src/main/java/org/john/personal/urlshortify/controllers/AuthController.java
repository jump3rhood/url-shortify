package org.john.personal.urlshortify.controllers;

import lombok.RequiredArgsConstructor;
import org.john.personal.urlshortify.dto.request.LoginRequest;
import org.john.personal.urlshortify.dto.request.SignupRequest;
import org.john.personal.urlshortify.models.User;
import org.john.personal.urlshortify.repositories.UserRepository;
import org.john.personal.urlshortify.services.TokenBlackListService;
import org.john.personal.urlshortify.utils.BCryptUtil;
import org.john.personal.urlshortify.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptUtil bCryptUtil;
    private final TokenBlackListService tokenBlackListService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity
                    .badRequest()
                    .body("Email already in use");
        }

        // Create new user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(bCryptUtil.hashPassword(request.getPassword()));
        userRepository.save(user);

        // Generate JWT
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(!bCryptUtil.checkPassword(request.getPassword(), user.getPassword())){
            return ResponseEntity
                    .badRequest()
                    .body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(token);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader){
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            //  Blacklist the token
            tokenBlackListService.blackListToken(authHeader.substring(7));
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }
}
