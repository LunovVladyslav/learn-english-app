package dev.lunov.eng.domain.profile;

import dev.lunov.eng.domain.ProficiencyLevel;
import dev.lunov.eng.domain.skill.Skill;
import dev.lunov.eng.security.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "profiles",
        indexes = {
                @Index(name = "idx_profile_user_id", columnList = "user_id")
        }
)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Double experience = 0.0;

    @Column(nullable = false)
    private String nativeLanguage;

    @Enumerated(EnumType.STRING)
    private ProficiencyLevel proficiency;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    /**
     * Updates experience and automatically levels up if threshold is reached
     * @param points Points to add to experience
     * @return The new total experience
     */
    public Double updateExperience(double points) {
        this.experience += points;
        updateProficiencyLevel();
        return this.experience;
    }

    /**
     * Checks if user should level up and updates proficiency accordingly
     */
    private void updateProficiencyLevel() {
        ProficiencyLevel newLevel = calculateProficiencyLevel(this.experience);
        if (newLevel != this.proficiency) {
            this.proficiency = newLevel;
        }
    }

     /** Determines the proficiency level based on total experience
     * @param experience Total experience points
     * @return Appropriate proficiency level
     */
    private ProficiencyLevel calculateProficiencyLevel(double experience) {
        ProficiencyLevel[] levels = ProficiencyLevel.values();

        for (int i = levels.length - 1; i >= 0; i--) {
            if (experience >= levels[i].getLevelPoints()) {
                return levels[i];
            }
        }

        return ProficiencyLevel.A1;
    }


    /**
     * Gets experience needed for next level
     * @return Points needed, or 0 if at max level
     */
    public double getExperienceToNextLevel() {
        ProficiencyLevel[] levels = ProficiencyLevel.values();

        int currentIndex = -1;
        for (int i = 0; i < levels.length; i++) {
            if (levels[i] == this.proficiency) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == levels.length - 1) {
            return 0.0;
        }

        ProficiencyLevel nextLevel = levels[currentIndex + 1];
        return nextLevel.getLevelPoints() - this.experience;
    }

    /**
     * Gets progress percentage to next level (0.0 to 1.0)
     * @return Progress as a decimal (e.g., 0.75 = 75%)
     */
    public double getProgressToNextLevel() {
        ProficiencyLevel[] levels = ProficiencyLevel.values();

        int currentIndex = -1;
        for (int i = 0; i < levels.length; i++) {
            if (levels[i] == this.proficiency) {
                currentIndex = i;
                break;
            }
        }

        // If at max level, return 1.0 (100%)
        if (currentIndex == levels.length - 1) {
            return 1.0;
        }

        double currentLevelPoints = this.proficiency.getLevelPoints();
        double nextLevelPoints = levels[currentIndex + 1].getLevelPoints();
        double experienceInCurrentLevel = this.experience - currentLevelPoints;
        double pointsNeededForNextLevel = nextLevelPoints - currentLevelPoints;

        return experienceInCurrentLevel / pointsNeededForNextLevel;
    }
}
