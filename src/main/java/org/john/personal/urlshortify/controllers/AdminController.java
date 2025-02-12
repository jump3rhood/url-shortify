package org.john.personal.urlshortify.controllers;

import lombok.RequiredArgsConstructor;
import org.john.personal.urlshortify.dto.UserDTO;
import org.john.personal.urlshortify.models.Role;
import org.john.personal.urlshortify.models.Url;
import org.john.personal.urlshortify.models.User;
import org.john.personal.urlshortify.security.annotation.RequireRole;
import org.john.personal.urlshortify.services.UrlShortenerService;
import org.john.personal.urlshortify.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
/*
* 1. View all users
* 2. View all shortened URLs for a particular user
* 3. Delete a particular url
*
* */


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final UrlShortenerService urlShortenerService;


    @GetMapping("/users")
    @RequireRole(Role.ADMIN)
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> users = userService.findAllUsers().stream().map(UserDTO::fromUser).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}/urls")
    @RequireRole(Role.ADMIN)
    public ResponseEntity<List<Url>> getUserUrls(@PathVariable Long userId) {
        User user = userService.findById(userId);
        List<Url> urls = urlShortenerService.findAllUrlsByUser(user);
        return ResponseEntity.ok(urls);
    }

    @DeleteMapping("/urls/{shortCode}")
    @RequireRole(Role.ADMIN)
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode){
        urlShortenerService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

}
