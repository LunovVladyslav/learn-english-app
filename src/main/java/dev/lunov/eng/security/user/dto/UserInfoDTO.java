package dev.lunov.eng.security.user.dto;

public record UserInfoDTO(
        String fullName,
        String email,
        String role
) {
}
