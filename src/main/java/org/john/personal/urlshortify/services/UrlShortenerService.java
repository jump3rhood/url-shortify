package org.john.personal.urlshortify.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.john.personal.urlshortify.dto.response.UrlStatsResponse;
import org.john.personal.urlshortify.models.Url;
import org.john.personal.urlshortify.models.User;
import org.john.personal.urlshortify.exception.UrlNotFoundException;
import org.john.personal.urlshortify.repositories.UrlRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UrlShortenerService {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    private final UrlRepository urlRepository;

    public UrlShortenerService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    /*
    * Takes an original long url and creates a unique short url
    * If long url is already present, returns the previous unique short url
    * TODO:
    * - if short url is expired, how should we handle it
    * */
    public @NotBlank String shorten(String longUrl, User user) {
        // check if url already exists for this long url
        Optional<Url> optional = urlRepository.findByLongUrl(longUrl);
        if (optional.isPresent()) {
            return optional.get().getShortUrl();
        }

        // create a unique short url
        Url entity = Url.builder()
                .longUrl(longUrl)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        // save to get ID
        urlRepository.save(entity);

        String shortCode = toBase62(entity.getId());
        entity.setShortUrl(shortCode);

        //update with short-url
        Url saved = urlRepository.save(entity);
        return shortCode;
    }

    public String expand(String shortCode){
        Optional<Url> optional = urlRepository.findByShortUrl(shortCode);
        return optional.map(Url::getLongUrl)
                .orElseThrow(() -> new UrlNotFoundException("this url is invalid"));
    }

    public Url getUrlFromShortCode(String shortCode) {
        Optional<Url> optional = urlRepository.findByShortUrl(shortCode);
        return optional.orElseThrow(() -> new UrlNotFoundException("this url is invalid"));
    }

    private String toBase62(long num) {
        if (num == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.insert(0, BASE62_CHARS.charAt((int) (num % BASE)));
            num /= BASE;
        }
        // if code length > 8 characters, limit to 8 characters only
        if(sb.length() > 8)
            return sb.substring(0,8);
        return sb.toString();
    }

    public List<Url> findAllUrlsByUser(User user){
        List<Url> urls = urlRepository.findByUserOrderByCreatedAtDesc(user);
        return urls;
    }

    public UrlStatsResponse getUrlStats(String shortCode, User signedInUser) {
        Url url = getUrlFromShortCode(shortCode);
        // owner of url
        User owner = url.getUser();
        if (!owner.getId().equals(signedInUser.getId())) {
            throw new UrlNotFoundException("this url is invalid or you do not have access to it");
        }
        return UrlStatsResponse.builder()
                .longUrl(url.getLongUrl())
                .shortUrl(url.getShortUrl())
                .clickCount(url.getClicks().size())
                .build();
    }

    public void deleteUrl(String shortCode) {
        urlRepository.deleteByShortUrl(shortCode);
    }
}
