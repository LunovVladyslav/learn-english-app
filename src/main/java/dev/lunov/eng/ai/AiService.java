package dev.lunov.eng.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiService {

    private final ChatClient openRouter;
    private final BeanOutputConverter<List<WordDTO>> converter;

    private final Map<String, List<WordDTO>> cache;

    private static final String SYSTEM_PROMPT = """
            You are a professional English language expert.
            """;
    private static final String USER_PROMPT = """
             Generate exactly 5 alternative ways to express "%s".
            
            STRICT REQUIREMENTS:
            - Output ONLY valid JSON array
            - No explanations, no extra text
            - Each entry needs: word, category, sentenceExample
            - Categories: formal, casual, slang, business
            - Business examples should use professional language like "Good morning", "Dear colleague", etc.
            
            Example format:
            [{"word":"hello","category":"formal","sentenceExample":"Hello, pleased to meet you."}]
            """;


    public AiService(
            @Qualifier("openAiChatClient") ChatClient openAiChatClient
    ) {
        this.openRouter = openAiChatClient;
        this.converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<WordDTO>>() {}
        );
        this.cache = new HashMap<>();
    }

    public List<WordDTO> getFiveWaysOpenAi(String word) {
        var rawResponse = "";
        try {
            if (!cache.containsKey(word)) {
                log.info("New key added to cache: {} ", word);
                var response = openRouter
                        .prompt()
                        .system(SYSTEM_PROMPT)
                        .user(USER_PROMPT.formatted(word))
                        .call()
                        .entity(converter);
                cache.put(word, response);
                return response;
            }

            var updatedPrompt = improvePrompt(USER_PROMPT, word);

            var result = openRouter
                    .prompt()
                    .system(SYSTEM_PROMPT)
                    .user(updatedPrompt)
                    .call();


            updateCache(word, result.entity(converter));
            rawResponse = result.content();

            return this.cache.get(word);

        } catch (Exception e) {
            var stacktrace = e.getStackTrace();
            var sb = new StringBuilder();
            for (StackTraceElement element : stacktrace) {
                sb.append(element.toString());
                sb.append("\n");
            }
            String response = openRouter
                    .prompt()
                    .system("""
                        You are tech assistant. 
                        User will send stacktrace of errors. 
                        You should explain what's happen< and how to fix it
                        """)
                    .user("%s\n%s".formatted(e.getMessage(), sb.toString()))
                    .call()
                    .content();


            log.error("Failed to parse response OpenRouter:\n {} \n {}", rawResponse, response);
            return  cache.get(word);
        }
    }

    private String improvePrompt(String userPrompt, String word) {
        StringBuilder builder = new StringBuilder(userPrompt.formatted(word));
        builder.append("\nDON'T USE THIS WORDS: ");
        cache.get(word).forEach(data -> builder.append("%s, ".formatted(data.word())));
        var updatedPrompt = builder.toString();
        log.info("PROMPT UPDATED: {}", updatedPrompt);
        return updatedPrompt;
    }

    private void updateCache(String word, List<WordDTO> response) {
        cache.get(word).addAll(response);
        log.info("CACHE UPDATED: \n {}", cache.get(word));
    }

}
