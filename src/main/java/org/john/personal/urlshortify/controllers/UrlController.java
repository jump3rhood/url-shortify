package org.john.personal.urlshortify.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.john.personal.urlshortify.dto.request.UrlRequest;
import org.john.personal.urlshortify.dto.response.UrlStatsResponse;
import org.john.personal.urlshortify.models.Url;
import org.john.personal.urlshortify.models.User;
import org.john.personal.urlshortify.security.annotation.CurrentUser;
import org.john.personal.urlshortify.services.ClickService;
import org.john.personal.urlshortify.services.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
@Slf4j
@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlShortenerService shortenerService;
    private final ClickService clickService;

    @GetMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Welcome to Urly_short");
    }

    @PostMapping("/api/urls/")
    public ResponseEntity<String> shortenUrl(@RequestBody UrlRequest requestDto, @CurrentUser User user) {
        log.info(requestDto.getLongUrl());
        // saves resource entity and returns short code
        String shortCode = shortenerService.shorten(requestDto.getLongUrl(), user);
        // returns short link
        return new ResponseEntity<>(buildShortUrl(shortCode), HttpStatus.CREATED);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode, HttpServletRequest request){
        Url foundUrl = shortenerService.getUrlFromShortCode(shortCode);
        // Create & save click
        clickService.save(foundUrl, request);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(foundUrl.getLongUrl()))
                .build();
    }

    @GetMapping("/api/urls/{shortCode}/stats")
    public ResponseEntity<UrlStatsResponse> getUrlStats(
            @PathVariable String shortCode,
            @CurrentUser User user) {
        System.out.println("Shortcode: ++: " + shortCode);
        log.debug("Getting stats for shortCode: {}", shortCode);
        UrlStatsResponse stats = shortenerService.getUrlStats(shortCode, user);
        log.debug("Stats response: {}", stats);
        return ResponseEntity.ok(stats);
    }


    //    public RedirectView resolveURL(@PathVariable String shortCode) {
//        var resource = resolver.resolve(shortCode);
//        return new RedirectView(resource.getOriginalUrl());
//    }
    private String buildShortUrl(String shortCode) {
        // In production, get this from configuration
        return "http://localhost:8080/" + shortCode;
    }
}
