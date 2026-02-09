package dev.lunov.eng.domain.vocabulary;

import dev.lunov.eng.domain.ProficiencyLevel;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a vocabulary item in the user's learning journey, including the word, its translation, definition,
 * part of speech, proficiency level, example sentence, mastery status, synonyms, tags, and review statistics.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VocabularyItem {
        private String word;
        private String transcription;
        private String translation;
        private String definition;
        private String partOfSpeech;
        private ProficiencyLevel level;
        private String exampleSentence;

        @Builder.Default
        private MasteryStatus masteryStatus = MasteryStatus.NEW;

        @Builder.Default
        private List<String> synonyms = new ArrayList<>();

        @Builder.Default
        private List<String> tags = new ArrayList<>();

        @Builder.Default
        private Integer correctAnswers = 0;

        @Builder.Default
        private Integer totalAttempts = 0;

        private Instant addedAt;
        private Instant lastReviewedAt;
        private Instant nextReviewAt; // For spaced repetition

        @Builder.Default
        private Integer repetitionInterval = 1; // Days until next review

        public double getAccuracy() {
                if (totalAttempts == 0) return 0.0;
                return (double) correctAnswers / totalAttempts * 100;
        }

        public void recordAttempt(boolean correct) {
                this.totalAttempts++;
                if (correct) {
                        this.correctAnswers++;
                }
                this.lastReviewedAt = Instant.now();
                updateMasteryStatus();
                updateNextReview(correct);
        }

        private void updateMasteryStatus() {
                double accuracy = getAccuracy();
                if (totalAttempts < 3) {
                        this.masteryStatus = MasteryStatus.NEW;
                } else if (accuracy >= 80 && totalAttempts >= 10) {
                        this.masteryStatus = MasteryStatus.MASTERED;
                } else if (accuracy >= 60) {
                        this.masteryStatus = MasteryStatus.LEARNING;
                } else {
                        this.masteryStatus = MasteryStatus.STRUGGLING;
                }
        }

        private void updateNextReview(boolean correct) {
                if (correct) {
                        // Increase interval (exponential backoff)
                        this.repetitionInterval = Math.min(this.repetitionInterval * 2, 90);
                } else {
                        // Reset to 1 day if incorrect
                        this.repetitionInterval = 1;
                }
                this.nextReviewAt = Instant.now().plusSeconds(repetitionInterval * 86400L);
        }
}
