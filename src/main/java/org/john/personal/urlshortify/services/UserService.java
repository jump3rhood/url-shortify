package org.john.personal.urlshortify.services;

import lombok.RequiredArgsConstructor;
import org.john.personal.urlshortify.models.User;
import org.john.personal.urlshortify.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user with id " + userId+ " not found"));
    }
}
