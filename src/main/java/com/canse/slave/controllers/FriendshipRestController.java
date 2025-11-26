package com.canse.slave.controllers;

import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.User;
import com.canse.slave.services.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendship")
@CrossOrigin(origins = "*")
public class FriendshipRestController {

    @Autowired
    FriendshipService friendshipService;

    @GetMapping("/search-by-name/{query}")
    public List<User>  getUsersByName(@PathVariable String query,@AuthenticationPrincipal String currentUsername){
        return friendshipService.searchUsersByName(query, currentUsername);
    }

    @PostMapping("/create/{targetId}")
    public Friendship createFriendship(@PathVariable Long targetId,@AuthenticationPrincipal String currentUsername){
        return friendshipService.sendFriendRequest(currentUsername, targetId);
    }

    @PostMapping("/accept/{friendshipId}")
    public Friendship acceptFriendship(@PathVariable Long friendshipId){
        return friendshipService.acceptRequest(friendshipId);
    }

    @PostMapping("/decline/{friendshipId}")
    public void declineFriendshipRequest(@PathVariable Long friendshipId){
        friendshipService.declineRequest(friendshipId);
    }

    @PostMapping("/block/{friendshipId}")
    public Friendship blockFriendship(@PathVariable Long friendshipId){
        return friendshipService.blockUser(friendshipId);
    }

    @GetMapping("/pending-received")
    public List<Friendship> getPendingReceived(@AuthenticationPrincipal String currentUsername){
        return friendshipService.getPendingReceivedRequests(currentUsername);
    }

    @GetMapping("/pending-sent")
    public List<Friendship> getPendingSent(@AuthenticationPrincipal String currentUsername){
        return friendshipService.getPendingSentRequests(currentUsername);
    }

    @PostMapping("/check/{friendshipId}")
    public Friendship getPendingSent(@PathVariable Long friendshipId){
        return friendshipService.markAsChecked(friendshipId);
    }

    @GetMapping("/friends")
    public List<User> getFriends(@AuthenticationPrincipal String currentUsername){
        return friendshipService.getFriends(currentUsername);
    }

    @DeleteMapping("{friendshipId}")
    public void deleteFriendship(@PathVariable Long friendshipId){
        friendshipService.removeFriend(friendshipId);
    }



}
