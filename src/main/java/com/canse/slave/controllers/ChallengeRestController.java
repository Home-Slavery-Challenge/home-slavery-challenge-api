package com.canse.slave.controllers;

import com.canse.slave.dto.CreateChallengeRequest;
import com.canse.slave.entities.ChallengeGroup;
import com.canse.slave.services.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/challenge")
@CrossOrigin(origins = "*")
public class ChallengeRestController {

    @Autowired
    ChallengeService challengeService;

    @PostMapping("/")
    public ResponseEntity<Void> createChallenge(@AuthenticationPrincipal String currentUsername, @RequestBody CreateChallengeRequest challengeRequest) {
        this.challengeService.create(currentUsername,challengeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/")
    public List<ChallengeGroup> getLightChallenges(@AuthenticationPrincipal String currentUsername){
        return challengeService.getLightChallenges(currentUsername);
    }

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(@AuthenticationPrincipal String currentUsername,@PathVariable Long challengeId){
        this.challengeService.delete(currentUsername,challengeId);
        return ResponseEntity.noContent().build();    }

}
