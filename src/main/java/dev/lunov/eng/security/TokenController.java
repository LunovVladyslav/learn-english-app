package dev.lunov.eng.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@ResponseBody
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public String token(Authentication authentication) {
        return this.tokenService.generate(authentication);
    }
}
