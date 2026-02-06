package dev.lunov.eng.ai;

import dev.lunov.eng.ai.dto.WordDTO;

import java.util.List;

public interface AiService {
    List<WordDTO> getAlternativeWaysOpenAi(String name, String word, int num);
}
