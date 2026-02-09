package dev.lunov.eng.domain.quiz;

import dev.lunov.eng.domain.ProficiencyLevel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document()
public class Quiz {
    @Id
    private String id;
    private String title;
    private UUID userId;
    private Task task;
    @Enumerated(EnumType.STRING)
    private DifficultType difficultType;
    @Enumerated(EnumType.STRING)
    private ProficiencyLevel level;
    private List<Answer> answers;
    private Boolean solved = false;
    @CreationTimestamp
    private Instant startTime;
    private Instant endTime;

    record Answer(
            String answer,
            Boolean isCorrect
    ) {
    }

    record Task(
            String task,
            String hint
    ) {
    }

    @Getter
    enum DifficultType {
        EASY(0.5), MEDIUM(1.0), HARD(1.5);
        private final double difficult;
        DifficultType(double difficult) {
            this.difficult = difficult;
        }
    }

    public Double getQuizPoints() {
        return getRandomBasePoints() * level.getLevelBonus() * difficultType.getDifficult() * getTimeBonus();
    }

    private double getRandomBasePoints() {
        Random random  = new SecureRandom();
        return random.nextDouble(level.getBasePointsMin(), level.getBasePointsMax());
    }

    private double getTimeBonus() {
        if (!this.solved || this.endTime == null) {
            return 0.0;
        }
        long durationSeconds = MILLISECONDS.toSeconds(
                this.endTime.toEpochMilli() - this.startTime.toEpochMilli()
        );

        double maxBonus = 2.0;
        double minBonus = 0.5;
        long maxSeconds = MINUTES.toSeconds(5);

        double bonus = maxBonus - ((maxBonus - minBonus) * ((double) durationSeconds / maxSeconds));

        return Math.max(minBonus, Math.min(maxBonus, bonus));
    }
}
