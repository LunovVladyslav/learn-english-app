package dev.lunov.eng;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

//    @GetMapping("/ten-ways/ollama")
//    public List<WordDTO> getWaysOllama(@RequestParam(name = "word") String word) {
//        return aiService.getTenWaysToUseWord(word);
//    }

    @GetMapping("/ten-ways/openrouter")
    public List<WordDTO> getWaysOpenRouter(@RequestParam(name = "word") String word) {
        return aiService.getFiveWaysOpenAi(word);
    }
}
