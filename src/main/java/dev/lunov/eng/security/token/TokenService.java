package dev.lunov.eng.security.token;

import dev.lunov.eng.security.token.dto.TokenResponseDTO;
import dev.lunov.eng.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.lunov.eng.security.token.TokenType.*;

@Service
@RequiredArgsConstructor
public class TokenService {

    public final Long ACCESS_TOKEN_EXPIRATION_TIME = 3600L;
    public final Long REFRESH_TOKEN_EXPIRATION_TIME = 86400L;
    public final Long PASSWORD_TOKEN_EXPIRATION_TIME = 1800L;

    private final JwtEncoder encoder;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final TokenMapper tokenMapper;

    public TokenResponseDTO generateTokenResponse(Authentication auth) {
        var now = Instant.now();
        var details = Map.of(
                "access_token", tokenMapper.toDTO(createToken(auth, now, ACCESS_TOKEN, ACCESS_TOKEN_EXPIRATION_TIME)),
                "refresh_token", tokenMapper.toDTO(createToken(auth, now, REFRESH_TOKEN, REFRESH_TOKEN_EXPIRATION_TIME))
        );
        return new TokenResponseDTO(details);
    }


    private Token createToken(Authentication authentication, Instant now, TokenType tokenType, Long expirationTime) {
        var tokenHash = generateHash(authentication, now, expirationTime);
        var user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        var token = Token.builder()
                .tokenHash(tokenHash)
                .tokenType(tokenType)
                .user(user)
                .used(false)
                .issuedAt(now)
                .expiredAt(now.plusSeconds(expirationTime))
                .build();

        return tokenRepository.save(token);
    }

    private String generateHash(Authentication authentication, Instant now, long expiry) {
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


}
