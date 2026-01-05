package com.canse.slave.controllers;

import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.Users;
import com.canse.slave.services.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Users> getUsersByName(@PathVariable String query, @AuthenticationPrincipal String currentUsername) {
        return friendshipService.searchUsersByName(query, currentUsername);
    }

    @PostMapping("/create/{targetId}")
    public Friendship createFriendship(@PathVariable Long targetId, @AuthenticationPrincipal String currentUsername) {
        return friendshipService.sendFriendRequest(currentUsername, targetId);
    }

    @PostMapping("/accept/{friendshipId}")
    public Friendship acceptFriendship(@PathVariable Long friendshipId, @AuthenticationPrincipal String currentUser) {
        return friendshipService.acceptAndNormalizeFriendship(currentUser, friendshipId);
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
    public Friendship blockUser(@PathVariable Long friendshipId, @AuthenticationPrincipal String currentUser) {
        return friendshipService.blockUser(friendshipId, currentUser);
    }

    @PostMapping("/block-friendship/{friendshipId}")
    public void blockFriendship(@PathVariable Long friendshipId, @AuthenticationPrincipal String currentUser) {
        friendshipService.blockFriendship(friendshipId, currentUser);
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
    public List<Friendship> getPendingSent(@AuthenticationPrincipal String currentUsername) {
        return friendshipService.getPendingSentRequests(currentUsername);
    }

    @PostMapping("/check")
    public void markAsChecked(@AuthenticationPrincipal String currentUsername) {
        friendshipService.markAsChecked(currentUsername);
    }

    @GetMapping("/friends")
    public List<Users> getFriends(@AuthenticationPrincipal String currentUsername) {
        return friendshipService.getFriends(currentUsername);
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
