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

    @GetMapping("/{query}")
    public List<User>  getUsersByName(@PathVariable String query,@AuthenticationPrincipal String currentUsername){
        return friendshipService.searchUsersByName(query, currentUsername);
    }

}
