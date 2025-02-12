package org.john.personal.urlshortify.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.john.personal.urlshortify.exception.UserAlreadyExistsException;
import org.john.personal.urlshortify.models.User;
import org.john.personal.urlshortify.services.UserService;
import org.john.personal.urlshortify.utils.BCryptUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitialiizer implements CommandLineRunner {
    private final UserService userService;
    private final BCryptUtil bCryptUtil;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;


    @Override
    public void run(String... args) throws Exception {
        try {
            User admin = userService.createAdminUser(
                    "System Admin",
                    email,
                    bCryptUtil.hashPassword(password)
            );
            log.info("Admin user created successfully with email: {}", email);

        } catch(UserAlreadyExistsException exception){
            log.info("Admin user already exists with email: {}", email);
        }catch (Exception e) {
            log.error("Failed to initialize admin user", e);
        }
    }
}
