package com.canse.slave.dto;

import com.canse.slave.enums.RewardMode;

import java.util.List;

public record ChallengeGroupDto(Long id,
                                String name,
                                UserRefDto owner,
                                List<UserRefDto> participants,
                                List<TaskDto> availableTasks,
                                List<RewardDto> rewardPool,
                                RewardMode rewardMode) {
}
