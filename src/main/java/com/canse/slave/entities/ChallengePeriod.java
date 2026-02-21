package com.canse.slave.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
public class ChallengePeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @ManyToOne
    @JoinColumn(name = "challenge_group_id")
    private ChallengeGroup group;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "period", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeDay> days;
    @ManyToOne
    @JoinColumn(name = "reward_id")
    private Reward reward;
    private Boolean rewardHonored= false;
    @ManyToOne(optional = true)
    @JoinColumn(name = "winner_id", nullable = true)
    private Users winner;
}
