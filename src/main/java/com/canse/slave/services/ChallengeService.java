package com.canse.slave.services;

import com.canse.slave.dto.CreateChallengeRequest;
import com.canse.slave.entities.ChallengeGroup;

import java.util.List;

public interface ChallengeService {
    void create(String currentUsername, CreateChallengeRequest challengeRequest);
    void delete(String currentUsername,Long challengeId);
    List<ChallengeGroup> getLightChallenges(String currentUsername);
}
