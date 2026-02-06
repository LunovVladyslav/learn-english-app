package dev.lunov.eng.security.token.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record TokenDetailsDTO(
        String hash,
        String type,
        Instant issuedAt,
        Instant expiredAt
) {
}
