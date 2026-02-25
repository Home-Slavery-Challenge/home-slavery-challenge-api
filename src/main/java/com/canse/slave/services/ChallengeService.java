package com.canse.slave.services;

import com.canse.slave.dto.ChallengeGroupDto;
import com.canse.slave.dto.CreateChallengeRequest;
import com.canse.slave.projections.ChallengeLiteProjection;

import java.util.List;

public interface ChallengeService {
    ChallengeGroupDto getChallengeById(String currentUsername, Long challengeId);

    void create(String currentUsername, CreateChallengeRequest challengeRequest);

    void delete(String currentUsername, Long challengeId);

    List<ChallengeLiteProjection> getLightChallenges(String currentUsername);
}
