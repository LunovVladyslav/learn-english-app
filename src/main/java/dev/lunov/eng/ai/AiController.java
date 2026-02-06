package dev.lunov.eng.ai;

import dev.lunov.eng.ai.dto.WordDTO;
import dev.lunov.eng.ai.impl.AiServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiServiceImpl aiService;

    @GetMapping("/ways/openrouter")
    public List<WordDTO> getWaysOpenRouter(
            Authentication auth,
            @RequestParam(name = "word") String word,
            @RequestParam(name = "num")  int num
    ) {
        return aiService.getAlternativeWaysOpenAi(auth.getName(), word, num);
    }
}
