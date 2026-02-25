package com.canse.slave.repos;

import com.canse.slave.projections.ChallengeLiteProjection;
import com.canse.slave.entities.ChallengeGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeGroupRepository extends JpaRepository<ChallengeGroup, Long> {
    List<ChallengeGroup> findByParticipants_Id(Long userId);
}
