package dev.lunov.eng.domain.skill;

import dev.lunov.eng.domain.profile.Profile;
import jakarta.persistence.*;
import lombok.*;

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
