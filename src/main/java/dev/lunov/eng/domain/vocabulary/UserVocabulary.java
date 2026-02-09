package dev.lunov.eng.domain.vocabulary;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user_vocabularies")
@CompoundIndex(name = "user_word_idx", def = "{'userId': 1, 'words.word': 1}")
public class UserVocabulary {
    @Id
    private String id;

    @Indexed(unique = true)
    private UUID userId;

    @Builder.Default
    private List<VocabularyItem> words = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
}
