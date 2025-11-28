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

    @PostMapping("/decline/{friendshipId}")
    public void declineFriendshipRequest(@PathVariable Long friendshipId) {
        friendshipService.declineRequest(friendshipId);
    }

    @PostMapping("/block-user/{friendshipId}")
    public Friendship unblockUser(@PathVariable Long friendshipId, @AuthenticationPrincipal String currentUser) {
        return friendshipService.blockUser(friendshipId, currentUser);
    }

    @PostMapping("/block-friendship/{friendshipId}")
    public void blockFriendship(@PathVariable Long friendshipId, @AuthenticationPrincipal String currentUser) {
        friendshipService.blockFriendship(friendshipId, currentUser);
    }


    @PostMapping("/unblock-user/{friendshipId}")
    public void unblockUser(@PathVariable Long friendshipId) {
        friendshipService.unblockUser(friendshipId);
    }

    @GetMapping("/pending-received")
    public List<Friendship> getPendingReceived(@AuthenticationPrincipal String currentUsername) {
        return friendshipService.getPendingReceivedRequests(currentUsername);
    }

    @GetMapping("/pending-sent")
    public List<Friendship> getPendingSent(@AuthenticationPrincipal String currentUsername) {
        return friendshipService.getPendingSentRequests(currentUsername);
    }

    @PostMapping("/check/{friendshipId}")
    public Friendship markAsChecked(@PathVariable Long friendshipId) {
        return friendshipService.markAsChecked(friendshipId);
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
