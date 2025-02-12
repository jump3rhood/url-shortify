package org.john.personal.urlshortify.controllers;

import lombok.RequiredArgsConstructor;
import org.john.personal.urlshortify.models.Url;
import org.john.personal.urlshortify.models.User;
import org.john.personal.urlshortify.security.annotation.CurrentUser;
import org.john.personal.urlshortify.services.UrlShortenerService;
import org.john.personal.urlshortify.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UrlShortenerService urlShorterenerService;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@CurrentUser User singedInUser) {

        List<Url> urls = urlShorterenerService.findAllUrlsByUser(singedInUser);

        Map<String, Object> response = new HashMap<>();
        response.put("fullname", singedInUser.getFullName());
        response.put("email", singedInUser.getEmail());
        response.put("urls", urls.stream().map(url -> Map.of(
                "originalUrl", url.getLongUrl(),
                "shortUrl", url.getShortUrl(),
                "clickCount", url.getClicks().size()
        )).collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

}
