package com.canse.slave.entities;

import com.canse.slave.enums.RewardMode;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
public class ChallengeGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Users owner;

    @ManyToMany
    @JoinTable(
            name = "challenge_group_participants",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<Users> participants = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengePeriod> periods = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "challenge_group_tasks",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private List<Task> availableTasks = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "challenge_group_rewards",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "reward_id")
    )
    private List<Reward> rewardPool = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private RewardMode rewardMode;
}
