package dev.lunov.eng.security;

import dev.lunov.eng.security.token.TokenService;
import dev.lunov.eng.security.token.dto.TokenResponseDTO;
import dev.lunov.eng.security.user.User;
import dev.lunov.eng.security.user.UserService;
import dev.lunov.eng.security.user.dto.UserCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@ResponseBody
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final TokenService tokenService;
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    public User registration(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        log.debug("Registration User: {}", userCreateDTO);
        return userService.save(userCreateDTO);
    }

    @PostMapping("/login")
    public TokenResponseDTO token(Authentication authentication) {
        log.info("Login User: {}", authentication.getPrincipal());
        return this.tokenService.generateTokenResponse(authentication);
    }
}
