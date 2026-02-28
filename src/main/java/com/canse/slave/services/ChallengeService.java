package com.canse.slave.services;

import com.canse.slave.api.ApiResponse;
import com.canse.slave.dto.ChallengeGroupDto;
import com.canse.slave.dto.CreateChallengeRequest;

import java.util.List;

public interface ChallengeService {
    ChallengeGroupDto getChallengeById(String currentUsername, Long challengeId);

    void create(String currentUsername, CreateChallengeRequest challengeRequest);

    void delete(String currentUsername, Long challengeId);

    List<ChallengeGroupDto> getChallenges(String currentUsername);

    public ApiResponse<ChallengeGroupDto> updateChallengeGroup(String currentUser, ChallengeGroupDto dto);
}
