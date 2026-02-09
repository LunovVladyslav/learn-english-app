package dev.lunov.eng.domain.vocabulary;

import dev.lunov.eng.domain.ProficiencyLevel;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a user's vocabulary collection, containing a list of vocabulary items and associated metadata.
 */

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

    /**
     * Adds a new vocabulary item to the user's collection and sets the initial review schedule.
     * @param word The vocabulary item to add
     */
    public void addWord(VocabularyItem word) {
        word.setAddedAt(Instant.now());
        word.setNextReviewAt(Instant.now().plusSeconds(86400)); // Review tomorrow
        this.words.add(word);
        this.updatedAt = Instant.now();
    }

    /**
     * Removes a vocabulary item from the user's collection by word.
     * @param word The word to remove
     * @return true if the word was found and removed, false otherwise
     */
    public boolean removeWord(String word) {
        boolean removed = this.words.removeIf(w -> w.getWord().equalsIgnoreCase(word));
        if (removed) {
            this.updatedAt = Instant.now();
        }
        return removed;
    }

    /**
     * Finds a vocabulary item by word.
     * @param word The word to find
     * @return The VocabularyItem if found, or null if not found
     */
    public VocabularyItem findWord(String word) {
        return this.words.stream()
                .filter(w -> w.getWord().equalsIgnoreCase(word))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a list of vocabulary items that are due for review based on their next review time.
     * @return List of VocabularyItem objects that are due for review
     */
    public List<VocabularyItem> getWordsDueForReview() {
        Instant now = Instant.now();
        return this.words.stream()
                .filter(w -> w.getNextReviewAt() != null && w.getNextReviewAt().isBefore(now))
                .toList();
    }

    /**
     * Retrieves a list of vocabulary items filtered by their mastery status.
     * @param status The MasteryStatus to filter by
     * @return List of VocabularyItem objects that match the specified mastery status
     */
    public List<VocabularyItem> getWordsByMasteryStatus(MasteryStatus status) {
        return this.words.stream()
                .filter(w -> w.getMasteryStatus() == status)
                .toList();
    }

    /**
     * Retrieves a list of vocabulary items filtered by their proficiency level.
     * @param level The ProficiencyLevel to filter by
     * @return List of VocabularyItem objects that match the specified proficiency level
     */
    public List<VocabularyItem> getWordsByLevel(ProficiencyLevel level) {
        return this.words.stream()
                .filter(w -> w.getLevel() == level)
                .toList();
    }


}
