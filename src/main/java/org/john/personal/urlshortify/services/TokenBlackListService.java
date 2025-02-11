package org.john.personal.urlshortify.services;

import lombok.AllArgsConstructor;
import org.john.personal.urlshortify.models.TokenBlackList;
import org.john.personal.urlshortify.repositories.TokenBlackListRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TokenBlackListService {
    private final TokenBlackListRepository tokenBlackListRepository;

    public void blackListToken(String token){
        TokenBlackList blackListedToken = new TokenBlackList();
        blackListedToken.setToken(token);
        blackListedToken.setBlackListedAt(LocalDateTime.now());
        tokenBlackListRepository.save(blackListedToken);
    }
}
