package com.canse.slave.controllers;

import com.canse.slave.dto.UserSummaryDto;
import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.Users;
import com.canse.slave.projections.FriendshipLiteProjection;
import com.canse.slave.services.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendship")
@CrossOrigin(origins = "*")
public class FriendshipRestController {

    @Autowired
    FriendshipService friendshipService;

    @GetMapping("/search-by-name/{query}")
    public List<UserSummaryDto> getUsersByName(@PathVariable String query, @AuthenticationPrincipal String currentUsername) {
        return friendshipService.searchUsersByName(query, currentUsername);
    }

    @PostMapping("/create/{targetId}")
    public ResponseEntity<Void> createFriendship(@PathVariable Long targetId, @AuthenticationPrincipal String currentUsername) {
        friendshipService.sendFriendRequest(currentUsername, targetId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/accept/{friendshipId}")
    public ResponseEntity<Void> acceptFriendship(@PathVariable Long friendshipId, @AuthenticationPrincipal String currentUser) {
        friendshipService.acceptAndNormalizeFriendship(currentUser, friendshipId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/decline-request/{friendshipId}")
    public void declineFriendshipRequest(@PathVariable Long friendshipId) {
        friendshipService.declinePendingRequest(friendshipId);
    }

    @PostMapping("/decline-friendship/{userIdTarget}")
    public void declineFriendshipRequest(@PathVariable Long userIdTarget, @AuthenticationPrincipal String currentUser) {
        friendshipService.declineFriendship(userIdTarget,currentUser);
    }

    @PostMapping("/block-user/{friendshipId}")
    public ResponseEntity<Void> blockUser(@PathVariable Long friendshipId, @AuthenticationPrincipal String currentUser) {
        friendshipService.blockUser(friendshipId, currentUser);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/block-friendship/{friendshipId}")
    public ResponseEntity<Void> blockFriendship(@PathVariable Long friendshipId, @AuthenticationPrincipal String currentUser) {
        friendshipService.blockFriendship(friendshipId, currentUser);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


    @PostMapping("/unblock-user/{userIdReceiver}")
    public void unblockUser(@PathVariable Long userIdReceiver, @AuthenticationPrincipal String currentUser) {
        friendshipService.unblockUser(userIdReceiver,currentUser);
    }

    @GetMapping("/pending-received")
    public List<Friendship> getPendingReceived(@AuthenticationPrincipal String currentUsername) {
        return friendshipService.getPendingReceivedRequests(currentUsername);
    }

    @GetMapping("/pending-sent")
    public List<FriendshipLiteProjection> getPendingSent(@AuthenticationPrincipal String currentUsername) {
        return friendshipService.getPendingSentRequests(currentUsername);
    }

    @PostMapping("/check")
    public void markAsChecked(@AuthenticationPrincipal String currentUsername) {
        friendshipService.markAsChecked(currentUsername);
    }

    @GetMapping("/friends")
    public List<UserSummaryDto> getSummaryFriends(@AuthenticationPrincipal String currentUsername) {
        return friendshipService.getSummaryFriends(currentUsername);
    }

    @GetMapping("/blocked")
    public List<Users> getBlocked(@AuthenticationPrincipal String currentUsername) {
        return friendshipService.getBlocked(currentUsername);
    }

    @DeleteMapping("{friendshipId}")
    public void deleteFriendship(@PathVariable Long friendshipId) {
        friendshipService.removeFriend(friendshipId);
    }

}
