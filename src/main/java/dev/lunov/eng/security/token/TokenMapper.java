package dev.lunov.eng.security.token;

import dev.lunov.eng.security.token.dto.TokenDetailsDTO;
import org.springframework.stereotype.Component;

@Component
public class TokenMapper {

    public TokenDetailsDTO toDTO(Token token) {
        return new TokenDetailsDTO(
                token.getTokenHash(),
                token.getTokenType().name(),
                token.getIssuedAt(),
                token.getExpiredAt()
        );
    }
}
