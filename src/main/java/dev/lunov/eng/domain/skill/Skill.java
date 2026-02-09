package dev.lunov.eng.domain.skill;

import dev.lunov.eng.domain.profile.Profile;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a specific skill within a user's language learning profile, such as listening, speaking, reading, writing, grammar, or vocabulary.
 * Each skill can have notes and an improving strategy to help the user focus on areas that need improvement.
 */

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skills")
public class Skill{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SkillType type;
    private String notes;
    private String improvingStrategy;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
}
