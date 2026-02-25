package com.canse.slave.services;

import com.canse.slave.dto.ChallengeGroupDto;
import com.canse.slave.dto.CreateChallengeRequest;
import com.canse.slave.entities.ChallengeGroup;
import com.canse.slave.entities.Reward;
import com.canse.slave.entities.Task;
import com.canse.slave.entities.Users;
import com.canse.slave.enums.RewardMode;
import com.canse.slave.mappers.ChallengeGroupMapper;
import com.canse.slave.projections.ChallengeLiteProjection;
import com.canse.slave.repos.ChallengeGroupRepository;
import com.canse.slave.repos.RewardRepository;
import com.canse.slave.repos.TaskRepository;
import com.canse.slave.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.springframework.http.HttpStatus.*;


@Transactional
@Service
public class ChallengeServiceImpl implements ChallengeService {

    @Autowired
    private ChallengeGroupRepository challengeGroupRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private RewardRepository rewardRepository;

    @Override
    public ChallengeGroupDto getChallengeById(String currentUsername, Long challengeId) {

        ChallengeGroup c = challengeGroupRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Challenge not found"));

        Users u = Optional.ofNullable(userRepository.findByUsername(currentUsername))
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));

        if (!c.getOwner().getId().equals(u.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "No access to this challenge");
        }

        return ChallengeGroupMapper.toDto(c);
    }

    @Override
    public void create(String currentUsername, CreateChallengeRequest req) {

        // --- AUTH ---
        if (currentUsername == null || currentUsername.isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "User not authenticated");
        }

        Users owner = userRepository.findByUsername(currentUsername);
        if (owner == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "User not found");
        }

        // --- NAME ---
        String name = req.name();
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Challenge name is required");
        }

        ChallengeGroup group = new ChallengeGroup();
        group.setOwner(owner);
        group.setName(name.trim());

        // --- PARTICIPANTS ---
        Long[] participantIdsRaw = req.participants();
        List<Long> participantIds = participantIdsRaw == null ? List.of() :
                Arrays.stream(participantIdsRaw)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

        List<Users> participants = new ArrayList<>();

        if (!participantIds.isEmpty()) {
            List<Users> found = userRepository.findAllById(participantIds);

            if (found.size() != participantIds.size()) {
                throw new ResponseStatusException(NOT_FOUND, "One or more participants not found");
            }

            participants.addAll(found);
        }


        // Owner included
        participants.add(owner);

        group.setParticipants(participants);

        // --- REWARDS ---
        String[] rewardsRaw = req.rewards() == null ? new String[0] : req.rewards();
        List<Reward> rewards = Arrays.stream(rewardsRaw)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(r -> {
                    Reward reward = new Reward();
                    reward.setName(r);
                    reward.setDescription(r);
                    return reward;
                })
                .toList();

        rewards = rewardRepository.saveAll(rewards);
        group.setRewardPool(rewards);

        // --- TASKS ---
        String[] tasksRaw = req.tasks() == null ? new String[0] : req.tasks();
        List<Task> tasks = Arrays.stream(tasksRaw)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(t -> {
                    Task task = new Task();
                    task.setName(t);
                    return task;
                })
                .toList();

        tasks = taskRepository.saveAll(tasks);
        group.setAvailableTasks(tasks);

        // --- MODE ---
        group.setRewardMode(RewardMode.RANDOM);

        // --- SAVE ---
        challengeGroupRepository.save(group);

    }

    @Override
    public void delete(String currentUsername, Long challengeId) {
        Users user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        ChallengeGroup challenge = challengeGroupRepository.findById(challengeId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Challenge not found"));
        if (!Objects.equals(challenge.getOwner().getId(), user.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "No authorized to delete");
        }
        challengeGroupRepository.delete(challenge);
    }

    @Override
    public List<ChallengeLiteProjection> getLightChallenges(String currentUsername) {
        Users user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "User not found");
        }
        return challengeGroupRepository.findByParticipants_Id(user.getId());
    }
}
