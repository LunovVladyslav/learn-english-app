package dev.lunov.eng.security.token.dto;

import java.util.Map;

public record TokenResponseDTO(
        Map<String, TokenDetailsDTO> response
) {
}
