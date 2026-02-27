package com.canse.slave.mappers;

import com.canse.slave.dto.ChallengeGroupDto;
import com.canse.slave.dto.RewardDto;
import com.canse.slave.dto.TaskDto;
import com.canse.slave.dto.UserRefDto;
import com.canse.slave.entities.ChallengeGroup;

public class ChallengeGroupMapper {

    public static ChallengeGroupDto toDto(ChallengeGroup c) {
        return new ChallengeGroupDto(
                c.getId(),
                c.getName(),
                new UserRefDto(c.getOwner().getId(), c.getOwner().getUsername()),
                c.getParticipants().stream()
                        .map(u -> new UserRefDto(u.getId(), u.getUsername()))
                        .toList(),
                c.getAvailableTasks().stream()
                        .map(t -> new TaskDto(t.getId(), t.getName(), t.getDefaultsPoints()))
                        .toList(),
                c.getRewardPool().stream()
                        .map(r -> new RewardDto(r.getId(), r.getName(), r.getDescription()))
                        .toList(),
                c.getRewardMode(),
                c.getRecurringReward()
        );
    }
}