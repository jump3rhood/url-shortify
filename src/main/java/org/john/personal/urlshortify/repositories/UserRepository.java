package org.john.personal.urlshortify.repositories;

import org.john.personal.urlshortify.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
