package com.canse.slave.services;

import com.canse.slave.dto.CreateChallengeRequest;
import com.canse.slave.dto.UserSummaryDto;
import com.canse.slave.entities.ChallengeGroup;
import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.Users;

import java.util.List;

public interface ChallengeService {
    void createChallenge(String currentUsername,CreateChallengeRequest challengeRequest);
    List<ChallengeGroup> getLightChallenges(String currentUsername);
}
