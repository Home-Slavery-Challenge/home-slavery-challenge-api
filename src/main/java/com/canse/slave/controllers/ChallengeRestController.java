package com.canse.slave.controllers;

import com.canse.slave.api.ApiResponse;
import com.canse.slave.dto.ChallengeGroupDto;
import com.canse.slave.dto.CreateChallengeRequest;
import com.canse.slave.services.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Actuellement, certaines entités (comme les tâches, récompenses et participants) sont liées par des relations
//  directes, ce qui pose problème pour l’historique. Si on modifie ou supprime une tâche ou un participant à l’avenir,
//  cela risque d’altérer les périodes ou journées de challenge passées. Pour garantir un historique immuable, nous
//  devons remplacer ces relations par des données figées (par exemple, stocker le nom, les points ou les identifiants
//  sous forme de chaînes ou de snapshots). Il faut réfléchir et planifier cette évolution pour historiser les éléments
//  sans impact lors des futures modifications. Une tâche dédiée à cette refonte est à créer.

@RestController
@RequestMapping("/challenge")
@CrossOrigin(origins = "*")
public class ChallengeRestController {

    @Autowired
    ChallengeService challengeService;

    @PostMapping("/")
    public ResponseEntity<Void> createChallenge(@AuthenticationPrincipal String currentUsername, @RequestBody CreateChallengeRequest challengeRequest) {
        this.challengeService.create(currentUsername, challengeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/")
    public List<ChallengeGroupDto> getChallenges(@AuthenticationPrincipal String currentUsername) {
        return challengeService.getChallenges(currentUsername);
    }

    @GetMapping("/{id}")
    public ChallengeGroupDto getChallenge(@AuthenticationPrincipal String currentUsername, @PathVariable Long id) {
        return challengeService.getChallengeById(currentUsername, id);
    }

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(@AuthenticationPrincipal String currentUsername, @PathVariable Long challengeId) {
        this.challengeService.delete(currentUsername, challengeId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/")
    public ApiResponse<ChallengeGroupDto> updateChallenge(@AuthenticationPrincipal String currentUser, @RequestBody ChallengeGroupDto challenge) {
        return challengeService.updateChallengeGroup(currentUser, challenge);
    }

}
