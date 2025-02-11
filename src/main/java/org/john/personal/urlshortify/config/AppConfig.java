package org.john.personal.urlshortify.config;

import org.john.personal.urlshortify.utils.BCryptUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public BCryptUtil bCryptUtil() {
        return new BCryptUtil();
    }
}
