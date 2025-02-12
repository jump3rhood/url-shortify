package org.john.personal.urlshortify.dto;

import lombok.Builder;
import lombok.Data;
import org.john.personal.urlshortify.models.Role;
import org.john.personal.urlshortify.models.User;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private Set<Role> roles;
    private LocalDateTime createdAt;

    public static UserDTO fromUser(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .build();

    }

}
