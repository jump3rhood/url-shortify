package org.john.personal.urlshortify.repositories;

import org.john.personal.urlshortify.models.Url;
import org.john.personal.urlshortify.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    List<Url> findAll();
    Boolean existsByShortUrl(String shortUrl);
    Optional<Url> findByShortUrl(String shortUrl);
    Optional<Url> findByLongUrl(String longUrl);
    long count();
    List<Url> findByUserOrderByCreatedAtDesc(User user);
    void deleteByShortUrl(String shortUrl);
}
