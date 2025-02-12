package org.john.personal.urlshortify.services;

import lombok.RequiredArgsConstructor;
import org.john.personal.urlshortify.dto.request.SignupRequest;
import org.john.personal.urlshortify.exception.UserAlreadyExistsException;
import org.john.personal.urlshortify.exception.UserDoesNotExistException;
import org.john.personal.urlshortify.models.Role;
import org.john.personal.urlshortify.models.User;
import org.john.personal.urlshortify.repositories.UserRepository;
import org.john.personal.urlshortify.security.annotation.RequireRole;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // Create a new user with the given details
    public User createUser(String fullname, String email, String hashedPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setFullName(fullname);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public User findById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserDoesNotExistException("User with id " + userId + " does not exist");
        }
        return optionalUser.get();
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // Method to create an admin user (used by existing admin)
    @RequireRole(Role.ADMIN)
    public User createAdminUser(String fullname, String email, String hashedPassword) throws UserAlreadyExistsException {
        User user = createUser(fullname, email, hashedPassword); // Create regular user first

        // Add ADMIN role
        Set<Role> roles = user.getRoles();
        roles.add(Role.ADMIN);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    // Method to promote existing user to admin
    @RequireRole(Role.ADMIN)
    public User promoteToAdmin(Long userId) {
        User user = findById(userId);

        Set<Role> roles = user.getRoles();
        roles.add(Role.ADMIN);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    // Method to remove admin privileges
    @RequireRole(Role.ADMIN)
    public User removeAdminRole(Long userId) {
        User user = findById(userId);

        Set<Role> roles = user.getRoles();
        roles.remove(Role.ADMIN);

        // Ensure user keeps USER role
        roles.add(Role.USER);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
