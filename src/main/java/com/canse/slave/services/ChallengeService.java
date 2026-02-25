package com.canse.slave.services;

import com.canse.slave.entities.ChallengeGroup;
import com.canse.slave.projections.ChallengeLiteProjection;
import com.canse.slave.dto.CreateChallengeRequest;

import java.util.List;

public interface ChallengeService {
    ChallengeGroup getChallengeById(String currentUsername, Long challengeId);
    void create(String currentUsername, CreateChallengeRequest challengeRequest);
    void delete(String currentUsername,Long challengeId);
    List<ChallengeLiteProjection> getLightChallenges(String currentUsername);
}
