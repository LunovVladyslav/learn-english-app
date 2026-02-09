package dev.lunov.eng.domain.vocabulary;

/**
 * Enum representing the mastery status of a vocabulary item in a user's learning journey.
 * This status helps track the user's progress with each word, indicating whether it's new, being learned, struggling with it, or mastered.
 */

public enum MasteryStatus {
    NEW,
    LEARNING,
    STRUGGLING,
    MASTERED
}
